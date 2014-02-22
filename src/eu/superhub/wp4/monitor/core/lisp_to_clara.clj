(ns eu.superhub.wp4.monitor.core.lisp-to-clara
  (:require [clojure.pprint :as ppr]
            [clojure.string :as stt]
            [clara.rules :refer :all]
            [eu.superhub.wp4.monitor.core.fol-conversions :as folc]
            [eu.superhub.wp4.monitor.core.regulative-parser :as regp]))

(defrecord Norm [norm-id activation maintenance expiration])
(defrecord CountsAs [norm-id abstract-fact context concrete-fact])
(defrecord Activation [norm formula])
(defrecord Expiration [norm formula])
(defrecord Maintenance [norm formula])
(defrecord Predicate [name argument-0 argument-1 argument-2 argument-3
                      argument-4 argument-5 argument-6 argument-7 argument-8 
                      argument-9 argument-10 argument-11 argument-12])
(defrecord AbstractFact [norm formula])
(defrecord Holds [clause substitution])

(defmulti argument->literal (fn [a _] (:type a)))

(defmethod argument->literal "variable" [variable idx]
  (let [name (:name variable)]
    `(= ~(symbol (str "?" name)) ~(str "argument-" idx))))

(defmethod argument->literal "constant" [constant idx]
  `(= ~(:value constant) ~(str "argument-" idx)))

(defmulti rule-atom :type)

(defmethod rule-atom "predicate" [predicate]
  (let [vars (filter #(= (:type %) "variable") (:arguments predicate))]
    `[Predicate (= :name ~(:name predicate))
      ~@(map-indexed (fn [idx arg] (argument->literal arg idx))
                     (:arguments predicate))]))

(defmethod rule-atom "negation" [negation]
  `[:not ~(rule-atom (:formula negation))])

(defn atom->variables [atom]
  (if (= (:type atom) "predicate")
    (map :name (filter #(= (:type %) "variable") (:arguments atom)))
    (atom->variables (:formula atom))))

(defn rule-clause [idx norm-id type formula]
  (let [atoms (:formulae formula)
        variables (into #{} (mapcat atom->variables atoms))
        str-type (apply str (map clojure.string/capitalize
                                 (clojure.string/split (name type) #"-")))
        str-norm-type (if (= type :abstract-fact)
                        "counts-as"
                        "norm")]
    `(defrule ~(read-string (str str-norm-type "-"
                                 norm-id "-" (name type) "-" idx))
       ~(str str-type " condition for " str-norm-type " "
             norm-id)
       [?n ~(symbol "<-") ~(if (= type :abstract-fact)
                             CountsAs
                             Norm)
        (= ~norm-id ~(symbol "norm-id"))]
       [~(read-string str-type)
        (= ?n ~(symbol "norm")) (= ?f ~(symbol "formula"))]
       ~@(map rule-atom atoms)
       =>
       (insert (->Holds
                 ~formula
                 ~(apply merge (map #(hash-map
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
    (mapcat #(rule-condition (:norm-id norm) %) conds)))

(defn rule-counts-as [counts-as]
  ;; TODO: Handle contexts in a better way!
  (let [cid (int (* 10000 (java.lang.Math/random)))]
    (mapcat #(rule-condition (str cid) %)
            (select-keys counts-as [:abstract-fact]))))

(defn ^Package opera-to-drools [^String st]
  (let [data (regp/parse-file st)
        rules (concat (mapcat rule-norm (:norms data))
                      (mapcat rule-counts-as (:cas-rules data)))]
    rules))

(opera-to-drools (.getPath (clojure.java.io/resource "TestOpera.opera")))
