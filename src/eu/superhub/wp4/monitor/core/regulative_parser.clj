(ns eu.superhub.wp4.monitor.core.regulative-parser
  (:import eu.superhub.wp4.monitor.core.domain.ConditionHolder)
  (:require [clojure.string :as str])
  (:require [clojure.pprint :as ppr])
  (:require [clojure.data.json :as json]
            [eu.superhub.wp4.monitor.core.fol-conversions :as folc]))

(defn str-invoke [instance method-str & args]
  (clojure.lang.Reflector/invokeInstanceMethod 
    instance 
    method-str 
    (to-array args)))

;; Entry point
(defn parse-condition [norm condition]
  (folc/operetta->edn
    (str-invoke norm (str "get" (str/capitalize condition) "Condition"))))

(defn parse-description [cas condition]
  (let [sp (str/split condition #"-")]
    (folc/operetta->edn
      (str-invoke cas
                  (apply str
                         "get"
                         (str/capitalize (first sp))
                         (str/capitalize (second sp)))))))

(defn parse-norm [norm]
  {:norm-id (.getNormID norm)
   :conditions (apply merge (map #(hash-map
                                    (keyword %)
                                    (parse-condition norm %))
                                 ["activation"
                                  "maintenance"
                                  "expiration"]))})

(defn parse-countsas [cas]
  (let [orig-ct (.getContext cas)
        ct (if (nil? orig-ct)
             "Universal"
             (.getName orig-ct))]
    {:context ct
     :concrete-fact (parse-description cas "concrete-fact")
     :abstract-fact (parse-description cas "abstract-fact")}))

(defn get-file-name [st]
  (-> (java.io.File. st)
    .getName
    (.replaceAll ".opera" "")))

(defn update-id [idx cas]
  (replace {"" (str "counts-as-rule-" idx)} cas))

(defn parse-norms [st norms cas]
  {:name (get-file-name st)
   :norms (into [] (map parse-norm norms))
   :cas-rules (into [] (map parse-countsas cas))})

(defn parse-file [st]
  (let [s (eu.superhub.wp4.monitor.metamodel.utils.Serialiser.
            net.sf.ictalive.operetta.OM.OMPackage "opera" false)
        om (.deserialise s (java.io.File. st))
        norms (-> om .getOm .getNs .getNorms)
        cas (-> om .getOm .getCs .getCountsAsRules)]
    (parse-norms st norms cas)))

#_(parse-file
   (.getPath (clojure.java.io/resource "TestOpera.opera")))
