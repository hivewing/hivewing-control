(defproject hivewing-events "0.1"
  :description "Events and control channel for the workers in Hivewing"
  :dependencies [
    [org.clojure/clojure "1.6.0"]
    ;[aleph "0.3.0-alpha2"]
    ;[compojure "1.1.1"]
    ;[ring "1.1.0-beta2"]
    ;[hiccup "1.0.0-beta1"]
    ;[lein-swank "1.4.4"]
  ]
  :source-paths ["src/"]
  :main io.hivewing.events.server/main

  ;; :ring {:handler core.main/-main}
 )
