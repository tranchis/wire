(ns eu.superhub.wp4.monitor.core.fol-conversions
  (:require [clojure.data.json :as json]
            [eu.superhub.wp4.monitor.core.fol :as fol]))

;; ## Multimethod: edn->fol
(defmulti edn->fol :type)

(defmethod edn->fol "disjunction" [e]
  `(~(symbol "disjunction")
     ~@(vec (map edn->fol (:formulae e)))))

(defmethod edn->fol "conjunction" [e]
  `(~(symbol "conjunction")
     ~@(map edn->fol (:formulae e))))

(defmethod edn->fol "negation" [e]
  `(~(symbol "negation")
     ~(edn->fol (:formula e))))

(defmethod edn->fol "predicate" [e]
  `(~(symbol "predicate")
     ~(:name e)
     ~(cons (symbol "arguments") (map edn->fol (:arguments e)))))

(defmethod edn->fol "function" [e]
  `(~(symbol "function")
     ~(:name e)
     ~(cons (symbol "parameters") (map edn->fol (:parameters e)))))

(defmethod edn->fol "variable" [e]
  (let [st (:name e)]
    (if (and (>= (int (first st)) (int \0)) (<= (int (first st)) (int \9)))
      `(~(symbol "variable") ~(str "x" (:name e)))
      `(~(symbol "variable") ~(:name e)))))

(defmethod edn->fol "constant" [e]
  `(~(symbol "constant") ~(:value e)))

;; ## Multimethod: fol->edn
(defmulti fol->edn first)

(defmethod fol->edn (symbol "disjunction") [formula]
  {:type "disjunction"
   :formulae (into [] (map fol->edn (rest formula)))})

(defmethod fol->edn (symbol "conjunction") [formula]
  {:type "conjunction"
   :formulae (into [] (map fol->edn (rest formula)))})

(defmethod fol->edn (symbol "negation") [formula]
  {:type "negation"
   :formula (fol->edn (second formula))})

(defmethod fol->edn (symbol "predicate") [formula]
  {:type "predicate"
   :name (second formula)
   :arguments (into [] (map fol->edn (rest (last formula))))})

(defmethod fol->edn (symbol "function") [formula]
  {:type "function"
   :name (second formula)
   :parameters (into [] (map fol->edn (rest (last formula))))})

(defmethod fol->edn (symbol "variable") [formula]
  {:type "variable"
   :name (second formula)})

(defmethod fol->edn (symbol "constant") [formula]
  {:type "constant"
   :value (second formula)})

;; ## Entry point
(defn operetta->fol [operetta]
  (fol/normal-form (fol/operator operetta)))

(defn operetta->edn [operetta]
  (fol->edn (operetta->fol operetta)))

(defn fol->json [formula]
  (json/write-str (fol->edn formula)))

(defn json->edn [json]
  (json/read-str json :key-fn keyword))

(defn json->fol [json]
  (edn->fol (json->edn json)))



