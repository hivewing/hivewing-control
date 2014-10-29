(ns server-test
  (:use clojure.test)
  (:use io.hivewing.events.server))

(deftest run-server
  (is (= (hello) "Hellow World"))
  (is (= (hello "test") "Hello test!")))
