(ns hivewing-control.core
  (:require [hivewing-control.server :refer [app-routes]]
            [hivewing-control.config :refer []]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]))

(defn -main
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (let [port 4000]
    (println (str "Starting up on port: " port))
    (run-server app-routes {:port port})))
