(ns wire.rules
  (:require [clara.rules :refer :all]
            [clara.rules.dsl :as dsl]
            [clara.rules.compiler :as c])
  (:import (wire.preds HasClause Holds Event Norm CountsAs Activation
                       Maintenance Predicate AbstractFact Holds
                       Formula Instantiated Abrogated Fulfilled
                       NormInstanceInjected Violated Repaired SubsetEQ
                       Repair RestrictedCountsAs CountsAsPerm)))

(defn substitute [formula theta]
  formula)

(defn contains-all [theta theta2]
  (every? true? (map #(= (get theta (key %)) (val %)) theta2)))

(def base-rules
  [{:lhs '[[?h1 <- HasClause (= ?f formula) (= ?f2 clause)]
           [?h2 <- Holds (= ?f2 formula) (= ?theta substitution)]]
    :rhs '(do
            (println "holds: " (.hashCode ?h1) " " (.hashCode ?h2)
                     " " (.hashCode ?f) " " (.hashCode ?f2))
            (insert! (->Holds ?f ?theta)))}
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
   {:lhs '[[?n <- Norm]
           [:not [wire.preds.Abrogated (= ?n norm)]]]
    :rhs '(do
            (println "norm-abrogation")
            (insert-unconditional! (->Abrogated ?n)))}
   {:lhs '[[?n <- Norm]
           [?n2 <- Norm]
           [?a <- wire.preds.Activation (= ?n norm) (= ?f formula)]
           [?h <- wire.preds.Holds (= ?f formula) (= ?theta substitution)]
           [:not [wire.preds.Fulfilled (= ?n norm) (= ?theta substitution)]]
           [:not [wire.preds.Abrogated (= ?n norm)]]
           [:not [wire.preds.Repair (= ?n2 norm) (= ?n repair-norm)]]]
    :rhs '(do
            (println "norm-instantiation")
            (insert-unconditional! (->Instantiated ?n ?theta)))}
   {:lhs '[[?n <- Norm]
           [?n2 <- Norm]
           [?i <- wire.preds.NormInstanceInjected
            (= ?n norm) (= ?theta substitution)]
           [:not [wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]]
           [:not [wire.preds.Repair (= ?n2 norm) (= ?n repair-norm)]]]
    :rhs '(do
            (println "injecting instantiated norm")
            (insert-unconditional! (->Instantiated ?n ?theta)))}
   {:lhs '[[?n <- Norm]
           [?n2 <- Norm]
           [?i <- wire.preds.NormInstanceInjected
            (= ?n norm) (= ?theta substitution)]
           [:not [wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]]
           [:not [wire.preds.Repair (= ?n2 norm) (= ?n repair-norm)]]]
    :rhs '(do
            (println "injecting instantiated norm")
            (insert-unconditional! (->Instantiated ?n ?theta)))}
   {:lhs '[[?n <- Norm]
           [?a <- wire.preds.Maintenance (= ?n norm) (= ?f formula)]
           [wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]
           [wire.preds.SubsetEQ (= ?theta2 subset) (= ?theta superset)]
           [:not 
            [?h <- wire.preds.Holds (= ?f formula) (= ?theta2 substitution)]]
           [:not [wire.preds.Violated (= ?n norm) (= ?theta substitution)]]]
    :rhs '(do
            (println "norm-violation")
            (insert-unconditional! (->Violated ?n ?theta)))}
   {:lhs '[[wire.preds.Expiration (= ?n norm) (= ?f formula)]
           [?ni <- wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]
           [wire.preds.SubsetEQ (= ?theta2 subset) (= ?theta superset)]
           [wire.preds.Holds (= ?f formula) (= ?theta2 substitution)]]
    :rhs '(do
            (println "norm-instance-fulfillment")
            (retract! ?ni)
            (insert-unconditional! (->Fulfilled ?n ?theta)))}
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
            (println "subseteq")
            (insert! (->SubsetEQ ?theta2 ?theta)))}
   {:lhs '[]
    :rhs '(println "rule engine started")}])

(defn production-rule [rule]
  (let [{:keys [lhs rhs]} rule]
    (eval `(dsl/parse-rule ~lhs ~rhs))))

(let [rules (map production-rule base-rules)
      session (c/mk-session* rules [])]
  (-> session
      (insert (->ClientRepresentative "Alice" "Acme")
                (->SupportRequest "Acme" :high))
      (fire-rules)))
