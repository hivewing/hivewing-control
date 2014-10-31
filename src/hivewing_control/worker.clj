(ns hivewing-control.worker
  (:require [rotary.client :refer :all]
            [hivewing-control.config  :refer [aws-credentials ddb-worker-table]]))

(defn worker-ensure-tables []
  (ensure-table aws-credentials {:name ddb-worker-table, :hash-key {:name "guid", :type :s}, :range-key {:name "key", :type :s}, :throughput {:read 1, :write 1}}))

(defn worker-get-config
  [worker-guid]
  (query aws-credentials ddb-worker-table {:key worker-guid}))

(defn worker-set-config
  [worker-guid parameters]
  ; Want to split the parameters
  (doseq [kv-pair parameters]
    (println kv-pair))
  )
