(ns hivewing-events.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes app
  (GET "/" [] "Hivewing.io Events Server")
  (route/not-found "Not Found"))
