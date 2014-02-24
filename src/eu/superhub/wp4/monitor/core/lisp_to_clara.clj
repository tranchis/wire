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

(defn rule-clause [idx norm-id type formula]
  {:pre [(= "conjunction" (:type formula))]}
  (let [atoms (:formulae formula)
        variables (into #{} (mapcat atom->variables atoms))
        str-type (apply str (map clojure.string/capitalize
                                 (clojure.string/split (name type) #"-")))
        str-norm-type (if (= type :abstract-fact)
                        "counts-as"
                        "norm")]
    (eval `(defrule ~(read-string (str str-norm-type "-"
                                       norm-id "-" (name type) "-" idx))
             ~(str str-type " condition for " str-norm-type " "
                   norm-id)
             ~@(if (= type :abstract-fact)
                  [`[?n ~(symbol "<-") CountsAs (= ?f ~(symbol "abstract-fact"))]]
                  [`[?n ~(symbol "<-") Norm (= ~norm-id ~(symbol "norm-id"))]
                   `[~(read-string str-type)
                     (= ?n ~(symbol "norm")) (= ?f ~(symbol "formula"))]])
             ~@(map rule-atom atoms)
             ~(symbol "=>")
             (insert! (->Holds
                       ~formula
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
                          {:rules [(rule-clause idx norm-id type b)]
                           :inserts [(->HasClause formula b)]})
                        (:formulae formula)))))

(defn rule-norm [norm]
  (let [conds (:conditions norm)
        production (apply merge-with concat
                          (map #(rule-condition (:norm-id norm) %) conds))
        n (->Norm (:norm-id norm))]
    {:inserts (concat (:inserts production)
                      [n
                       (->Activation n (:activation (:conditions norm)))
                       (->Expiration n (:expiration (:conditions norm)))
                       (->Maintenance n (:maintenance (:conditions norm)))])
     :rules (:rules production)}))

(defn rule-counts-as [counts-as]
  ;; TODO: Handle contexts in a better way!
  (let [cid (int (* 10000 (java.lang.Math/random)))
        production (apply merge-with concat
                          (map #(rule-condition (str cid) %)
                               (select-keys counts-as [:abstract-fact])))]
    {:inserts (conj (:inserts production) (map->CountsAs counts-as))
     :rules (:rules production)}))

(defn ^Package opera-to-drools [^String st]
  (let [data (regp/parse-file st)
        rules (apply merge-with concat
                     (concat (map rule-norm (:norms data))
                             #_(map rule-counts-as (:cas-rules data))))]
    rules))

(defrule holds
  "holds"
  [HasClause (= ?f formula) (= ?f2 clause)]
  [Holds (= ?f2 formula) (= ?theta substitution)]
  =>
  (insert! (->Holds ?f ?theta)))

(defrule event-processed
  "event processed"
  [Event (= ?a asserter) (= ?p content)]
  =>
  (insert! ?p))

(defn substitute [formula theta]
  formula)

(defn contains-all [theta theta2]
  (every? true? (map #(= (get theta (key %)) (val %)) theta2)))

(defrule counts-as-activation
  "counts-as activation"
  [CountsAs
   (= ?g1 abstract-fact)
   (= ?g2 concrete-fact)
   (= ?s context)]
  [Holds (= ?g1 formula) (= ?theta substitution)]
  [Holds (= ?s formula) (= ?theta2 substitution)]
  [:not [Holds (= ?g2 formula) (= ?theta substitution)]]
  =>
  (insert-unconditional! (substitute ?g2 ?theta)))

(defrule counts-as-deactivation
  "counts-as deactivation"
  [CountsAs
   (= ?g1 abstract-fact)
   (= ?g2 concrete-fact)
   (= ?s context)]
  [Holds (= ?g1 formula) (= ?theta substitution)]
  [:not [Holds (= ?s formula) (= ?theta2 substitution)]]
  [Holds (= ?g2 formula) (= ?theta substitution)]
  [?f <- Formula (= ?g2 content) (= ?theta grounding)]
  =>
  (retract! ?f))

(defrule norm-instantiation
  "norm instantiation"
  [Activation (= ?n norm) (= ?f formula)]
  [Holds (= ?f formula) (= ?theta substitution)]
  [:not [Instantiated (== ?n norm) (== ?theta substitution)]]
  [:not [Repair (= ?n2 norm) (= ?n repair-norm)]]
  =>
  (do
    (println "Instantiated " (.hashCode ?n) " " (.hashCode ?theta))
    (insert-unconditional! (->Instantiated ?n ?theta))))

(defrule norm-instance-fulfillment
  "norm instance fulfillment"
  [Expiration (= ?n norm) (= ?f formula)]
  [?ni <- Instantiated (= ?n norm) (= ?theta substitution)]
  [SubsetEQ (= ?theta2 subset) (= ?theta superset)]
  [Holds (= ?f formula) (= ?theta2 substitution)]
  =>
  (do
    (retract! ?ni)
    (insert-unconditional! (->Fulfilled ?n ?theta))))

(defrule norm-instance-violation-repaired
  "norm instance violation repaired"
  [?ni <- Violated (= ?n norm) (= ?theta substitution)]
  [Repair (= ?n norm) (= ?n2 repair-norm)]
  [Fulfilled (= ?n2 norm) (= ?theta substitution)]
  =>
  (retract! ?ni))

(defrule subseteq
  "subseteq"
  [Holds (= ?f formula) (= ?theta substitution)]
  [Holds (= ?f2 formula) (= ?theta2 substitution)]
  [:test (contains-all ?theta ?theta2)]
  =>
  (insert! (->SubsetEQ ?theta2 ?theta)))

(defquery test-instantiated
  []
  [?n <- Instantiated])

(defn start-engine []
  (let [specification (opera-to-drools
                        (.getPath
                          (clojure.java.io/resource "TestOpera.opera")))]
    (ppr/pprint (count (:rules specification)))
    (let [session (->
                    (mk-session)
                    (insert (map->Predicate {:name "NumberOfWorkers"
                                             :argument-0 "x"}))
                    (insert (map->Predicate {:name "lessThan"
                                             :argument-0 "x"
                                             :argument-1 "5"})))
          session-all (apply insert session (:inserts specification))]
      (query (fire-rules session-all) test-instantiated))))

(start-engine)

