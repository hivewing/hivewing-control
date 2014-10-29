(defproject hivewing-events "0.1.0"
  :description "Hivewing Events / Control server."
  :url "http://events.hivewing.io"
  :dependencies [
                 [org.clojure/clojure "1.6.0"]
                 [compojure "1.2.1"]
                 [ring/ring-devel "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [http-kit "2.0.0"]
                 ]
  :main hivewing-events.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
