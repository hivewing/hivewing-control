(ns hivewing-control.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [taoensso.timbre :as logger]
            [compojure.handler :as handler]
            [digest :as digest]
            [msgpack.core :as msgpack]
            [clojure.data.json :as json]
            [org.httpkit.server :refer :all]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
            [hivewing-control.authentication :refer [worker-authenticated?]]
            [hivewing-core.worker :as core-worker]
            [hivewing-core.worker-config :as core-worker-config]
            [hivewing-core.worker-events :as core-worker-events]
            [hivewing-core.worker-data :as core-worker-data]
            [hivewing-core.hive-logs :as core-hive-logs]
            [hivewing-core.pubsub :as core-pubsub]
            [clojure.data :as clojure-data]
            [clojure.string :refer [split trim lower-case]]))

(defn binary?
  "Depending on the request parameters, we may send back JSON or MsgPack"
  [request]
  (not (get-in request [:params :json])))

(defn create-events-message
  "You create the event message.  It is really just giving it the right fields"
  ([]  (create-events-message {}))
  ([events] {"event" events}))

(defn create-update-message
  "You create the update message.  It is really just giving it the right fields"
  ([]  (create-update-message {}))
  ([parameters] {"update" parameters}))

(defn create-status-message-field
  "Create the field for a status message"
  [key-val]
  (let [[key val] key-val]
    [key (digest/md5 val)]))


