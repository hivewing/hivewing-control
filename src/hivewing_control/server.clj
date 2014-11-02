(ns hivewing-control.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :refer :all]
            [clojure.string :refer [split trim lower-case]]))

; https://gist.github.com/cgmartin/5880732
(defn worker-control-handler [request]
  (with-channel request channel
    (on-close channel (fn [status] (println "channel closed: " status)))
    (on-receive channel (fn [data] ;; echo it back
      (send! channel data)))))

(defroutes app
  (GET "/" [] "Hivewing.io Control Server")
  (GET "/:worker-guid" [] worker-control-handler)
  (route/not-found "Not Found"))
