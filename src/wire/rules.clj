(ns wire.rules
  (:require [clara.rules :refer :all]
            [clara.rules.dsl :as dsl]
            [clara.rules.compiler :as c]
            [clojure.math.combinatorics :as combo]
            [wire.logic :as logic]
            [wire.model :as model]
            [wire.preds :as preds])
  (:import (wire.preds HasClause Holds Event Norm CountsAs Activation
                       Maintenance Predicate AbstractFact Holds
                       Formula Instantiated Abrogated Fulfilled
                       NormInstanceInjected Violated Repaired SubsetEQ
                       Repair RestrictedCountsAs CountsAsPerm)))

(defn substitute [formula theta]
  formula)

(defn contains-all [theta theta2]
  (every? true? (map #(= (get theta (key %)) (val %)) theta2)))

(defn compatible? [f theta]
  (let [vars (map #(into #{} (mapcat logic/clause->vars %)) (logic/sol f))
        c? (reduce (fn [p n] (or p n)) false (map #(= % (into #{} (keys theta))) vars))]
    #_(println "compatible?" f theta c?)
    c?))

(def base-rules
  [{:lhs '[[?h1 <- HasClause (= ?f formula) (= ?f2 clause)]
           [?h2 <- Holds (= ?f2 formula) (= ?theta substitution)]]
    :rhs '(do
            (println "holds: " (.hashCode ?f))
            (insert! (wire.preds/->Holds ?f ?theta)))}
   {:lhs '[[wire.preds.Event (= ?a asserter) (= ?p content)]]
    :rhs '(do
            (println "event-processed")
            (insert! ?p))}
   {:lhs '[[wire.preds.CountsAs
            (= ?g1 abstract-fact)
            (= ?g2 concrete-fact)
            (= ?s context)]
           [wire.preds.Holds (= ?g1 formula) (= ?theta substitution)]
           [wire.preds.Holds (= ?s formula) (= ?theta2 substitution)]
           [:not [wire.preds.Holds (= ?g2 formula) (= ?theta substitution)]]]
    :rhs '(do
            (println "counts-as-activation")
            (insert-unconditional! (substitute ?g2 ?theta)))}
   {:lhs '[[wire.preds.CountsAs
            (= ?g1 abstract-fact)
            (= ?g2 concrete-fact)
            (= ?s context)]
           [wire.preds.Holds (= ?g1 formula) (= ?theta substitution)]
           [wire.preds.Holds (= ?g2 formula) (= ?theta substitution)]
           [:not [wire.preds.Holds (= ?s formula)]]
           [?f <- wire.preds.Formula (= ?g2 content) (= ?theta grounding)]]
    :rhs '(do
            (println "counts-as-deactivation")
            (retract! ?f))}
   {:lhs '[[wire.preds.RestrictedCountsAs
            (= ?g1 abstract-fact)
            (= ?g2 concrete-fact)
            (= ?s context)
            (= ?a asserter)]
           [wire.preds.Holds (= ?g1 formula) (= ?theta substitution)]
           [wire.preds.Holds (= ?s formula) (= ?theta2 substitution)]
           [wire.preds.CountsAsPerm (= ?g1 abstract-fact) 
            (= ?s context)
            (= ?a asserter)]
           [:not [wire.preds.Holds (= ?g2 formula) (= ?theta substitution)]]]
    :rhs '(do
            (println "counts-as-activation")
            (insert-unconditional! (substitute ?g2 ?theta)))}
   {:lhs '[[wire.preds.RestrictedCountsAs
            (= ?g1 abstract-fact)
            (= ?g2 concrete-fact)
            (= ?s context)
            (= ?a asserter)]
           [wire.preds.Holds (= ?g1 formula) (= ?theta substitution)]
           [:not [wire.preds.Holds (= ?s formula)]]
           [:not [wire.preds.CountsAsPerm (= ?g1 abstract-fact) 
                  (= ?s context)
                  (= ?a asserter)]]
           [wire.preds.Holds (= ?g2 formula) (= ?theta substitution)]
           [?f <- wire.preds.Formula (= ?g2 content) (= ?theta grounding)]]
    :rhs '(do
            (println "counts-as-deactivation")
            (retract! ?f))}
   #_{:lhs '[[?n <- Norm]
           [:not [wire.preds.Abrogated (= ?n norm)]]]
    :rhs '(do
            (println "norm-abrogation")
            (insert-unconditional! (wire.preds/->Abrogated ?n)))}
   {:lhs '[[?n <- Norm]
           [?a <- wire.preds.Activation (= ?n norm) (= ?f formula)]]
    :rhs '(do
            (println "activation exists" (.hashCode ?f)))}
   {:lhs '[[?n <- Norm]
           [?n2 <- Norm]
           [?a <- wire.preds.Activation (= ?n norm) (= ?f formula)]
           [?h <- wire.preds.Holds (= ?f formula) (= ?theta substitution)]
           [:not [wire.preds.Fulfilled (= ?n norm) (= ?theta substitution)]]
           [:not [wire.preds.Abrogated (= ?n norm)]]
           [:not [wire.preds.Repair (= ?n2 norm) (= ?n repair-norm)]]]
    :rhs '(do
            (println "norm-instantiation")
            (insert-unconditional! (wire.preds/->Instantiated ?n ?theta)))}
   {:lhs '[[?n <- Norm]
           [?n2 <- Norm]
           [?i <- wire.preds.NormInstanceInjected
            (= ?n norm) (= ?theta substitution)]
           [:not [wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]]
           [:not [wire.preds.Repair (= ?n2 norm) (= ?n repair-norm)]]]
    :rhs '(do
            (println "injecting instantiated norm")
            (insert-unconditional! (wire.preds/->Instantiated ?n ?theta)))}
   {:lhs '[[?n <- Norm]
           [?n2 <- Norm]
           [?i <- wire.preds.NormInstanceInjected
            (= ?n norm) (= ?theta substitution)]
           [:not [wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]]
           [:not [wire.preds.Repair (= ?n2 norm) (= ?n repair-norm)]]]
    :rhs '(do
            (println "injecting instantiated norm")
            (insert-unconditional! (wire.preds/->Instantiated ?n ?theta)))}
   {:lhs '[[?n <- Norm]
           [?a <- wire.preds.Maintenance (= ?n norm) (= ?f formula)]
           [wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]
           [wire.preds.SubsetEQ (= ?theta2 subset) (= ?theta superset)]
           [:not 
            [wire.preds.Holds (= ?f formula) (= ?theta2 substitution)]]
           [:not [wire.preds.Violated (= ?n norm) (= ?theta substitution)]]
           [:test (compatible? ?f ?theta2)]]
    :rhs '(do
            (println "norm-violation" ?n ?theta ?theta2)
            (insert-unconditional! (wire.preds/->Violated ?n ?theta)))}
   {:lhs '[[wire.preds.Expiration (= ?n norm) (= ?f formula)]
           [?ni <- wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]
           [wire.preds.SubsetEQ (= ?theta2 subset) (= ?theta superset)]
           [wire.preds.Holds (= ?f formula) (= ?theta2 substitution)]]
    :rhs '(do
            (println "norm-instance-fulfillment")
            (retract! ?ni)
            (insert-unconditional! (wire.preds/->Fulfilled ?n ?theta)))}
   {:lhs '[[?ni <- wire.preds.Violated (= ?n norm) (= ?theta substitution)]
           [wire.preds.Repair (= ?n norm) (= ?n2 repair-norm)]
           [wire.preds.Fulfilled (= ?n2 norm) (= ?theta substitution)]]
    :rhs '(do
            (println "norm-instance-violation-repaired")
            (retract! ?ni))}
   {:lhs '[[?h1 <- wire.preds.Holds (= ?f formula) (= ?theta substitution)]
           [?h2 <- wire.preds.Holds (= ?f2 formula) (= ?theta2 substitution)]
           [:test (contains-all ?theta ?theta2)]]
    :rhs '(do
            #_(println "subseteq" ?theta ?theta2)
            (insert! (wire.preds/->SubsetEQ ?theta2 ?theta)))}
   {:lhs '[[?h1 <- wire.preds.Holds (= ?theta substitution)]]
    :rhs '(when (> (count ?theta) 1)
            (let [all-thetas (map (fn [x] (into {} x))
                                  (filter #(not (or (= 0 (count %)) (= (count ?theta) (count %))))
                                          (combo/subsets (into [] ?theta))))]
              #_(println all-thetas)
              (dorun (map #(do
                             #_(println "subseteq" ?theta %)
                             (insert! (wire.preds/->SubsetEQ % ?theta)))
                          all-thetas))))}
   {:lhs '[[?a <- wire.preds.Predicate]]
    :rhs '(println "Predicate!" ?a)}
   {:lhs '[]
    :rhs '(println "rule engine started")}])

(defn production-rule [rule]
  (let [{:keys [lhs rhs]} rule]
    #_(println lhs rhs)
    (eval `(dsl/parse-rule ~lhs ~rhs))))

#_(let [[new-inserts new-rules] (logic/norm->inserts model/example-norm)
      [new-inserts-2 new-rules-2] (logic/norm->inserts model/example-norm-2)
      all-rules (concat base-rules [] new-rules new-rules-2)
      rules (map production-rule all-rules)
      empty-session (c/mk-session [rules])
      session (apply insert empty-session (concat new-inserts new-inserts-2))]
  (-> session
      (insert (preds/map->Predicate {:name :enacts-role :argument-0 "x"
                                     :argument-1 "y"}))
      (insert (preds/map->Predicate {:name :driving :argument-0 "x"}))
      (insert (preds/map->Predicate {:name :test :argument-0 "y"}))
      (fire-rules))
  #_rules)

(let [[new-inserts new-rules] (logic/norm->inserts model/example-norm)
      [new-inserts-2 new-rules-2] [nil nil] #_(logic/norm->inserts model/example-norm-2)
      all-rules (concat base-rules [] new-rules new-rules-2)
      rules (map production-rule all-rules)
      empty-session (c/mk-session [rules])
      session (apply insert empty-session (concat new-inserts new-inserts-2))]
  (-> session
      (insert (preds/map->Predicate {:name :enacts-role :argument-0 "x"
                                     :argument-1 "y"}))
      (insert (preds/map->Predicate {:name :driving :argument-0 "x"}))
      (insert (preds/map->Predicate {:name :test :argument-0 "y"}))
      (insert (preds/map->Predicate {:name :crossed-red :argument-0 "x"}))
      (fire-rules)))

#_(defprotocol Monitor
   "Protocol that defines basic monitoring operations"
   (has-violated? [session agent-id] "Returns the list of norm instances violated by agent-id")
   (status 
     ([session] "Returns a list of all norm instances along its current status and related agent if applies")
   ([session agent-id] "Returns a list of all norm instances' status")
   ([session norm-id] "Returns the status of given norm instance id "))
   (how2repair [session norm-id] "Returns the repair formula of norm instance norm-id"))