(defn create-status-message
  "Creates a status message for the client.  This is in a hash-map form"
  [config-keys]
  (logger/info "Config status message calc - config-keys" config-keys)
  (let [skeys (sort-by name (keys config-keys))
        kvs (map #(list  %1 (config-keys %1)) skeys)
        ins (reduce concat () kvs)
        sha-str (map #(byte-array (map byte (name %1))) ins)]
    (logger/info "Calculating status hash for " skeys)
    {"status" {"hash" (digest/sha-256 sha-str)}}))

(defn unpack-message
  "Unpacks a message with the correct style of encoding (depending on the request)"
  [request bytes]
  (if (binary? request)
    (msgpack/unpack bytes)
    (json/read-str bytes)))

(defn pack-message
  "Packs a message with the correct style of encoding (depending on the request)"
  [request message]
  (if (binary? request)
    (msgpack/pack message)
    (json/write-str message)))

(defn update-worker-config
  "Given the worker-uuid, and the updates. Apply the config changes
  to the worker-config repo. ANd then return the current *full*
  config hashmap"
  [worker-uuid updates]
  ; Incoming worker config
  ; Set it, and the return is the complete config.
  (do
    (logger/info "Updating worker config: " worker-uuid updates)
    (core-worker-config/worker-config-set worker-uuid updates :suppress-change-publication true :allow-system-keys true)
    (let [new-config (core-worker-config/worker-config-get worker-uuid :include-system-keys true)]
      (logger/info "Updated to worker config: " worker-uuid new-config)
      new-config)))


(defn process-status-message
  "An incoming status message describes the state of the other side's config data.
  It has single sha-256 of all the config data. Returns either the entire config
  if status does not match or an empty hash if the status is up-to-date."
  [worker-uuid {worker-status "hash"}]
  (let [
        ;get the existin configuration
        worker-config  (core-worker-config/worker-config-get worker-uuid :include-system-keys true)
        ; Then figure out the "status"
        current-status (get-in (create-status-message worker-config) ["status" "hash"])]
    (logger/info "Processing status... Worker: " worker-status " ? " current-status)
    (if (= current-status worker-status)
      {}
      {"set" worker-config})))

(defn process-data-message
  "The worker has passed us a data value and we should store it"
  [worker-uuid data-hash]
  (core-worker-data/worker-data-store worker-uuid (flatten (into [] data-hash)))
  ;; return an empty collection
  ())

(defn process-log-message
  "The worker sent up some log data. Save it!
  The format is { 'task' => <log message> }
  If you send a blank task name, it is a 'system' task."
  [hive-uuid worker-uuid command-data]
    (doseq [[task-name message] command-data]
      (logger/info "Storing log message from worker " worker-uuid)
      (logger/info "task: " task-name " message " message)
      (core-hive-logs/hive-logs-push hive-uuid
                                     worker-uuid
                                     (if (empty? task-name) nil task-name)
                                     message))
    ;; Return an empty collection
    ())

(defn process-message
  [request [command command-data :as kv-pair]]

  (let [auth  (get request :basic-authentication)
        worker-uuid   (:uuid auth)
        hive-uuid     (:hive_uuid auth)]
    (try
      (let [res (case command
                  ;; When we are sent an update message.
                  ;; We should update those keys which are pushed to us
                  ;; And then send down a status message of all of our content.
                  ;; Update the values, then reply with the status message
                  "update" (do
                             ;; Update it
                             (update-worker-config worker-uuid command-data)
                             ;; Now get all the config!
                             (create-status-message (core-worker-config/worker-config-get worker-uuid)))
                  ;; When we receive a status message
                  ;; We look at it, and if there are things that are not up-to-date
                  ;; we will create an update message to send to the client.
                  ;; if not we send a nil (which means don't send anything)
                  "status" (process-status-message worker-uuid command-data)

                  ;; When we get data from the worker we store it.
                  "data" (process-data-message worker-uuid command-data)

                  "log" (process-log-message hive-uuid worker-uuid command-data)
                  nil
                  )]

        (cond (nil? res) (logger/warn "unknown command:" command)
              (seq res) (logger/info "process-message result:" command res))
        res)
      (catch Exception e (logger/error (str "Exception: " e))))))

(defn process-messages
  "Each message coming in needs to be processed, and then the
   response should be created.
   Any responses returned will be sent (even if nil, etc)
   So we remove nil and blank messages before returning"
  [request incoming-message]
  ; Processing a message coming in.
  ; Need to process each of the messages in the incoming message body (there could be lots, but... )
  ; We look up the worker config for each
  (remove #(or (nil? %) (empty? %))
      (map #(process-message request %) incoming-message)))

(defn worker-control-handler [request]
  (with-channel request channel
    (let [worker-uuid   (:uuid (get request :basic-authentication))
          hive-uuid     (:hive_uuid (get request :basic-authentication))

          worker-change-listener (core-pubsub/subscribe-message
                                   ; This is the worker config updates channel handler
                                   (core-worker-config/worker-config-updates-channel worker-uuid)
                                   (fn [chan-string changes]
                                      ; When there are changes, we just ship them out to the
                                      ; cilent as an update message
                                      (send! channel (pack-message request (create-update-message changes))))

                                   ; This is the channel that events are pushed to the workers
                                   (core-worker-events/worker-events-channel worker-uuid)
                                   (fn [chan-string events]
                                      ; When there are events, we just ship them out to the
                                      ; cilent as an event message
                                      (send! channel (pack-message request (create-events-message events))))
                                   )]

    (core-hive-logs/hive-logs-push hive-uuid worker-uuid nil "Connected to control server")

    (logger/info (str "Connecting from device " (:worker-uuid (:params request))))
    ; Upon connection we send over a single update message - empty.
    ; This prompts the recipient to reply with a status message
    (send! channel (pack-message request (create-update-message)))
    (logger/info "Sent initial blank update message")

    (on-close channel (fn [status] (do
                                    (logger/info "channel closed: " status)
                                    (core-pubsub/unsubscribe worker-change-listener)
                                    (logger/info "listener closed.")
                                    )))
    (on-receive channel (fn [data]
                          (let [decoded-message (unpack-message   request data) ; Decoding the message
                                responses       (process-messages request decoded-message)] ; Process it and get a set of replies
                            ; If the response has something to say - we send it back. Otherwise, don't.
                            (doseq [reply responses]
                              (send! channel (pack-message request reply)))))) ; Send it !
    )))

(defroutes app-routes
  (GET "/" [] worker-control-handler)
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-basic-authentication worker-authenticated?)
      handler/site))
