(ns eu.superhub.wp4.monitor.core.fol
  (:require [clojure.math.combinatorics :as comb]))

;; ## Multimethod operator
(defmulti operator class)

;; ### Operators
(defmethod operator net.sf.ictalive.operetta.OM.impl.ConjunctionImpl [o]
  `(~(symbol "conjunction")
     ~(operator (.getLeftStateFormula o))
     ~(operator (.getRightStateFormula o))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.ImplicationImpl [o]
  `(~(symbol "disjunction")
     ~(cons (symbol "negation") ~(operator (.getAntecedentStateFormula o)))
     ~(operator (.getConsequentStateFormula o))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.DisjunctionImpl [o]
  `(~(symbol "disjunction")
     ~(operator (.getLeftStateFormula o))
     ~(operator (.getRightStateFormula o))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.NegationImpl [o]
  `(~(symbol "negation")
     ~(operator (.getStateFormula o))))

;; ## Terms
(defmethod operator net.sf.ictalive.operetta.OM.impl.AtomImpl [o]
  `(~(symbol "predicate")
     ~(operator (.getPredicate o))
     ~(cons (symbol "arguments") (operator (.getArguments o)))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.FunctionImpl [o]
  `(~(symbol "function")
     ~(operator (.getName o))
     ~(cons (symbol "parameters") (operator (.getArguments o)))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.VariableImpl [o]
  (let [st (operator (.getName o))]
    (if (and (>= (int (first st)) (int \0)) (<= (int (first st)) (int \9)))
      `(~(symbol "variable") ~(str "x" (operator (.getName o))))
      `(~(symbol "variable") ~(operator (.getName o))))))

(defmethod operator net.sf.ictalive.operetta.OM.impl.ConstantImpl [o]
  `(~(symbol "constant") ~(operator (.getName o))))

;; ## Others
(defmethod operator org.eclipse.emf.ecore.util.EObjectResolvingEList [o]
  (map operator (java.util.Vector. o)))

(defmethod operator String [o]
  (str o))

(defn add-not [o]
  `(~(symbol "negation") ~o))

(defn add-and [o]
  `(~(symbol "conjunction") ~o))

(defn distribute [dnf]
  "Input: DNF sequence in the form
  (((symbol \"disjunction\")
      ((symbol \"conjunction\") (a1) (a2) ... (aN))
      ((symbol \"conjunction\") (b1) (b2) ... (bN))
      ...
      ((symbol \"conjunction\") (z1) (z2) ... (zN)))
    ((symbol \"disjunction\") ...))"
  (map #(cons (symbol "conjunction") %)
       (apply comb/cartesian-product (map #(rest (second %)) dnf))))

;; ## Multimethod: eliminate redundancy
(defmulti eliminate-redundancy first)

(defmethod eliminate-redundancy (symbol "disjunction") [o]
  (let [normalized (map eliminate-redundancy (rest o))]
    (if (not (some #(not= (symbol "disjunction") (first %))
                    normalized))
      `(~(symbol "disjunction") ~@(mapcat rest normalized))
      `(~(symbol "disjunction") ~@normalized))))

(defmethod eliminate-redundancy (symbol "conjunction") [o]
  (let [normalized (map eliminate-redundancy (rest o))]
    (if (not (some #(not= (symbol "conjunction") (first %))
                    normalized))
      `(~(symbol "conjunction") ~@(mapcat rest normalized))
      `(~(symbol "conjunction") ~@normalized))))

(defmethod eliminate-redundancy :default [o]
  o)

;; ## Multimethod: normalize
(defmulti normalize (fn [a] (first a)))

(defmethod normalize (symbol "disjunction") [o]
  "Input: DNF sequence in the form
  (((symbol \"disjunction\")
      ((symbol \"conjunction\") (a1) (a2) ... (aN))
      ((symbol \"conjunction\") (b1) (b2) ... (bN))
      ...
      ((symbol \"conjunction\") (z1) (z2) ... (zN)))
    ((symbol \"disjunction\") ...))"
  (let [normalized (map normalize (rest o))]
    (cons (symbol "disjunction") normalized)))

(defmethod normalize (symbol "conjunction") [o]
  (let [normalized (map normalize (rest o))]
    (cons (symbol "disjunction") (distribute normalized))))

(defmethod normalize (symbol "predicate") [o]
  `(~(symbol "disjunction") (~(symbol "conjunction") ~o)))

(defmethod normalize (symbol "negation") [o]
  `(~(symbol "disjunction") (~(symbol "conjunction") ~o)))

;; ## Multimethod: negate
(defmulti negate (fn [a] (first a)))

(defmethod negate (symbol "conjunction") [o]
  (cons (symbol "disjunction") (map add-not (rest o))))

(defmethod negate (symbol "disjunction") [o]
  (cons (symbol "conjunction") (map add-not (rest o))))

(defmethod negate (symbol "negation") [o]
  (second o))

;; ## Multimethod: clean-negations
(defmulti clean-negations (fn [a] (class (first a))))

(defmethod clean-negations clojure.lang.Symbol [o]
  (if (= (first o) (symbol "negation"))
    (if (= (first (second o)) (symbol "predicate"))
      o
      (clean-negations (negate (first (rest o)))))
    (cons (first o) (map clean-negations (rest o)))))

(defmethod clean-negations java.lang.Character [o]
  (str o))

(defn normal-form [x]
  (loop [result (operator x) old-result '()]
    (if (= result old-result)
      result
      (recur
        (eliminate-redundancy (normalize (eliminate-redundancy (clean-negations result))))
        result))))

#_(normal-form '(conjunction (conjunction (disjunction (conjunction (conjunction (predicate "test" (arguments)) (predicate "test2" (arguments))))))))
