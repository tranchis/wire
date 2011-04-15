(ns net.sf.ictalive.monitoring.rules.drools.ConditionParser
  (:import net.sf.ictalive.monitoring.domain.ConditionHolder)
  (:require [clojure.string :as str])
  )

(defn str-invoke [instance method-str & args]
            (clojure.lang.Reflector/invokeInstanceMethod 
                instance 
                method-str 
                (to-array args)))

;; Multimethod operator
(defmulti operator class)

;;; Operators
(defmethod operator net.sf.ictalive.operetta.OM.impl.ConjunctionImpl [o]
  `(:and(~(operator (.getLeftStateFormula o)) ~(operator (.getRightStateFormula o))))
)

(defmethod operator net.sf.ictalive.operetta.OM.impl.ImplicationImpl [o]
  `(:implies(~(operator (.getAntecedentStateFormula o)) ~(operator (.getConsequentStateFormula o))))
)

(defmethod operator net.sf.ictalive.operetta.OM.impl.DisjunctionImpl [o]
  `(:or(~(operator (.getLeftStateFormula o)) ~(operator (.getRightStateFormula o))))
)

(defmethod operator net.sf.ictalive.operetta.OM.impl.NegationImpl [o]
  `(:not(~(operator (.getStateFormula o))))
)

;;; Terms
(defmethod operator net.sf.ictalive.operetta.OM.impl.AtomImpl [o]
  `(:predicate(~(operator (.getPredicate o)) ~(operator (.getArguments o))))
)

(defmethod operator net.sf.ictalive.operetta.OM.impl.FunctionImpl [o]
  `(:function(~(operator (.getName o)) ~(operator (.getArguments o))))
)

(defmethod operator net.sf.ictalive.operetta.OM.impl.VariableImpl [o]
  `(:var(~(operator (.getName o))))
)

(defmethod operator net.sf.ictalive.operetta.OM.impl.ConstantImpl [o]
  `(:constant(~(operator (.getName o))))
)

;;; Others
(defmethod operator org.eclipse.emf.ecore.util.EObjectResolvingEList [o]
  (map operator (java.util.Vector. o))
)

(defmethod operator String [o]
  (str o)
)

(defmethod operator :default [o]
  (str "Not supported: " (class o))
)

;; Multimethod: clean-negations
(defmulti clean-negations class)

(defmethod clean-negations clojure.lang.Cons [o]
  (map clean-negations o)
)

(defmethod clean-negations clojure.lang.Keyword [o]
  (if (= o :not) (str "negation!") o)
)

(defmethod clean-negations java.lang.String [o]
  (str o)
)

(defmethod clean-negations clojure.lang.LazySeq [o]
  (map clean-negations o)
)

(defmethod clean-negations clojure.lang.PersistentList [o]
  (map clean-negations o)
)

(defmethod clean-negations :default [o]
  (str "Not supported: " (class o))
)

;; Entry point
(defn parse-condition [norm condition]
  (clean-negations (operator (str-invoke norm (str "get" (str/capitalize condition) "Condition"))))
)

(defn parse-norm [norm]
  (map #(parse-condition norm %) (list "activation" "maintenance" "expiration"))
)

(defn parse-norms [norms]
  (vec (map parse-norm norms))
)
