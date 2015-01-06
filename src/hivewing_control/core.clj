(ns hivewing-control.core
  (:require [hivewing-control.server :refer [app]]
            [org.httpkit.server :refer [run-server]])
  (:gen-class))

(defn -main
  [& args]
  (let [port 6000]
    (println (str "Starting up on port: " port))
    (run-server app {:port port})))
