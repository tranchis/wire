;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Copyright (c) 2015 Sergio Alvarez and Ignasi Gómez Sebastià
; 
; All rights reserved. This program and the accompanying materials
; are made available under the terms of the Eclipse Public License v1.0
; which accompanies this distribution, and is available at
; http://www.eclipse.org/legal/epl-v10.html
; 
; Contributors:
;     Sergio Alvarez: Original wire project
;     Ignasi Gómez-Sebastià: Wire project extensions
;                           - (2015-09-14) Extending original wire monitor to support merging of active instances
;                             
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns eu.superhub.wp4.monitor.core.lisp-to-clara
  (:require [clojure.pprint :as ppr]
            [clojure.string :as stt]
            [clara.rules :refer :all]
            [wire.preds :refer :all]
            [eu.superhub.wp4.monitor.core.fol-conversions :as folc]
            [eu.superhub.wp4.monitor.core.regulative-parser :as regp])
  (:use 
    [clojure.tools.logging :only (info error)]))

(defmulti argument->literal (fn [a _] (:type a)))

(defmethod argument->literal "variable" [variable idx]
  (let [name (:name variable)]
    `(= ~(symbol (str "?" name)) ~(read-string (str "argument-" idx)))))

(defmethod argument->literal "constant" [constant idx]
  `(= ~(:value constant) ~(read-string (str "argument-" idx))))

(defmulti rule-atom :type)

(defmethod rule-atom "predicate" [predicate]
  (let [vars (filter #(= (:type %) "variable") (:arguments predicate))]
    `[wire.preds.Predicate (= ~(:name predicate) ~(symbol "name"))
      ~@(map-indexed (fn [idx arg] (argument->literal arg idx))
                     (:arguments predicate))]))

(defmethod rule-atom "negation" [negation]
  `[:not ~(rule-atom (:formula negation))])

(defn atom->variables [atom]
  (if (= (:type atom) "predicate")
    (map :name (filter #(= (:type %) "variable") (:arguments atom)))
    (atom->variables (:formula atom))))

(defn rule-clause [clause]
  {:pre [(= "conjunction" (:type clause))]}
  (let [atoms (:formulae clause)
        norm-id (str (int (* 1000 (java.lang.Math/random))))
        variables (into #{} (mapcat atom->variables atoms))]
    `(defrule ~(read-string (str "clause-" norm-id))
       ~@(map rule-atom atoms)
       ~(symbol "=>")
       (do
         (println "Holds: " ~(hash clause) ~(pr-str clause))
         (insert! (->Holds
                    ~clause
                    ~(apply merge (map #(hash-map
                                          %
                                          (symbol (str "?" %)))
                                       variables))))))))

(defn rule-condition [norm-id condition]
  {:pre [(= "disjunction" (:type (val condition)))]}
  (let [type (key condition)
        formula (val condition)]
    (apply merge-with concat
           (map-indexed (fn [idx b]
                          {:formulae [{b [formula]}]
                           #_:inserts #_[(->HasClause formula b)]})
                        (:formulae formula)))))

(defn rule-norm [norm]
  (let [conds (:conditions norm)
        production (apply merge-with concat
                          (map #(rule-condition (:norm-id norm) %) conds))
        n (->Norm (:norm-id norm))]
    {:inserts [n
               (->Activation n (:activation (:conditions norm)))
               (->Expiration n (:expiration (:conditions norm)))
               (->Maintenance n (:maintenance (:conditions norm)))]
     :formulae (:formulae production)}))

(defn rule-counts-as [counts-as]
  ;; TODO: Handle contexts in a better way!
  (let [cid (int (* 10000 (java.lang.Math/random)))
        production (apply merge-with concat
                          (map #(rule-condition (str cid) %)
                               (select-keys counts-as [:abstract-fact])))]
    {:inserts (map->CountsAs counts-as)
     :formulae (:formulae production)}))

(defn ^Package opera-to-drools [^String st]
  (let [data (regp/parse-file st)
        rules (apply merge-with concat
                     (concat (map rule-norm (:norms data))
                             (map rule-counts-as (:cas-rules data))))
        clause-relationships (apply merge-with concat (:formulae rules))
        clauses (keys clause-relationships)
        inserts (mapcat #(map (fn [a]
                                (->HasClause a (key %)))
                              (distinct (val %))) clause-relationships)]
    (merge rules {:rules (map rule-clause clauses)
                  :inserts (concat inserts (:inserts rules))})))

(defn base-rules []
  (let [id (symbol (str "rule-ns-" (int (* 1000 (java.lang.Math/random)))))]
    (eval
      `(ns ~id
         (:require [clojure.string :as stt]
                   [clara.rules :refer :all]
                   [eu.superhub.wp4.monitor.core.lisp-to-clara :as lc :refer :all]
                   [eu.superhub.wp4.monitor.core.fol-conversions :as folc]
                   [eu.superhub.wp4.monitor.core.regulative-parser :as regp])))

    (println *ns*)
    (binding [*ns* (find-ns id)]
      (eval '(defrule holds
               "holds"
               [?h1 <- wire.preds.HasClause (= ?f formula) (= ?f2 clause)]
               [?h2 <- wire.preds.Holds (= ?f2 formula) (= ?theta substitution)]
               =>
               (do
                 (println "holds: " (.hashCode ?h1) " " (.hashCode ?h2)
                          " " (.hashCode ?f) " " (.hashCode ?f2))
                 (insert! (->Holds ?f ?theta)))))
      
      (eval '(defrule event-processed
               "event processed"
               [wire.preds.Event (= ?a asserter) (= ?p content)]
               =>
               (do
                 (println "event-processed")
                 (insert! ?p))))
      
      (eval '(defn substitute [formula theta]
               formula))
      
      (eval '(defn contains-all [theta theta2]
               (every? true? (map #(= (get theta (key %)) (val %)) theta2))))
      
      (eval '(defrule counts-as-activation
               "counts-as activation"
               [wire.preds.CountsAs
                (= ?g1 abstract-fact)
                (= ?g2 concrete-fact)
                (= ?s context)]
               [wire.preds.Holds (= ?g1 formula) (= ?theta substitution)]
               [wire.preds.Holds (= ?s formula) (= ?theta2 substitution)]
               [:not [wire.preds.Holds (= ?g2 formula) (= ?theta substitution)]]
               =>
               (do
                 (println "counts-as-activation")
                 (insert-unconditional! (substitute ?g2 ?theta)))))
      
      (eval '(defrule counts-as-deactivation
               "counts-as deactivation"
               [wire.preds.CountsAs
                (= ?g1 abstract-fact)
                (= ?g2 concrete-fact)
                (= ?s context)]
               [wire.preds.Holds (= ?g1 formula) (= ?theta substitution)]
               [:not [wire.preds.Holds (= ?s formula) (= ?theta2 substitution)]]
               [wire.preds.Holds (= ?g2 formula) (= ?theta substitution)]
               [?f <- wire.preds.Formula (= ?g2 content) (= ?theta grounding)]
               =>
               (do
                 (println "counts-as-deactivation")
                 (retract! ?f))))

      (eval '(defrule restricted-counts-as-activation
               "counts-as activation with constitutive power"
               [wire.preds.RestrictedCountsAs
                (= ?g1 abstract-fact)
                (= ?g2 concrete-fact)
                (= ?s context)
                (= ?a asserter)]
               [wire.preds.Holds (= ?g1 formula) (= ?theta substitution)]
               [wire.preds.Holds (= ?s formula) (= ?theta2 substitution)]
               [wire.preds.CountsAsPerm (= ?g1 abstract-fact) 
                                        (= ?s context)
                                        (= ?a asserter)]
               [:not [wire.preds.Holds (= ?g2 formula) (= ?theta substitution)]]
               =>
               (do
                 (println "counts-as-activation")
                 (insert-unconditional! (substitute ?g2 ?theta)))))
      
      (eval '(defrule restricted-counts-as-deactivation
               "counts-as deactivation with constitutive power"
               [wire.preds.RestrictedCountsAs
                (= ?g1 abstract-fact)
                (= ?g2 concrete-fact)
                (= ?s context)
                (= ?a asserter)]
               [wire.preds.Holds (= ?g1 formula) (= ?theta substitution)]
               [:not [wire.preds.Holds (= ?s formula) (= ?theta2 substitution)]]
               [:not [wire.preds.CountsAsPerm (= ?g1 abstract-fact) 
                                              (= ?s context)
                                              (= ?a asserter)]]
               [wire.preds.Holds (= ?g2 formula) (= ?theta substitution)]
               [?f <- wire.preds.Formula (= ?g2 content) (= ?theta grounding)]
               =>
               (do
                 (println "counts-as-deactivation")
                 (retract! ?f))))
      
      (eval '(defrule norm-abrogation
               "norm abrogation"
               [:not [wire.preds.Abrogated (= ?n norm)]]
               =>
               (do
                 (println "norm-abrogation")
                 (insert-unconditional! (->Abrogated ?n)))))

      (eval '(defrule norm-instantiation
               "norm instantiation"
               [?a <- wire.preds.Activation (= ?n norm) (= ?f formula)]
               [?h <- wire.preds.Holds (= ?f formula) (= ?theta substitution)]
               [:not [wire.preds.Fulfilled (= ?n norm) (= ?theta substitution)]]
               [:not [wire.preds.Abrogated (= ?n norm)]]
               [:not [wire.preds.Repair (= ?n2 norm) (= ?n repair-norm)]]
               =>
               (do
                 (println "norm-instantiation")
                 (insert-unconditional! (->Instantiated ?n ?theta)))))

       (eval '(defrule inject-instantiation
               "injecting instantiated norm"
               [?i <- wire.preds.NormInstanceInjected (= ?n norm) 
                                                      (= ?theta substitution)]
               [:not [wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]]
               [:not [wire.preds.Repair (= ?n2 norm) (= ?n repair-norm)]]
               =>
               (do
                 (println "injecting instantiated norm"
                 (insert-unconditional! (->Instantiated ?n ?theta))))))

       (eval '(defrule retract-instantiation
               "injecting instantiated norm"
               [?i <- wire.preds.NormInstanceInjected (= ?n norm) 
                                                      (= ?theta substitution)]
               [:not [wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]]
               [:not [wire.preds.Repair (= ?n2 norm) (= ?n repair-norm)]]
               =>
               (do
                 (println "injecting instantiated norm"
                 (insert-unconditional! (->Instantiated ?n ?theta))))))

        (eval '(defrule norm-instance-violation
               "norm instance violation"
               [?a <- wire.preds.Maintenance (= ?n norm) (= ?f formula)]
               [wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]
               [:not 
                    [:and [wire.preds.SubsetEQ (= ?theta2 subset) (= ?theta superset)]
                    [?h <- wire.preds.Holds (= ?f formula) (= ?theta2 substitution)]]
               ]
               [:not [wire.preds.Violated (= ?n norm) (= ?theta substitution)]]         
               =>
               (do
                 (println "norm-violation")
                 (insert-unconditional! (->Violated ?n ?theta)))))
      
      (eval '(defrule norm-instance-fulfillment
               "norm instance fulfillment"
               [wire.preds.Expiration (= ?n norm) (= ?f formula)]
               [?ni <- wire.preds.Instantiated (= ?n norm) (= ?theta substitution)]
               [wire.preds.SubsetEQ (= ?theta2 subset) (= ?theta superset)]
               [wire.preds.Holds (= ?f formula) (= ?theta2 substitution)]
               =>
               (do
                 (println "norm-instance-fulfillment")
                 (retract! ?ni)
                 (insert-unconditional! (->Fulfilled ?n ?theta)))))
      
      (eval '(defrule norm-instance-violation-repaired
               "norm instance violation repaired"
               [?ni <- wire.preds.Violated (= ?n norm) (= ?theta substitution)]
               [wire.preds.Repair (= ?n norm) (= ?n2 repair-norm)]
               [wire.preds.Fulfilled (= ?n2 norm) (= ?theta substitution)]
               =>
               (do
                 (println "norm-instance-violation-repaired")
                 (retract! ?ni))))
      
      (eval '(defrule subseteq
               "subseteq"
               [?h1 <- wire.preds.Holds (= ?f formula) (= ?theta substitution)]
               [?h2 <- wire.preds.Holds (= ?f2 formula) (= ?theta2 substitution)]
               [:test (contains-all ?theta ?theta2)]
               =>
               (do
                 (println "subseteq")
                 (insert! (->SubsetEQ ?theta2 ?theta)))))
      
      (eval '(defrule rule-engine-started
               "rule engine started"
               =>
               (do
                 (println "rule engine started"))))

      (eval '(defquery test-instantiated
               []
               [?n <- wire.preds.Instantiated]))
    
      ;Norm instances instantiated.
      ;Same as above to avoid breaking legacy code. Improves name
      (eval '(defquery Instantiated
               []
               [?n <- wire.preds.Instantiated]))

      ;Norm instances Violated.
      (eval '(defquery Violated
               []
               [?n <- wire.preds.Violated]))

      ;Norm instances Fulfilled.
      (eval '(defquery Fulfilled
               []
               [?n <- wire.preds.Fulfilled]))

       ;Norm instances Repair.
      (eval '(defquery Repair
               []
               [?n <- wire.preds.Repaired]))

      ;Constitutive norm instances.
      (eval '(defquery CountsAs
               []
               [?n <- wire.preds.CountsAs]))

      ;Hold.
      (eval '(defquery Holds
               []
               [?n <- wire.preds.Holds]))

      ;Shortcut to everything above
      (eval '(defquery Everything
               []
               [:or [?n <- wire.preds.Instantiated]
                    [?n <- wire.preds.Violated]
                    [?n <- wire.preds.Fulfilled]
                    [?n <- wire.preds.Repair]
                    [?n <- wire.preds.CountsAs]])))

