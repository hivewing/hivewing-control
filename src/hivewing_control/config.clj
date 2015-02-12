(ns hivewing-control.config
  (:require [environ.core  :refer [env]]))

(defn porter-hosts
  [] (env :hivewing-porter-hosts))
