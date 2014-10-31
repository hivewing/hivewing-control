(ns hivewing-control.core
  (:require [hivewing-control.server :refer [app]]
            [hivewing-control.worker :refer [worker-ensure-tables worker-set-config]]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]))

 (defn -main
  [& args]
  (let [port 3003]
    (println (str "Hello, World! Spinning up! port:" port))
    (worker-ensure-tables)
    (worker-set-config "guid123" {:name "bear bryant", :home "alabama", :band "needtobreathe"})
    (run-server app {:port port})))
