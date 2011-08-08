(ns net.sf.ictalive.monitoring.rules.drools.RegulativeParser
  (:import net.sf.ictalive.monitoring.domain.ConditionHolder)
  (:require [clojure.string :as str])
  (:require [clojure.contrib.combinatorics :as comb]))

(defn str-invoke [instance method-str & args]
            (clojure.lang.Reflector/invokeInstanceMethod 
                instance 
                method-str 
                (to-array args)))

;; Multimethod operator
(defmulti operator class)

;;; Operators
(defmethod operator net.sf.ictalive.operetta.OM.impl.ConjunctionImpl [o]
  `(:and ~(operator (.getLeftStateFormula o)) ~(operator (.getRightStateFormula o))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.ImplicationImpl [o]
  `(:or (:not ~(operator (.getAntecedentStateFormula o))) ~(operator (.getConsequentStateFormula o))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.DisjunctionImpl [o]
  `(:or ~(operator (.getLeftStateFormula o)) ~(operator (.getRightStateFormula o))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.NegationImpl [o]
  `(:not ~(operator (.getStateFormula o))))

;;; Terms
(defmethod operator net.sf.ictalive.operetta.OM.impl.AtomImpl [o]
  `(:predicate ~(operator (.getPredicate o)) ~(cons :arguments (operator (.getArguments o)))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.FunctionImpl [o]
  `(:function ~(operator (.getName o)) ~(cons :parameters (operator (.getArguments o)))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.VariableImpl [o]
  `(:variable ~(operator (.getName o))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.ConstantImpl [o]
  `(:constant ~(operator (.getName o))))

;;; Others
(defmethod operator org.eclipse.emf.ecore.util.EObjectResolvingEList [o]
  (map operator (java.util.Vector. o)))

(defmethod operator String [o]
  (str o))

(defmethod operator :default [o]
  (str "Not supported: " (class o)))

(defn add-not [o]
  `(:not ~o))

(defn add-and [o]
  `(:and ~o))

(defn distribute [dnf]
  ;; Input: DNF sequence in the form ((:or (:and (a1) (a2) ... (aN)) (:and (b1) (b2) ... (bN)) ... (:and (z1) (z2) ... (zN))) (:or ...))
  (map #(cons :and %) (apply comb/cartesian-product (map #(rest (second %)) dnf))))

;; Multimethod: normalize
(defmulti normalize (fn [a] (first a)))

(defmethod normalize :or [o]
  ;; Input: DNF sequence in the form ((:or (:and (a1) (a2) ... (aN)) (:and (b1) (b2) ... (bN)) ... (:and (z1) (z2) ... (zN))) (:or ...))
  (cons :or (apply concat (map rest (map normalize (rest o))))))

(defmethod normalize :and [o]
  (cons :or (distribute (map normalize (rest o)))))

(defmethod normalize :predicate [o]
  `(~:or (~:and ~o)))

(defmethod normalize :not [o]
  `(~:or (~:and ~o)))

;;; Multimethod: negate
(defmulti negate (fn [a] (first a)))

(defmethod negate :and [o]
  (cons :or (map add-not (rest o))))

(defmethod negate :or [o]
  (cons :and (map add-not (rest o))))

(defmethod negate :not [o]
  (rest o))

;; Multimethod: clean-negations
(defmulti clean-negations (fn [a] (class (first a))))

(defmethod clean-negations clojure.lang.Keyword [o]
  (if (= (first o) :not) (if (= (first (second o)) :predicate) o (clean-negations (negate (second o)))) (cons (first o) (map clean-negations (rest o)))))

(defmethod clean-negations java.lang.Character [o]
  (str o))

(defmethod clean-negations :default [o]
  (str "Not supported: " (class (first o))))

(defn convert-condition [norm condition]
  (operator (str-invoke norm (str "get" (str/capitalize condition) "Condition"))))

;; Entry point
(defn parse-condition [norm condition]
  `(~(keyword (str condition)) ~(normalize (clean-negations (convert-condition norm condition)))))

(defn parse-norm [norm]
  (cons :norm (cons (. norm getNormID) (map #(parse-condition norm %) (list "activation" "maintenance" "expiration")))))

(defn parse-norms [norms]
  (cons :institution (vec (map parse-norm norms))))

;		ns = om.getOm().getNs().getNorms();
(defn parse-file [st]
	;		Serialiser<OperAModel>	s;
	;		s = new Serialiser<OperAModel>(OMPackage.class, "opera", false);
	;		om = s.deserialise(new File(DroolsEngine.class.getClassLoader().getResource(file).getFile().replace("%20", " ")));	
	(def s (net.sf.ictalive.metamodel.utils.Serialiser. net.sf.ictalive.operetta.OM.OMPackage "opera" false))
	;(def om (. s deserialise (java.io.File. "/Users/sergio/Documents/Research/wire/core/src/main/java/CalicoJack-1.0.0.opera")))
	(def om (. s deserialise (java.io.File. st)))
	;(def om (. s deserialise (java.io.File. "/Users/sergio/Documents/Research/wire/core/src/main/java/Thales_Evacuation.opera.opera")))
  (parse-norms (. (. (. om getOm) getNs) getNorms)))
