(ns wire.monitor
  (:require [wire.preds :as preds]
            [wire.logic :as logic]
            [wire.rules :refer :all]
            [wire.model :as model]
            [clojure.math.combinatorics :as combo]
            [clara.rules.compiler :as c]
            [clara.rules :refer :all]))

(defprotocol Monitor
  "Protocol that defines basic monitoring operations"
  (has-violated? [this agent-id])
  (status [this])
  (agent-status [this agent-id])
  (norm-status [this norm-id])
  (how2repair [this norm-id])
  (add-fact [this fact])
  (remove-fact [this fact])
  (facts [this]))

(extend-type clara.rules.engine.LocalSession
  Monitor
  (has-violated? [this agent-id]
    (->> (query this {:lhs [{:type wire.preds.Violated
                             :constraints []
                             :fact-binding :?viol}]
                      :params #{:?agent-id}})
         (map :?viol)
         (into #{})))
  (status [this]
    (->> (query this {:lhs [{:type wire.preds.Instantiated
                             :constraints []
                             :fact-binding :?inst}]
                      :params #{}})
         (map :?inst)
         (into #{})))
  (agent-status [this agent-id])
  (norm-status [this norm-id])
  (how2repair [this norm-id])
  (add-fact [this fact]
    (let [full-fact (concat fact (take (- 14 (count fact)) (repeat nil)))]
      (insert this (apply preds/->Predicate full-fact))
      (fire-rules this)))
  (facts [this])
  (remove-fact [this fact]
    (let [full-fact (concat fact (take (- 14 (count fact)) (repeat nil)))]
      (retract this (apply preds/->Predicate full-fact))
      (fire-rules this))))

(defn monitor [norm-model]
  (let [[new-inserts new-rules] (logic/norm->inserts norm-model)
        all-queries (map production-query queries)
        all-rules (concat base-rules [] new-rules)
        rules (map production-rule all-rules)
        rulebase (concat rules all-queries)
        empty-session (c/mk-session [rulebase])]
    (apply insert empty-session new-inserts)))

#_(monitor model/example-norm)
