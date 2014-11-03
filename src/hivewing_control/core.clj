(ns hivewing-control.core
  (:require [hivewing-control.server :refer [app-routes]]
            [compojure.handler :refer [site]]
            [clojure.tools.cli :refer [cli]]
            [environ.core :refer [env]]
            [ring.middleware.reload :as reload]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
            [clojure.tools.logging :as log]
            [org.httpkit.server :refer [run-server]]))

(defn- authenticated? [worker-guid access-key]
  (and (= worker-guid "foo")
       (= access-key "bar")))


(defn -main
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (let [[options args banner]
    (cli args
      ["-h" "--help" "Show Help" :default false :flag true]
      ["-p" "--port" "Port to listen to" :default 3000 :parse-fn #(Integer. %)]
      ["-d" "--development" "Run server in development mode" :default false :flag true])]
      (defonce in-dev? (:development options))
        (when (:help options)
          (println banner)
          (System/exit 0))
        (log/info "Running server on port" (:port options) "with development mode" in-dev?)

        (let [handler (if in-dev?
          (reload/wrap-reload (site #'app-routes)) ;; only reload when dev
          (site app-routes))

      _ (run-server (wrap-basic-authentication handler authenticated?) {:port (:port options)})])))
