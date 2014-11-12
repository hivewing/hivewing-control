(ns hivewing-control.core
  (:require [hivewing-control.server :refer [app]]
            [org.httpkit.server :refer [run-server]]))

(defn -main
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (let [port 4000]
    (println (str "Starting up on port: " port))
    (run-server app {:port port})))
