(ns hivewing-control.worker
  (:require [rotary.client :refer :all]
            [pantomime.mime :refer [mime-type-of]]
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
    (println kv-pair)
    ; TODO - If it's > 1k bytes in length (the 1 of kv-pair
    ;        then we should store on S3
    (put-item aws-credentials ddb-worker-table
              {:guid worker-guid,
               :key  (get kv-pair 0),
               :data (get kv-pair 1),
               :_uat (System/currentTimeMillis),
               :type (mime-type-of (get kv-pair 1))})))
