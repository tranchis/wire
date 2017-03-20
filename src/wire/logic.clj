(ns wire.logic
  (:require [wire.model :as m]
            [clara.rules :refer :all]
            [rolling-stones.core :as sat]
            [wire.preds :as preds]))

(defn sol [wff]
  (binding [*ns* (the-ns 'rolling-stones.core)]
    (let [res (sat/solutions-symbolic-formula (eval (m/valid-wff? wff)))]
      (if (< (count res) 2)
        [res]
        res))))

#_(sol (:norm/fa m/example-norm))

(defn formula->inserts [f]
  (let [clauses (sol f)]
    (map #(preds/->HasClause f %) clauses)))

(defn param-converted [idx p]
  (let [k (symbol (str "argument-" idx))]
    (if (keyword? p)
      `(~(symbol "=") ~(symbol (str "?" (name p))) ~k)
      `(~(symbol "=") ~p ~k))))

(defn predicate->binding [cl]
  (if (vector? cl)
    `[wire.preds.Predicate (~(symbol "=") ~(first cl) ~(symbol "name"))
      ~@(map-indexed param-converted (rest cl))]
    `[:not [wire.preds.Predicate
            (~(symbol "=") ~(first (:literal cl)) ~(symbol "name"))
            ~@(map-indexed param-converted (rest (:literal cl)))]]))

(defn clause->rule [clause]
  (let [all-variables (into #{} (remove nil? (mapcat #(filter keyword? (drop 1 %))
                                                     clause)))]
    {:lhs `[~@(map predicate->binding 
                   clause)]
     :rhs `(let [substitution# ~(apply hash-map
                                       (interleave all-variables
                                                   (map #(symbol (str "?" (name %)))
                                                        all-variables)))]
             (println "Holds! " ~clause substitution#)
             (insert! (preds/->Holds ~clause substitution#)))}))

(defn norm->inserts [norm]
  (let [implementation (:implementation norm)
        mappings-formulas [(:norm/fa implementation) (:norm/fm implementation)
                           (:norm/fd implementation) (:norm/fr implementation)]
        inserts-mappings (mapcat formula->inserts mappings-formulas)
        rules-clauses (map clause->rule (mapcat sol mappings-formulas))
        insert-fa (preds/->Activation norm (:norm/fa implementation))
        insert-fm (preds/->Maintenance norm (:norm/fm implementation))
        insert-fd (preds/->Expiration norm (:norm/fd implementation))
        insert-fr (preds/->Repair norm (:norm/fr implementation))
        formulas-timeout (sol (:norm/timeout implementation))]
    [(concat inserts-mappings [insert-fa insert-fm insert-fd insert-fr norm])
     rules-clauses]))
