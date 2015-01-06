(ns hivewing-control.server_test
  (:require [hivewing-control.server :as server]
            [hivewing-core.helpers :as helpers]
            [hivewing-core.worker :refer :all]
            [digest :as digest]
            [clojure.test :refer :all]))

(use-fixtures :each helpers/clean-database)

(deftest server-test
  (testing "status message field"
    (let [ value "value"
           config  ["key1" value]
           result (server/create-status-message-field config)]
      (is (= result
             ["key1" (digest/md5 value)])))
    ))


(deftest server-test-status-message-creation
    (let [ value "value"
           config  {"key1" value}
           result (server/create-status-message config)]
      (println value config result)
      (is (=
           "07b1937be911b2a2ed60686bb9eab2b66fe51d3a247d63763d2a22f3e3d36547"
            (get-in result ["status" "hash"])))))

(deftest server-test-processing-incoming-status
  (let [
        res    (helpers/create-worker)
        worker-uuid (:worker-uuid res)
        access-token (:access_token (worker-get worker-uuid :include-access-token true))
        req         {:basic-authentication (worker-access? worker-uuid access-token)}
        key-value   "key1-default"
        result (first (server/process-messages req {"update" {"key1" key-value}}))
        ]
    (is (= "0ac88afe181467d621075372bb1b9f7b23ef13e1571e52d93d0351726f61073d"
           (get-in result ["status" "hash"])))
    ))


(deftest server-test-incoming-update
    (let [res    (helpers/create-worker)
          worker-uuid (:worker-uuid res)
          access-token (:access_token (worker-get worker-uuid :include-access-token true))
          req         {:basic-authentication (worker-access? worker-uuid access-token)}
          key-value   "key1-default"
          result (first (server/process-messages req {"update" {"key1" key-value}}))
          ; First we update to the key
          initial-result (first (server/process-messages req {"update" {"key1" key-value}}))
          ; Now we send a status , with an incorrect md5
          status (first (server/process-messages req {"status" {"key1" "not-matching"}}))
          ]
        (println status)
      ))
