(ns hivewing-control.authentication
  (:require [hivewing-core.worker :as worker-core]))

(defn worker-authenticated?
  "Worker authentication method. This should look up the worker in the DB
   and test for it matching access-tokens.  If it passes, it should return
   the worker uuid, so that the rest of the stack knows who authenticated"
  [worker-uuid worker-access-token]
  (worker-core/worker-access? worker-uuid worker-access-token))
