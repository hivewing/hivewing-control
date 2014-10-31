(ns hivewing-control.config
  (:require [environ.core  :refer [env]]))

(def aws-credentials
  {:access-key (env :hivewing-aws-access-key),
   :secret-key (env :hivewing-aws-secret-key),
   :endpoint   (env :hivewing-aws-endpoint)})

(def ddb-worker-table
  (env :hivewing-aws-dynamo-worker-table))
