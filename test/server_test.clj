(ns test
  (:use clojure.test)
  (:use io.hivewing.events))

(deftest run-server
  (is (= (hello) "Hellow World"))
  (is (= (hello "test") "Hello test!")))
