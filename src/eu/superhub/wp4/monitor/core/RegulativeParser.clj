(ns eu.superhub.wp4.monitor.core.RegulativeParser
  (:import eu.superhub.wp4.monitor.core.domain.ConditionHolder)
  (:require [clojure.string :as str])
  (:require [clojure.pprint :as ppr])
  (:require [clojure.math.combinatorics :as comb]))

(defn str-invoke [instance method-str & args]
  (clojure.lang.Reflector/invokeInstanceMethod 
    instance 
    method-str 
    (to-array args)))

;; Multimethod operator
(defmulti operator class)

;;; Operators
(defmethod operator net.sf.ictalive.operetta.OM.impl.ConjunctionImpl [o]
  `(~(symbol "conjunction") ~(operator (.getLeftStateFormula o)) ~(operator (.getRightStateFormula o))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.ImplicationImpl [o]
  `(~(symbol "disjunction") ~(cons (symbol "negation") ~(operator (.getAntecedentStateFormula o))) ~(operator (.getConsequentStateFormula o))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.DisjunctionImpl [o]
  `(~(symbol "disjunction") ~(operator (.getLeftStateFormula o)) ~(operator (.getRightStateFormula o))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.NegationImpl [o]
  `(~(symbol "negation") ~(operator (.getStateFormula o))))

;;; Terms
(defmethod operator net.sf.ictalive.operetta.OM.impl.AtomImpl [o]
  `(~(symbol "predicate") ~(operator (.getPredicate o)) ~(cons (symbol "arguments") (operator (.getArguments o)))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.FunctionImpl [o]
  `(~(symbol "function") ~(operator (.getName o)) ~(cons (symbol "parameters") (operator (.getArguments o)))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.VariableImpl [o]
  (def st (operator (.getName o)))
  (if (and (>= (int (first st)) (int \0)) (<= (int (first st)) (int \9)))
    (do
      `(~(symbol "variable") ~(str "x" (operator (.getName o)))))
    (do
      `(~(symbol "variable") ~(operator (.getName o))))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.ConstantImpl [o]
  `(~(symbol "constant") ~(operator (.getName o))))

;;; Others
(defmethod operator org.eclipse.emf.ecore.util.EObjectResolvingEList [o]
  (map operator (java.util.Vector. o)))

(defmethod operator String [o]
  (str o))

(defn add-not [o]
  `(~(symbol "negation") ~o))

(defn add-and [o]
  `(~(symbol "conjunction") ~o))

(defn distribute [dnf]
  ;; Input: DNF sequence in the form (((symbol "disjunction") ((symbol "conjunction") (a1) (a2) ... (aN)) ((symbol "conjunction") (b1) (b2) ... (bN)) ... ((symbol "conjunction") (z1) (z2) ... (zN))) ((symbol "disjunction") ...))
  (map #(cons (symbol "conjunction") %) (apply comb/cartesian-product (map #(rest (second %)) dnf))))

;; Multimethod: normalize
(defmulti normalize (fn [a] (first a)))

(defmethod normalize (symbol "disjunction") [o]
  ;; Input: DNF sequence in the form (((symbol "disjunction") ((symbol "conjunction") (a1) (a2) ... (aN)) ((symbol "conjunction") (b1) (b2) ... (bN)) ... ((symbol "conjunction") (z1) (z2) ... (zN))) ((symbol "disjunction") ...))
  (cons (symbol "disjunction") (apply concat (map rest (map normalize (rest o))))))

(defmethod normalize (symbol "conjunction") [o]
  (cons (symbol "disjunction") (distribute (map normalize (rest o)))))

(defmethod normalize (symbol "predicate") [o]
  `(~(symbol "disjunction") (~(symbol "conjunction") ~o)))

(defmethod normalize (symbol "negation") [o]
  `(~(symbol "disjunction") (~(symbol "conjunction") ~o)))

;;; Multimethod: negate
(defmulti negate (fn [a] (first a)))

(defmethod negate (symbol "conjunction") [o]
  (cons (symbol "disjunction") (map add-not (rest o))))

(defmethod negate (symbol "disjunction") [o]
  (cons (symbol "conjunction") (map add-not (rest o))))

(defmethod negate (symbol "negation") [o]
  (rest o))

;; Multimethod: clean-negations
(defmulti clean-negations (fn [a] (class (first a))))

(defmethod clean-negations clojure.lang.Symbol [o]
  (if (= (first o) (symbol "negation")) (if (= (first (second o)) (symbol "predicate")) o (clean-negations (negate (second o)))) (cons (first o) (map clean-negations (rest o)))))

(defmethod clean-negations java.lang.Character [o]
  (str o))

(defn convert-condition [norm condition]
  (operator (str-invoke norm (str "get" (str/capitalize condition) "Condition"))))

(defn convert-description [cas condition]
  (def sp (str/split condition #"-"))
  (operator (str-invoke cas (apply str "get" (str/capitalize (first sp)) (str/capitalize (second sp))))))

;; Entry point
(defn parse-condition [norm condition]
  `(~(symbol "condition") ~(str condition)
                          ~(normalize (clean-negations (convert-condition norm condition)))))
;                          ~(convert-condition norm condition)))

(defn parse-description [cas condition]
  `(~(symbol (str condition))
     ~(normalize (clean-negations (convert-description cas condition)))))
;                          ~(convert-condition norm condition)))

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

;		ns = om.getOm().getNs().getNorms();
(defn parse-file [st]
  ;		Serialiser<OperAModel>	s;
  ;		s = new Serialiser<OperAModel>(OMPackage.class, "opera", false);
  ;		om = s.deserialise(new File(DroolsEngine.class.getClassLoader().getResource(file).getFile().replace("%20", " ")));	
  (def s (eu.superhub.wp4.monitor.metamodel.utils.Serialiser. net.sf.ictalive.operetta.OM.OMPackage "opera" false))
  ;(def om (. s deserialise (java.io.File. "/Users/sergio/Documents/Research/wire/core/src/main/java/CalicoJack-1.0.0.opera")))
  (def om (. s deserialise (java.io.File. st)))
  ;(def om (. s deserialise (java.io.File. "/Users/sergio/Documents/Research/wire/core/src/main/java/Thales_Evacuation.opera.opera")))
 	;	ns = om.getOm().getNs().getNorms();
  ;	cs = om.getOm().getCs().getCountsAsRules();
  (parse-norms st (. (. (. om getOm) getNs) getNorms) (. (. (. om getOm) getCs) getCountsAsRules)))

(defn frm-save 
  "Save a clojure form to file." 
  [#^java.io.File file form] 
  (with-open [w (java.io.FileWriter. file)] 
    (binding [*out* w *print-dup* true] (ppr/pprint form))))

; Example of use:
; (frm-save (java.io.File. "/tmp/norm.lisp") (parse-file (. (clojure.java.io/resource "TestOpera.opera") getPath)))
