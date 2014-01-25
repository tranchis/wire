(ns eu.superhub.wp4.monitor.core.PolicyMonitor)

(use '[clojure.pprint :only (pprint)])

(import (eu.superhub.wp4.encoder.core          PolicyEncoderCore)
        (eu.superhub.wp3.marshaller            GenericMarshaller)
        (eu.superhub.wp4.models.mobilitypolicy PolicyModel))

(gen-class
  :name eu.superhub.wp4.monitor.core.PolicyMonitor
  :init "init"
  :state "state"
  :constructors {[] []}
  :methods [
            [savePolicyToFile [] void]
            [getPolicy [] eu.superhub.wp4.models.mobilitypolicy.PolicyModel]
           ])

(defn load-policy []
  (let [pec (PolicyEncoderCore.)]
     (. pec getPolicyModelFromDB)))

(defn -init []
  [[] (ref {:policy (load-policy)})])

(defn ^PolicyModel -getPolicy [this]
  (:policy @(. this state)))

(defn hashmap-to-string [m]
  (let [w (java.io.StringWriter.)] (pprint m w) (.toString w)))

(defn ^Void -savePolicyToFile [this ^String file-name]
  (spit (java.io.File. file-name) (hashmap-to-string (-getPolicy this))))

;; Compile, if we can
;; Should fail when called from Java (AOT) but that's fine
(try
  (set! *compile-path* "contrib/clojure-binaries")
  (compile 'eu.superhub.wp4.monitor.core.PolicyMonitor)
  (catch Exception e #_(.printStackTrace e)))
