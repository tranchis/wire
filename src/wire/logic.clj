(ns wire.logic
  (:require [wire.preds :as preds]
            [wire.model :as model]
            [clara.rules :refer :all]
            [rolling-stones.core :as sat]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]))

(defn bang->not [formula]
  (if (instance? rolling_stones.core.Not formula)
    [:NOT (bang->not (:literal formula))]
    (if (sequential? formula)
      (into (empty formula) (map bang->not formula))
      formula)))

(defn sol [wff]
  (binding [*ns* (the-ns 'rolling-stones.core)]
    (if (sequential? wff)
      (let [sols (bang->not (sat/solutions-symbolic-formula (eval wff)))]
        (if (vector? wff)
          #{#{(first sols)}}
          (into #{} (map #(into #{} %) sols))))
      #{#{wff}})))

(s/fdef sol :args ::model/wff)

#_(sol (:norm/fa m/example-norm))

(defn formula->inserts [f]
  (let [clauses (sol f)]
    (map #(preds/->HasClause f %) clauses)))

(defn param-converted [idx p]
  (let [k (symbol (str "argument-" idx))]
    (if (keyword? p)
      `(~(symbol "=") ~(symbol (str "?" (name p))) ~k)
      `(~(symbol "=") ~p ~k))))

(defn generate-checkers [param]
  (let [individual-checkers (map (fn [idx]
                                   `[wire.preds.Predicate
                                     (~(symbol "=")
                                      ~(symbol (str "?" (name param)))
                                      ~(symbol (str "argument-" idx)))])
                                 (range 13))]
    `[:or ~@individual-checkers]))

(defn generate-negative [literal]
  #_(log/info "generate-negative" literal)
  (let [main `[:not [wire.preds.Predicate
                     (~(symbol "=") ~(first literal) ~(symbol "name"))
                     ~@(map-indexed param-converted (rest literal))]]
        vars (into #{} (map keyword (rest literal)))
        not-null (map (fn [x] `[:test (not (nil? ~(symbol (str "?" (name x)))))])
                      vars)
        checkers (map generate-checkers vars)]
    `[:and ~main ~@checkers ~@not-null]))

(defn predicate->binding [cl]
  #_(log/info "pb" cl)
  #_(log/info (vector? cl) cl)
  (if (= (first cl) :NOT) #_(instance? rolling_stones.core.Not cl)
    (generate-negative (second cl))
    `[wire.preds.Predicate (~(symbol "=") ~(first cl) ~(symbol "name"))
      ~@(map-indexed param-converted (rest cl))]))

(defn clause->vars [cl]
  #_(log/info "cl" cl)
  (if (instance? rolling_stones.core.Not cl)
    (filter keyword? (drop 1 (:literal cl)))
    (if (= :NOT (first cl))
      (filter keyword? (drop 1 (second cl)))
      (filter keyword? (drop 1 cl)))))

(defn clause->rule [clause]
  #_(log/info "clause" clause)
  (let [all-variables (into #{} (remove nil? (mapcat clause->vars clause)))]
    {:lhs `[~@(map predicate->binding clause)]
     :rhs `(let [substitution# ~(apply hash-map
                                       (interleave all-variables
                                                   (map #(symbol (str "?" (name %)))
                                                        all-variables)))]
             (log/info "Holds!" ~(pr-str clause) substitution#)
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
    #_(println rules-clauses)
    [(concat inserts-mappings [insert-fa insert-fm insert-fd insert-fr norm])
     rules-clauses]))
