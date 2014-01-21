(ns eu.superhub.wp4.monitor.core.SDMClient
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(gen-class
  :name eu.superhub.wp4.monitor.core.SDMClient
  :init init
  :constructors {[String] []}
  :state state
  :prefix "sdm-"
  :methods [[query [String] String]])

(defn sdm-init [^String url]
  [[] (ref url)])

(defn sdm-query [this ^String json]
  (->
    (client/post @(.state this) {:form-params {:query json}})
    :body))

;; Compile, if we can
;; Should fail when called from Java (AOT) but that's fine
(try
  (set! *compile-path* "contrib/clojure-binaries")
  (compile 'eu.superhub.wp4.monitor.core.SDMClient)
  (catch Exception e
;    (.printStackTrace e)
    ))