(ns eu.superhub.wp4.monitor.core.lisp-to-clara)
(find-ns id)))


(defn all-ti
  [ti session-all]
  (query (fire-rules session-all) @(val ti)))

(defn do-inject
  [norm-instance br session]
  (let [instance (first norm-instance)
        instance (:?n instance)
        norm (:norm instance)
        ;norm (:norm-id norm)
        substitution (:substitution instance)
        _ (info "Injecting norm ' " norm " ' with theta ' " substitution " ' ")
         session (insert session (->NormInstanceInjected norm substitution))

    ]
    session ))

(defn get-instances-in-state
  [session br state]
  (map #(all-ti % session) (filter #(= (name (key %)) state)
                            (ns-map br)))
  )

(defn merge-engines-all
  "Dumps the working memory of a monitor into another monitor"
  [session-main session-aux br]
  (info "Merging active instances...")
  (let [all-aux (map #(all-ti % session-aux) (filter #(= (name (key %)) 
                          "Everything")
                            (ns-map br)))
        all-main (map #(all-ti % session-main) (filter #(= (name (key %)) 
                          "Everything")
                            (ns-map br)))
        ;_ (info "Pelusso:" [all-aux all-main])
        session (doall (map #(do-inject % br session-main) all-aux))
        session (first session)
        all-test (map #(all-ti % session) (filter #(= (name (key %)) 
                          "Everything")
                            (ns-map br)))
         ]
        session
  ))

