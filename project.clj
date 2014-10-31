(defproject hivewing-control "0.1.0"
  :description "Hivewing Control server."
  :url "http://control.hivewing.io"
  :dependencies [
                 [org.clojure/clojure "1.6.0"]
                 [compojure "1.2.1"]
                 [ring/ring-devel "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [http-kit "2.0.0"]
                 [rotary "0.4.1"]
                 [environ "1.0.0"]
                 ]
  :main hivewing-control.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
