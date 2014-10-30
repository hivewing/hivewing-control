(ns hivewing-control.core
  (:require [hivewing-control.server :refer [app]]
            [org.httpkit.server :refer [run-server]]))

 (defn -main
  [& args]
  (let [port 3003]
    (println (str "Hello, World! Spinning up! port:" port))
    (run-server app {:port port})))
