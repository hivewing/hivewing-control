(defproject hivewing-control "0.1.0"
  :description "Hivewing Control server."
  :url "http://control.hivewing.io"
  :dependencies [
                 [org.clojure/clojure "1.6.0"]
                 [compojure "1.2.1"]
                 [ring "1.2.0"]
                 [ring/ring-devel "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [http-kit "2.0.0"]
                 [rotary "0.4.1"]
                 [environ "1.0.0"]
                 [ring-basic-authentication "1.0.5"]
                 [org.clojars.gjahad/debug-repl "0.3.3"]
                 [ring.middleware.logger "0.4.1"]             ;; Ring middleware to log each request using Log4J
                 [org.clojure/tools.cli "0.2.2"]              ;; Command line argument parserkkk
                 ]
  :plugins [[lein-ring "0.8.6"]
            [lein-environ "1.0.0"]]
  :main hivewing-control.core
  :target-path "target/%s"
  :ring {:handler hello.core/app}
  :profiles {:uberjar {:aot :all}})
