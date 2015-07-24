(ns eu.superhub.wp4.monitor.core.lisp-to-clara
  (:require [clojure.pprint :as ppr]
            [clojure.string :as stt]
            [clara.rules :refer :all]
            [clara.tools.viz :as viz]
            [eu.superhub.wp4.monitor.core.fol-conversions :as folc]
            [eu.superhub.wp4.monitor.core.regulative-parser :as regp]))

(defrecord Norm [norm-id])
(defrecord CountsAs [abstract-fact context concrete-fact])
(defrecord Activation [norm formula])
(defrecord Expiration [norm formula])
(defrecord Maintenance [norm formula])
(defrecord Predicate [name argument-0 argument-1 argument-2 argument-3
                      argument-4 argument-5 argument-6 argument-7 argument-8 
                      argument-9 argument-10 argument-11 argument-12])
(defrecord AbstractFact [norm formula])
(defrecord Holds [formula substitution])
(defrecord HasClause [formula clause])
(defrecord Event [asserter content])
(defrecord Formula [content grounding])
(defrecord Instantiated [norm substitution])
(defrecord Fulfilled [norm substitution])
(defrecord Violated [norm substitution])
(defrecord Repaired [norm substitution])
(defrecord SubsetEQ [subset superset])
(defrecord Repair [norm repair-norm])

(defmulti argument->literal (fn [a _] (:type a)))

