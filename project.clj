(defproject hivewing-control "0.1.0"
  :description "Hivewing Control server."
  :url "http://control.hivewing.io"
  :dependencies [
                 [org.clojure/clojure "1.6.0"]
                 [compojure "1.2.1"]
                 [environ "1.0.0"]
                 [hivewing-core "0.1.0-SNAPSHOT"]
                 [http-kit "2.1.16"]
                 ]
  :plugins [[s3-wagon-private "1.1.2"]]

  :repositories [["hivewing-core" {:url "s3p://clojars.hivewing.io/hivewing-core/releases"
                                   :username "AKIAJFFI52CFHPRZIPJA"
                                   :passphrase "THLlPQGSstzspARpgw4s+LgqFT2DyrNFJ+vxkHDf"}]]

  :main hivewing-control.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
