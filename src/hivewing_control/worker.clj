(ns hivewing-control.worker
  (:require [rotary.client :refer :all]
            [pantomime.mime :refer [mime-type-of]]
            [alex-and-georges.debug-repl :refer :all]
            [hivewing-control.config  :refer [aws-credentials ddb-worker-table]]))

(defn worker-ensure-tables []
  (ensure-table aws-credentials {:name ddb-worker-table, :hash-key {:name "guid", :type :s}, :range-key {:name "key", :type :s}, :throughput {:read 1, :write 1}}))

(defn worker-get-config
  [worker-guid]
  (debug-repl)
  (:items (query aws-credentials ddb-worker-table {"guid" worker-guid})))

(defn worker-set-config
  [worker-guid parameters]
  ; Want to split the parameters
  (doseq [kv-pair parameters]
    (let [upload-data {:guid worker-guid,
               :key  (str (get kv-pair 0)),
               :data (str (get kv-pair 1)),
               :_uat (System/currentTimeMillis),
               :type (str (mime-type-of (get kv-pair 1)))}]

      ; If the data is > 1024 bytes, we need to upload it.
      (if (> 1024 (count (:data upload-data)))
        ( ; Upload data should go to S3, and we put the url in there instead.
          ; the mime-type should be a blob
         ))
      (println (str "Uploading " upload-data))
      (put-item aws-credentials ddb-worker-table upload-data))))