(defmethod argument->literal "variable" [variable idx]
  (let [name (:name variable)]
    `(= ~(symbol (str "?" name)) ~(read-string (str "argument-" idx)))))

(defmethod argument->literal "constant" [constant idx]
  `(= ~(:value constant) ~(read-string (str "argument-" idx))))

(defmulti rule-atom :type)

(defmethod rule-atom "predicate" [predicate]
  (let [vars (filter #(= (:type %) "variable") (:arguments predicate))]
    `[Predicate (= ~(:name predicate) ~(symbol "name"))
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
                   [clara.tools.viz :as viz]
                   [eu.superhub.wp4.monitor.core.lisp-to-clara :as lc :refer :all]
                   [eu.superhub.wp4.monitor.core.fol-conversions :as folc]
                   [eu.superhub.wp4.monitor.core.regulative-parser :as regp])))

    (println *ns*)
    (binding [*ns* (find-ns id)]
      (eval '(defrule holds
               "holds"
               [?h1 <- eu.superhub.wp4.monitor.core.lisp_to_clara.HasClause (= ?f formula) (= ?f2 clause)]
               [?h2 <- eu.superhub.wp4.monitor.core.lisp_to_clara.Holds (= ?f2 formula) (= ?theta substitution)]
               =>
               (do
                 (println "holds: " (.hashCode ?h1) " " (.hashCode ?h2)
                          " " (.hashCode ?f) " " (.hashCode ?f2))
                 (insert! (->Holds ?f ?theta)))))
      
      (eval '(defrule event-processed
               "event processed"
               [eu.superhub.wp4.monitor.core.lisp_to_clara.Event (= ?a asserter) (= ?p content)]
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
               [eu.superhub.wp4.monitor.core.lisp_to_clara.CountsAs
                (= ?g1 abstract-fact)
                (= ?g2 concrete-fact)
                (= ?s context)]
               [eu.superhub.wp4.monitor.core.lisp_to_clara.Holds (= ?g1 formula) (= ?theta substitution)]
               [eu.superhub.wp4.monitor.core.lisp_to_clara.Holds (= ?s formula) (= ?theta2 substitution)]
               [:not [eu.superhub.wp4.monitor.core.lisp_to_clara.Holds (= ?g2 formula) (= ?theta substitution)]]
               =>
               (do
                 (println "counts-as-activation")
                 (insert-unconditional! (substitute ?g2 ?theta)))))
      
      (eval '(defrule counts-as-deactivation
               "counts-as deactivation"
               [eu.superhub.wp4.monitor.core.lisp_to_clara.CountsAs
                (= ?g1 abstract-fact)
                (= ?g2 concrete-fact)
                (= ?s context)]
               [eu.superhub.wp4.monitor.core.lisp_to_clara.Holds (= ?g1 formula) (= ?theta substitution)]
               [:not [eu.superhub.wp4.monitor.core.lisp_to_clara.Holds (= ?s formula) (= ?theta2 substitution)]]
               [eu.superhub.wp4.monitor.core.lisp_to_clara.Holds (= ?g2 formula) (= ?theta substitution)]
               [?f <- eu.superhub.wp4.monitor.core.lisp_to_clara.Formula (= ?g2 content) (= ?theta grounding)]
               =>
               (do
                 (println "counts-as-deactivation")
                 (retract! ?f))))
      
      (eval '(defrule norm-instantiation
               "norm instantiation"
               [?a <- eu.superhub.wp4.monitor.core.lisp_to_clara.Activation (= ?n norm) (= ?f formula)]
               [?h <- eu.superhub.wp4.monitor.core.lisp_to_clara.Holds (= ?f formula) (= ?theta substitution)]
               [:not [eu.superhub.wp4.monitor.core.lisp_to_clara.Instantiated (= ?n norm) (= ?theta substitution)]]
               [:not [eu.superhub.wp4.monitor.core.lisp_to_clara.Repair (= ?n2 norm) (= ?n repair-norm)]]
               =>
               (do
                 (println "norm-instantiation")
                 (insert-unconditional! (->Instantiated ?n ?theta)))))
      
      (eval '(defrule norm-instance-fulfillment
               "norm instance fulfillment"
               [eu.superhub.wp4.monitor.core.lisp_to_clara.Expiration (= ?n norm) (= ?f formula)]
               [?ni <- eu.superhub.wp4.monitor.core.lisp_to_clara.Instantiated (= ?n norm) (= ?theta substitution)]
               [eu.superhub.wp4.monitor.core.lisp_to_clara.SubsetEQ (= ?theta2 subset) (= ?theta superset)]
               [eu.superhub.wp4.monitor.core.lisp_to_clara.Holds (= ?f formula) (= ?theta2 substitution)]
               =>
               (do
                 (println "norm-instance-fulfillment")
                 (retract! ?ni)
                 (insert-unconditional! (->Fulfilled ?n ?theta)))))
      
      (eval '(defrule norm-instance-violation-repaired
               "norm instance violation repaired"
               [?ni <- eu.superhub.wp4.monitor.core.lisp_to_clara.Violated (= ?n norm) (= ?theta substitution)]
               [eu.superhub.wp4.monitor.core.lisp_to_clara.Repair (= ?n norm) (= ?n2 repair-norm)]
               [eu.superhub.wp4.monitor.core.lisp_to_clara.Fulfilled (= ?n2 norm) (= ?theta substitution)]
               =>
               (do
                 (println "norm-instance-violation-repaired")
                 (retract! ?ni))))
      
      (eval '(defrule subseteq
               "subseteq"
               [?h1 <- eu.superhub.wp4.monitor.core.lisp_to_clara.Holds (= ?f formula) (= ?theta substitution)]
               [?h2 <- eu.superhub.wp4.monitor.core.lisp_to_clara.Holds (= ?f2 formula) (= ?theta2 substitution)]
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
               [?n <- eu.superhub.wp4.monitor.core.lisp_to_clara.Instantiated])))

(ns eu.superhub.wp4.monitor.core.lisp-to-clara)
(find-ns id)))

(defn start-engine []
  (let [br (base-rules)
        specification (opera-to-drools
                        (.getPath
                          (clojure.java.io/resource "TestOpera.opera")))]
    (dorun (map #(binding [*ns* br] (println %) (eval %)) (:rules specification)))
    #_(ppr/pprint (:rules specification))
    (let [session (->
                    (mk-session (ns-name br) :cache false)
                    (insert (map->Predicate {:name "NumberOfWorkers"
                                             :argument-0 "luis"}))
                    (insert (map->Predicate {:name "lessThan"
                                             :argument-0 "luis"
                                             :argument-1 "5"})))
          session-all (apply insert session (:inserts specification))
          ti (first (filter #(= (name (key %)) "test-instantiated")
                            (ns-map br)))]
      #_(spit "/tmp/map.txt" (pr-str (ns-map br)))
      (query (fire-rules session-all) @(val ti)))))

(time (start-engine))
#_(base-rules)

