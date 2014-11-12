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
            [clojure.string :refer [split trim lower-case]]))

(def binary?
  false)

(defn create-update-message
  ([]  (create-update-message {}))
  ([parameters] {"update" parameters}))

(defn- create-status-message-field
  [key-val]
  (let [[key val] key-val]
    [key (digest/md5 val)]))

(defn create-status-message
  [config-keys]
  (into {} (map create-status-message-field  config-keys)))

(defn unpack-message
  [bytes]
  (if binary?
    (msgpack/unpack bytes)
    (json/read-str bytes)))

(defn pack-message
  [message]
  (if binary?
    (msgpack/pack message)
    (json/write-str message)))

(defn worker-control-handler [request]
  (with-channel request channel
    (println (str "Connecting from device " (:worker-uuid (:params request))))
    (println request)
    ; we set our handlers


    ; Upon connection we send over a single update message - empty.
    ; This prompts the recipient to reply with a status message
    (send! channel (pack-message (create-update-message)))
    (println "Sent initial blank update message")

    (on-close channel (fn [status] (println "channel closed: " status)))
    (on-receive channel (fn [data] ;; echo it back
      (send! channel data)))

    ))


(defroutes app-routes
  (GET "/" [] worker-control-handler)
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-basic-authentication worker-authenticated?)
      handler/site))
