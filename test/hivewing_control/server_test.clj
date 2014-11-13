(ns hivewing-control.server_test
  (:require [hivewing-control.server :as server]
            [hivewing-core.worker-config :as worker-config]
            [digest :as digest]
            [clojure.test :refer :all]))

; Just always run this
(worker-config/worker-ensure-tables)

(deftest server-test
  (testing "status message field"
    (let [ value "value"
           config  ["key1" value]
           result (server/create-status-message-field config)]
      (is (= result
             ["key1" (digest/md5 value)])))
    )
  (testing "status message"
    (let [ value "value"
           config  {"key1" value}
           result (server/create-status-message config)]
      (is (= (get-in result ["status" "key1"])
             (digest/md5 value)))
    ))
  (testing "process incoming status message"
    (let [worker-uuid "worker-uuid"
          req         {:basic-authentication worker-uuid}
          key-value   "key1-default"
          result (first (server/process-messages req {"update" {"key1" key-value}}))
          ]
      (is (= (digest/md5 key-value)
             (get-in result ["status" "key1"])))
      ))

  (testing "process incoming update message"
    (let [worker-uuid "worker-uuid"
          req         {:basic-authentication worker-uuid}
          key-value   "key1-default"
          ; First we update to the key
          initial-result (first (server/process-messages req {"update" {"key1" key-value}}))
          ; Now we send a status , with an incorrect md5
          status (first (server/process-messages req {"status" {"key1" "not-matching"}}))
          ]
        (println status)
      ))
  )
