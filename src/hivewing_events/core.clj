(ns hivewing-events.core
  (:require [hivewing-events.server :refer [app]]
            [org.httpkit.server :refer [run-server]]))

 (defn -main
  [& args]
  (println "Hello, World! Spinning up!")
  (run-server app {:port 3003}))
