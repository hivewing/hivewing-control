(ns hivewing-control.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :refer :all]
            [clojure.string :refer [split trim lower-case]]))


; https://gist.github.com/cgmartin/5880732
;   Need to authenticate inside a "with-channel" macro
;     worker-guid is in the URL, the Key should be matching the workers access-key
;     make a request to SQL and find the info - verify it matches!
;   Need to verify the subprotocol in there as well
;     should match something handy like "hivewing-control.v1"


(defn worker-control-handler [request]
  (with-channel request channel
    (on-close channel (fn [status] (println "channel closed: " status)))
    (on-receive channel (fn [data] ;; echo it back
      (send! channel data)))))

(defroutes app
  (GET "/" [] "Hivewing.io Control Server")
  (GET "/:worker-guid" [] worker-control-handler)
  (route/not-found "Not Found"))
