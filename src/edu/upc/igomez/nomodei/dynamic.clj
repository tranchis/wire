(ns edu.upc.igomez.nomodei.dynamic
  (:use [clojure.tools.logging :only (info error)]
        [clojure.data]
        [clojure.pprint])
  (:require
        [eu.superhub.wp4.monitor.core.lisp-to-clara :as engine]  
        [clojure.pprint]
        [wire.preds :refer :all]
        [clara.rules :refer :all]
        [eu.superhub.wp4.monitor.core.regulative-parser :as regp]
        ))
#_(load-file "/Users/igomez/deapt/dea-repo/wire-github-ignasi/wire/src/edu/upc/igomez/nomodei/dynamic.clj")
#_(use 'edu.upc.igomez.nomodei.dynamic)
#_(plain)

(def generate-norm-set-wow-1
  {:name "TestOpera",
 :norms
 [{:norm-id "nCW0",
   :conditions
   {:expiration
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "negation",
         :formula
         {:type "predicate",
          :name "NumberOfWorkers",
          :arguments [{:type "variable", :name "x78"}]}}]}
      {:type "conjunction",
       :formulae
       [{:type "negation",
         :formula
         {:type "predicate",
          :name "lessThan",
          :arguments
          [{:type "variable", :name "x78"}
           {:type "constant", :value "5"}]}}]}]},
    :activation
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "predicate",
         :name "NumberOfWorkers",
         :arguments [{:type "variable", :name "x78"}]}
        {:type "predicate",
         :name "lessThan",
         :arguments
         [{:type "variable", :name "x78"}
          {:type "constant", :value "5"}]}]}]},
    :maintenance
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "negation",
         :formula
         {:type "predicate",
          :name "Produce_New_Soldier",
          :arguments []}}]}]}}}],
 :cas-rules
 [{:context "Universal",
   :concrete-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "Unit",
        :arguments
        [{:type "variable", :name "x85"}
         {:type "constant", :value "Peasant"}]}]}]},
   :abstract-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "Worker",
        :arguments [{:type "variable", :name "x85"}]}]}]}}
  {:context "Universal",
   :concrete-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "Count",
        :arguments
        [{:type "constant", :value "Worker"}
         {:type "variable", :name "x78"}]}]}]},
   :abstract-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "NumberOfWorkers",
        :arguments [{:type "variable", :name "x78"}]}]}]}}]}
  )

(def generate-norm-set-wow-2
  {:name "TestOpera",
 :norms
 [{:norm-id "n1",
   :conditions
   {:expiration
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "predicate",
         :name "NumberOfWorkers",
         :arguments [{:type "variable", :name "x78"}]}
        {:type "predicate",
         :name "lessThan",
         :arguments
         [{:type "variable", :name "x78"}
          {:type "constant", :value "5"}]}]}]},
    :activation
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "predicate",
         :name "Unit",
         :arguments
         [{:type "variable", :name "x85"}
          {:type "constant", :value "Archmage"}]}]}]},
    :maintenance
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "predicate",
         :name "Unit",
         :arguments
         [{:type "variable", :name "x85"}
          {:type "constant", :value "Archmage"}]}]}]}}}],
 :cas-rules
 [{:context "Universal",
   :concrete-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "Unit",
        :arguments
        [{:type "variable", :name "x85"}
         {:type "constant", :value "Peasant"}]}]}]},
   :abstract-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "Worker",
        :arguments [{:type "variable", :name "x85"}]}]}]}}
  {:context "Universal",
   :concrete-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "Count",
        :arguments
        [{:type "constant", :value "Worker"}
         {:type "variable", :name "x78"}]}]}]},
   :abstract-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "NumberOfWorkers",
        :arguments [{:type "variable", :name "x78"}]}]}]}}]}
  )

(def generate-norm-set-wow-full
  {:name "TestOpera",
 :norms
 [{:norm-id "nCW0",
   :conditions
   {:expiration
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "negation",
         :formula
         {:type "predicate",
          :name "NumberOfWorkers",
          :arguments [{:type "variable", :name "x78"}]}}]}
      {:type "conjunction",
       :formulae
       [{:type "negation",
         :formula
         {:type "predicate",
          :name "lessThan",
          :arguments
          [{:type "variable", :name "x78"}
           {:type "constant", :value "5"}]}}]}]},
    :activation
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "predicate",
         :name "NumberOfWorkers",
         :arguments [{:type "variable", :name "x78"}]}
        {:type "predicate",
         :name "lessThan",
         :arguments
         [{:type "variable", :name "x78"}
          {:type "constant", :value "5"}]}]}]},
    :maintenance
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "negation",
         :formula
         {:type "predicate",
          :name "Produce_New_Soldier",
          :arguments []}}]}]}}}
  {:norm-id "n1",
   :conditions
   {:expiration
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "predicate",
         :name "NumberOfWorkers",
         :arguments [{:type "variable", :name "x78"}]}
        {:type "predicate",
         :name "lessThan",
         :arguments
         [{:type "variable", :name "x78"}
          {:type "constant", :value "5"}]}]}]},
    :activation
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "predicate",
         :name "Unit",
         :arguments
         [{:type "variable", :name "x85"}
          {:type "constant", :value "Archmage"}]}]}]},
    :maintenance
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "predicate",
         :name "Unit",
         :arguments
         [{:type "variable", :name "x85"}
          {:type "constant", :value "Archmage"}]}]}]}}}],
 :cas-rules
 [{:context "Universal",
   :concrete-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "Unit",
        :arguments
        [{:type "variable", :name "x85"}
         {:type "constant", :value "Peasant"}]}]}]},
   :abstract-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "Worker",
        :arguments [{:type "variable", :name "x85"}]}]}]}}
  {:context "Universal",
   :concrete-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "Count",
        :arguments
        [{:type "constant", :value "Worker"}
         {:type "variable", :name "x78"}]}]}]},
   :abstract-fact
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
        :name "NumberOfWorkers",
        :arguments [{:type "variable", :name "x78"}]}]}]}}]})

