(ns eu.superhub.wp4.monitor.core.lisp-to-clara
  (:require [clojure.pprint :as ppr]
            [clojure.string :as stt]
            [clara.rules :refer :all]
            [eu.superhub.wp4.monitor.core.fol-conversions :as folc]
            [eu.superhub.wp4.monitor.core.regulative-parser :as regp]))

(defrecord Norm [norm-id activation maintenance expiration])
(defrecord Activation [norm-id formula])
(defrecord Predicate [name argument-0 argument-1 argument-2 argument-3
                      argument-4 argument-5 argument-6 argument-7 argument-8 
                      argument-9 argument-10 argument-11 argument-12])
(defrecord Holds [norm-id type substitution])

(defmulti argument->literal (fn [a _] (:type a)))

(defmethod argument->literal "variable" [variable idx]
  (let [name (:name variable)]
    `(= ~(symbol (str "?" name)) ~(str "argument-" idx))))

(defmethod argument->literal "constant" [constant idx]
  `(= ~(:value constant) ~(str "argument-" idx)))

(defmulti rule-atom (fn [a _] (:type a)))

(defmethod rule-atom "predicate" [predicate]
  (let [vars (filter #(= (:type %) "variable") (:arguments predicate))]
    `[Predicate (= :name ~(:name predicate))
      ~@(map-indexed (fn [idx arg] (argument->literal arg idx))
                     (:arguments predicate))]))

(defmethod rule-atom "negation" [negation]
  `[:not (rule-atom (:formula negation))])

(defn atom->variables [atom]
  (if (= (:type atom) "predicate")
    (map :name (filter #(= (:type %) "variable") (:arguments atom)))
    (atom->variables (:formula atom))))

(defn rule-clause [idx norm-id type formula]
  (let [atoms (:formulae formula)
        variables (into #{} (mapcat atom->variables atoms))]
    `(defrule ~(read-string (str "norm-" norm-id "-" (name type)))
         ~(str (clojure.string/capitalize (name type)) " condition for norm "
               norm-id)
         ~@(map rule-atom atoms)
         =>
         (insert (->Holds
                   ~norm-id
                   ~type ~(apply merge (map #(hash-map
                                               %
                                               (symbol (str "?" %)))
                                            variables)))))))

(defn rule-condition [norm-id condition]
  (let [type (key condition)
        formula (val condition)]
    (map-indexed (fn [idx b]
                   (rule-clause idx norm-id type b)) (:formulae formula))))

(defn rule-norm [norm]
  (let [conds (:conditions norm)]
    #_(->Norm (:norm-id norm))
    (map #(rule-condition (:norm-id norm) %) conds)))

(defn ^Package opera-to-drools [^String st]
  (let [data (regp/parse-file st)]
    #_(clojure.pprint/pprint data)
    (map clojure.pprint/pprint (map rule-norm (:norms data)))))

(opera-to-drools (.getPath (clojure.java.io/resource "TestOpera.opera")))