(defn merge-engines-active
  "Merges sets of active norm instances between 2 rules engine sessions 
  by inserting the set of active instances from the first
   engine into the second. The first engine session belongs to an 
   auxiliary monitor, the second to a main one"
  [session-main session-aux br]
  (info "Merging active instances...")
  (let [active-aux (map #(all-ti % session-aux) (filter #(= (name (key %)) 
                          "Instantiated")
                            (ns-map br)))
        #_ (info "Set of active instances in aux monitor: '" active-aux "'")
        active-main (map #(all-ti % session-main) (filter #(= (name (key %)) 
                          "Instantiated")
                            (ns-map br)))
        #_ (info "Set of active instances in main monitor: '" active-main "'")
        session (doall (map #(do-inject % br session-main) active-aux))
        session (first session)
         ]
        session
  ))

(defn start-engine-merge [file]
  (let [br (base-rules)
        specification (opera-to-drools
                        (.getPath
                          (clojure.java.io/resource file)))]
        (dorun (map #(binding [*ns* br] (println %) (eval %)) 
                                        (:rules specification)))
    (let [session (->
                    (mk-session (ns-name br) :cache false)
                      (insert (map->Predicate {:name "NumberOfWorkers"
                                               :argument-0 "x"}))
                    (insert (map->Predicate {:name "lessThan"
                                             :argument-0 "x"
                                             :argument-1 "5"})))
          session-main (apply insert session (:inserts specification))
          session (->
                    (mk-session (ns-name br) :cache false)
                    (insert (map->Predicate {:name "Unit"
                                             :argument-0 "y"}))
                    (insert (map->Predicate {:name "Unit"
                                             :argument-0 "y"
                                             :argument-1 "Archmage"})))
          session-aux (apply insert session (:inserts specification))
          test (merge-engines-active session-main session-aux br)
          ti (map #(all-ti % test) (filter #(= (name (key %)) "test-instantiated")
                            (ns-map br)))]
          ti
      )))

