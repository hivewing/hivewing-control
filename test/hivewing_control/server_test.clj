(ns hivewing-events.server_test
  (:require [hivewing-events.server :refer [app]]
            [clojure.test :refer :all]))

(defn request [resource web-app & params]
  (web-app {:request-method :get :uri resource :params (first params)}))

(deftest test-routes
  (is (= 200 (:status (request "/" app))))
  (is (= "Hello World" (:body (request "/" app)))))
