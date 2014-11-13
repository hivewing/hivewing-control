(ns hivewing-control.authentication
  (:require [hivewing-core.worker :as worker-core]))

(defn worker-authenticated?
  "Worker authentication method. This should look up the worker in the DB
   and test for it matching access-tokens.  If it passes, it should return
   the worker uuid, so that the rest of the stack knows who authenticated"
  [worker-uuid worker-access-token]
  (if (= (str (get (worker-core/worker-get worker-uuid) :access_token))
          worker-access-token)
    worker-uuid
    nil))

; ws://83d49946-6adb-11e4-b202-4fda88215485:4b7303bb-6add-457c-9b27-26172b4fb661@fitznet41.duckdns.org:4000/?json=true