(defn ^Package operationalise 
  "Operationalises a set of norms"
  [norms]
  (let [data norms
        rules (apply merge-with concat
                     (concat (map rule-norm (:norms data))
                             (map rule-counts-as (:cas-rules data))))
        clause-relationships (apply merge-with concat (:formulae rules))
        clauses (keys clause-relationships)
        inserts (mapcat #(map (fn [a]
                                (->HasClause a (key %)))
                              (distinct (val %))) clause-relationships)]
    (merge rules {:rules (map rule-clause clauses)
                  :inserts (concat inserts (:inserts rules))})))

(defn start-engine-plain [file]
  (let [br (base-rules)
        specification (opera-to-drools
                        (.getPath
                          (clojure.java.io/resource file)))]
    (dorun (map #(binding [*ns* br] (println %) (eval %)) (:rules specification)))
    #_(ppr/pprint specification)
    (let [session (->
                    (mk-session (ns-name br) :cache false)
                    (insert (map->Predicate {:name "NumberOfWorkers"
                                             :argument-0 "x"}))
                    (insert (map->Predicate {:name "lessThan"
                                             :argument-0 "x"
                                             :argument-1 "5"}))
                    (insert (map->Predicate {:name "Produce_New_Soldier"}))
                    )
          session-all (apply insert session (:inserts specification))

          ti (map #(all-ti % session-all) (filter #(= (name (key %)) 
                            "Everything") (ns-map br)))]
          ti
      )))


;Wrapper for tests
(defn start-engine [file]
  (let [result (start-engine-plain file)
        ]
        result ) )

#_(time (start-engine "TestOpera.opera"))
#_(base-rules)