(defn create-monitor
  [norms base-rules]
  (let [plain-norms norms
        norms (engine/operationalise norms)
        _ (dorun (map #(binding [*ns* base-rules] (println %) (eval %)) (:rules norms)))
        session (mk-session (ns-name base-rules) :cache false)
        result {:state session :norms norms :norm-def plain-norms}]
    result))

(defn update-monitor
  [monitor predicate]
  (let [session (:state monitor)
        session (insert session (map->Predicate predicate))   
        result (assoc monitor :state session)]
    result))

(defn apply-update
  [monitor]
  (let [session (:state monitor)
        norms (:norms monitor)
        session (apply insert session (:inserts norms))
        result (assoc monitor :state session)]
    result))

(defn get-instances-in-state
  [monitor state br]
  (let [session (:state monitor)
        norms (:norms monitor)
        result (map #(engine/all-ti % session) 
                                (filter #(= (name (key %)) state)
                            (ns-map br) ))]
    result))

(defn plain
  "Plain test using wow norms. For internal testing only"
  []
  (let [br (engine/base-rules)
        norm-set-1 generate-norm-set-wow-1
        monitor-1 (create-monitor norm-set-1 br)
        monitor-1 (update-monitor monitor-1 {:name "NumberOfWorkers" 
                                             :argument-0 "x"})
        monitor-1 (update-monitor monitor-1 {:name "lessThan" 
                                             :argument-0 "x" 
                                             :argument-1 "5"})
        monitor-1 (apply-update monitor-1)
        test-instantiated-1 (get-instances-in-state monitor-1 "test-instantiated" br)

        norm-set-2 generate-norm-set-wow-2
        monitor-2 (create-monitor norm-set-2 br)
        monitor-2 (update-monitor monitor-2 {:name "Unit"
                                             :argument-0 "y"})
        monitor-2 (update-monitor monitor-2 {:name "Unit"
                                             :argument-0 "y"
                                             :argument-1 "Archmage"})
        monitor-2 (apply-update monitor-2)
        monitor-2 (update-monitor monitor-2 {:name "Unit"
                                             :argument-0 "z"})
        monitor-2 (update-monitor monitor-2 {:name "Unit"
                                             :argument-0 "z"
                                             :argument-1 "Archmage"})
        monitor-2 (apply-update monitor-2)
        test-instantiated-2 (get-instances-in-state monitor-2 
                              "test-instantiated" br)
        result [test-instantiated-1 test-instantiated-2] ]
    result))

(defn propespective-promulgation
  "propespective promulgation operation"
  [main-monitor norm-set br]
  (let [;Inject norm into actual norm-set
        main-norms (:norm-def main-monitor)
        union-norms-reg (clojure.set/union (:norms main-norms) 
                                           (:norms norm-set))
        union-norms-cons (clojure.set/union (:cas-rules main-norms) 
                                            (:cas-rules norm-set))
        merged-norms (assoc main-norms :norms union-norms-reg 
                                       :cas-rules union-norms-cons)

        ;Create auxiliary monitor and inject norm and event for testing
        aux-monitor (create-monitor merged-norms br)
        aux-monitor (update-monitor aux-monitor {:name "Unit"
                                             :argument-0 "y"})
        aux-monitor (update-monitor aux-monitor {:name "Unit"
                                             :argument-0 "y"
                                             :argument-1 "Archmage"})
        aux-monitor (apply-update aux-monitor)

        ;Inject instances of old monitor into new monitor
        old-state (:state main-monitor)
        new-state (:state aux-monitor)
        new-state (engine/merge-engines-all new-state old-state br)

        ;Done!
        result (assoc aux-monitor :state new-state) 
        ]
       result))

(defn test-propespective-promulgation
  "Test for the propespective promulgation operation using wow norms"
  []
   (let [;Take base rules, initial norm set and norm to promulg.
         br (engine/base-rules)
         norm-set-1 generate-norm-set-wow-1
         norm-set-2 generate-norm-set-wow-2
        
        ;Create Main monitor with initial norm set and inject event
        monitor-1 (create-monitor norm-set-1 br)
        monitor-1 (update-monitor monitor-1 {:name "NumberOfWorkers" 
                                             :argument-0 "x"})
        monitor-1 (update-monitor monitor-1 {:name "lessThan" 
                                             :argument-0 "x" 
                                             :argument-1 "5"})
        monitor-1 (apply-update monitor-1)

        ;Prospectively promulgate a norm
        _ (info "propespective promulgation of a new norm")
        monitor-1 (propespective-promulgation monitor-1 norm-set-2 br)

        ;Inject a new event to verify norm is operative after merge
        monitor-1 (update-monitor monitor-1 {:name "Unit"
                                             :argument-0 "z"})
        monitor-1 (update-monitor monitor-1 {:name "Unit"
                                             :argument-0 "z"
                                             :argument-1 "Archmage"})
        monitor-1 (apply-update monitor-1)
        
        ;This is the result
        test-instantiated (get-instances-in-state monitor-1 "Instantiated" br)
        #_ (pprint monitor-1)
        result test-instantiated ]
    result))


