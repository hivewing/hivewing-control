(defproject hivewing-control "0.1.0"
  :description "Hivewing Control server."
  :url "http://control.hivewing.io"
  :dependencies [
                 [org.clojure/clojure "1.6.0"]
                 [compojure "1.2.1"]
                 [environ "1.0.0"]
                 [hivewing-core "0.1.0-SNAPSHOT"]
                 [http-kit "2.1.16"]
                 [clojure-msgpack "0.1.0-SNAPSHOT"]
                 [digest "1.4.4"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring/ring-core "1.3.1"]
                 [ring-basic-authentication "1.0.5"]
                 [org.clojure/data.json "0.2.5"]
                 ]
  :plugins [[s3-wagon-private "1.1.2"]
            [lein-environ "1.0.0"]]
  :repositories [["hivewing-core" {:url "s3p://clojars.hivewing.io/hivewing-core/releases"
                                   :username "AKIAJCSUM5ZFGI7DW5PA"
                                   :passphrase "UcO9VGAaGMRuJZbgZxCiz0XuHmB1J0uvzt7WIlJK"}]]

  :main hivewing-control.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
  ;   :hivewing-redis-uri "redis://127.0.0.1:3900/"
  ;   :hivewing-aws-access-key "123abc",
  ;   :hivewing-aws-secret-key "123abc",
  ;   :hivewing-ddb-endpoint   "http://127.0.0.1:3800",
  ;   :hivewing-ddb-worker-config-table "HivewingWorkerConfiguration.v1"
  ;   :hivewing-sql-connection-string "//127.0.0.1:5432/hivewing-dev?user=hivewing&password=hivewing"
