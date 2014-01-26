(ns eu.superhub.wp4.monitor.core.lisp-regulative-parser
  (:import eu.superhub.wp4.monitor.core.domain.ConditionHolder)
  (:require [clojure.string :as str])
  (:require [clojure.pprint :as ppr])
  (:require [clojure.math.combinatorics :as comb]
            [clojure.data.json :as json]))

(defn str-invoke [instance method-str & args]
  (clojure.lang.Reflector/invokeInstanceMethod 
    instance 
    method-str 
    (to-array args)))

(defn parse-norm [norm]
  `(~(symbol "norm") ~(. norm getNormID) ~(cons (symbol "conditions") (map #(parse-condition norm %) (list "activation" "maintenance" "expiration")))))

(defn parse-countsas [cas]
  (if (nil? (. cas getContext))
    (def ct "Universal")
    (def ct (. (. cas getContext) getName)))
  `(~(symbol "counts-as") "" ~(list (symbol "context") ct) ~(parse-description cas "concrete-fact") ~(parse-description cas "abstract-fact")))

(defn get-file-name [st]
  (. (. (java.io.File. st) getName) replaceAll ".opera" ""))

(defn update-id [idx cas]
  (replace {"" (str "counts-as-rule-" idx)} cas))

(defn parse-norms [st norms cas]
  `(~(symbol "institution") ~(get-file-name st) ~@(vec (map parse-norm norms)) ~@(vec (map-indexed update-id (map parse-countsas cas)))))

(defn parse-file [st]
  (def s (eu.superhub.wp4.monitor.metamodel.utils.Serialiser. net.sf.ictalive.operetta.OM.OMPackage "opera" false))
  (def om (. s deserialise (java.io.File. st)))
  (parse-norms st (. (. (. om getOm) getNs) getNorms) (. (. (. om getOm) getCs) getCountsAsRules)))

(defn frm-save 
  "Save a clojure form to file." 
  [#^java.io.File file form] 
  (with-open [w (java.io.FileWriter. file)] 
    (binding [*out* w *print-dup* true] (ppr/pprint form))))

; Example of use:
(frm-save (java.io.File. "/tmp/norm.lisp") (json/write-str (parse-file (. (clojure.java.io/resource "TestOpera.opera") getPath))))
