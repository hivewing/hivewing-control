(ns hivewing-control.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes app
  (GET "/" [] "Hivewing.io Control Server")
  (route/not-found "Not Found"))
