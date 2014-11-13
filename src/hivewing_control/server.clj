(ns hivewing-control.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [digest :as digest]
            [msgpack.core :as msgpack]
            [clojure.data.json :as json]
            [org.httpkit.server :refer :all]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
            [hivewing-control.authentication :refer [worker-authenticated?]]
            [hivewing-core.worker-config :as core-worker-config]
            [clojure.string :refer [split trim lower-case]]))

(defn binary?
  "Depending on the request parameters, we may send back JSON or MsgPack"
  [request]
  (not (get-in request [:params :json])))

(defn create-update-message
  "You create the update message.  It is really just giving it the right fields"
  ([]  (create-update-message {}))
  ([parameters] {"update" parameters}))

(defn- create-status-message-field
  "Create the field for a status message"
  [key-val]
  (let [[key val] key-val]
    [key (digest/md5 val)]))

(defn create-status-message
  "Creates a status message for the client.  This is in a hash-map form"
  [config-keys]
  {"status" (into {} (map create-status-message-field  config-keys))})

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
  [worker-uuid worker-config updates]
  worker-config
  )

(defn process-message
  [worker-uuid [command data :as kv-pair]]
  (let [worker-config (core-worker-config/worker-config-get worker-uuid)]
    (case command
      "update" (create-status-message (update-worker-config worker-uuid worker-config data)); Update the values, then reply with the status message
      "status" {}
      )))

(defn process-messages
  [request incoming-message]
  ; Processing a message coming in.
  ; Need to process each of the messages in the incoming message body (there could be lots, but... )
  ; We look up the worker config for each
  (let [worker-uuid   (get request :basic-authentication) ]
    (map #(process-message worker-uuid %) incoming-message)))

(process-messages {} {"update" {"temperature.temp" 90 "temperature.frequency" 1}})

(defn worker-control-handler [request]
  (with-channel request channel
    (println (str "Connecting from device " (:worker-uuid (:params request))))
    (println request)

    ; Upon connection we send over a single update message - empty.
    ; This prompts the recipient to reply with a status message
    (send! channel (pack-message request (create-update-message)))
    (println "Sent initial blank update message")

    (on-close channel (fn [status] (println "channel closed: " status)))
    (on-receive channel (fn [data] ;; echo it back
                          (let [decoded-message (unpack-message   request data) ; Decoding the message
                                responses        (process-messages request decoded-message)] ; Process it and get a set of replies
                            ; If the response has something to say - we send it back. Otherwise, don't.
                            (doseq [reply responses]
                              (send! channel (pack-message request reply)))))) ; Send it !
    ))

(defroutes app-routes
  (GET "/" [] worker-control-handler)
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-basic-authentication worker-authenticated?)
      handler/site))
