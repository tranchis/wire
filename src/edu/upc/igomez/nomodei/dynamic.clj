(ns edu.upc.igomez.nomodei.dynamic
  (:use [clojure.tools.logging :only (info error)]
        [clojure.data]
        [clojure.pprint]
        [edu.upc.igomez.nomodei.constants.constants :as db])
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

(defn test-wait
  "Wait between event injection, so demo can be smooth"
  []
  (if ( = db/mode-demo "False" )
    (Thread/sleep db/test-sleep)
    (read-line)
    ))

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
          :arguments []}}]}]}
          }}],
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


(def generate-norm-set-water-1
  {:name "TestWater1",
 :norms
 [{:norm-id "heavy-rain-norm",
   :conditions
   {:activation 
    {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
         :name "Weather",
         :arguments
         [{:type "variable", :name "x85"}
          {:type "constant", :value "heavyRain"}]}]}]},
   :expiration 
   {:type "disjunction",
    :formulae
    [{:type "conjunction",
      :formulae
      [{:type "predicate",
         :name "Weather",
         :arguments
         [{:type "variable", :name "x85"}
          {:type "constant", :value "NOT heavyRain"}]}]}]},
   :maintenance
    {:type "disjunction",
     :formulae
     [{:type "conjunction",
       :formulae
       [{:type "negation",
         :formula
         {:type "predicate",
          :name "Discharge",
          :arguments
         [{:type "variable", :name "x85"}
          {:type "constant", :value "waterMass"}]}}]}]}
   }}],
 :cas-rules
 []}
  )

(def generate-norm-set-emtpy
  {:name "TestWater1",
 :norms
 [],
 :cas-rules
 []}
  )


(defn create-monitor
  [norms base-rules]
  (let [plain-norms norms
        norms (engine/operationalise norms)
        _ (dorun (map #(binding [*ns* base-rules] (println %) (eval %)) (:rules norms)))
        session (mk-session (ns-name base-rules) :cache false)
        result {:state session :norms norms :norm-def plain-norms :events []}]
    result))

(defn update-monitor
  [monitor predicate]
  (let [session (:state monitor)
        session (insert session (map->Predicate predicate))   
        session (fire-rules session)
        events (conj (:events monitor) predicate)
        result (assoc monitor :state session :events events)]
    result))

(defn apply-update
  [monitor]
  (let [session (:state monitor)
        norms (:norms monitor)
        session (apply insert session (:inserts norms))
        result (assoc monitor :state session)]
    result))

(defn inject-event
  [monitor event]
  (info "Injecting event:" event)
  (let
    [monitor (update-monitor monitor event)
     monitor (apply-update monitor)]
    monitor)
  )

(defn get-instances-in-state
  [monitor state br]
  (let [session (:state monitor)
        norms (:norms monitor)
        result (map #(engine/all-ti % session) 
                                (filter #(= (name (key %)) state)
                            (ns-map br) ))]
    result))

(def local-monitor (atom nil))

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

        ;Create auxiliary monitor and inject norm and events received so far
        aux-monitor (create-monitor merged-norms br)
        _ (reset! local-monitor aux-monitor)
        events (:events main-monitor)
        _ (doseq [event events              
               :let [seq-monitor @local-monitor
                     seq-monitor (inject-event seq-monitor event)
                     test-state (first (get-instances-in-state seq-monitor "Violated" br))
                     seq-monitor (if (> (count test-state) 0) @local-monitor seq-monitor)
                     _ (info "Pelusso:" event test-state (count test-state) seq-monitor)
                     ] 
               ]
          (reset! local-monitor seq-monitor) )       

        aux-monitor @local-monitor
        _ (reset! local-monitor nil)

        ;Inject instances of old monitor into new monitor
        old-state (:state main-monitor)
        new-state (:state aux-monitor)
        new-state (engine/merge-engines-active new-state old-state br)
        test-instantiated (get-instances-in-state aux-monitor "Everything" br)

        ;Done!
        result (assoc aux-monitor :state new-state) 
        ]
       result))

(defn retroactive-promulgation
  "retroactive promulgation operation"
  [main-monitor norm-set br]
  (let [;Inject norm into actual norm-set
        main-norms (:norm-def main-monitor)
        union-norms-reg (clojure.set/union (:norms main-norms) 
                                           (:norms norm-set))
        union-norms-cons (clojure.set/union (:cas-rules main-norms) 
                                            (:cas-rules norm-set))
        merged-norms (assoc main-norms :norms union-norms-reg 
                                       :cas-rules union-norms-cons)

        ;Create auxiliary monitor and inject norm and events received so far
        aux-monitor (create-monitor merged-norms br)
        _ (reset! local-monitor aux-monitor)
        events (:events main-monitor)
        _ (doseq [event events              
               :let [seq-monitor @local-monitor
                     seq-monitor (inject-event seq-monitor event)
                     ] 
               ]
         (reset! local-monitor seq-monitor) )        

        aux-monitor @local-monitor
        _ (reset! local-monitor nil)

        ;Inject instances of old monitor into new monitor
        old-state (:state main-monitor)
        new-state (:state aux-monitor)
        new-state (engine/merge-engines-all new-state old-state br)
        test-instantiated (get-instances-in-state aux-monitor "Everything" br)

        ;Done!
        result (assoc aux-monitor :state new-state) 
        ]
       result))

(defn annulment
  "annulment operation"
  [main-monitor norm-set br]
  (let [;Inject norm into actual norm-set
        main-norms (:norm-def main-monitor)
        s1 (into #{} (:norms main-norms))
        s2 (into #{} (:norms norm-set))
        norms-reg (clojure.set/difference s1 s2)
        s1 (into #{} (:cas-rules main-norms))
        s2 (into #{} (:cas-rules norm-set))
        norms-cons (clojure.set/difference s1 s2)
        merged-norms (assoc main-norms :norms norms-reg 
                                       :cas-rules norms-cons)

        ;Create auxiliary monitor and inject norm and events received so far
        aux-monitor (create-monitor merged-norms br)
        _ (reset! local-monitor aux-monitor)
        events (:events main-monitor)
        _ (doseq [event events              
               :let [seq-monitor @local-monitor
                     seq-monitor (inject-event seq-monitor event)
                     ] 
               ]
         (reset! local-monitor seq-monitor) )        

        aux-monitor @local-monitor
        _ (reset! local-monitor nil)

        ;Inject instances of old monitor into new monitor
        old-state (:state main-monitor)
        new-state (:state aux-monitor)
        new-state (engine/merge-engines-all new-state old-state br)
        test-instantiated (get-instances-in-state aux-monitor "Everything" br)

        ;Done!
        result (assoc aux-monitor :state new-state) 
        ]
       result))

(defn abrogation
  "abrogation operation"
  [main-monitor norm-set br]
  (let [;Inject norm into actual norm-set
        main-norms (:norm-def main-monitor)
        s1 (into #{} (:norms main-norms))
        s2 (into #{} (:norms norm-set))
        norms-reg (clojure.set/difference s1 s2)
        s1 (into #{} (:cas-rules main-norms))
        s2 (into #{} (:cas-rules norm-set))
        norms-cons (clojure.set/difference s1 s2)
        merged-norms (assoc main-norms :norms norms-reg 
                                       :cas-rules norms-cons)

        ;Create auxiliary monitor and inject norm and events received so far
        aux-monitor (create-monitor merged-norms br)
        _ (reset! local-monitor aux-monitor)
        events (:events main-monitor)
        _ (doseq [event events              
               :let [seq-monitor @local-monitor
                     seq-monitor (inject-event seq-monitor event)
                     ] 
               ]
         (reset! local-monitor seq-monitor) )        

        aux-monitor @local-monitor
        _ (reset! local-monitor nil)

        ;Inject instances of old monitor into new monitor
        old-state (:state main-monitor)
        new-state (:state aux-monitor)
        new-state (engine/merge-engines-all new-state old-state br)
        test-instantiated (get-instances-in-state aux-monitor "Everything" br)

        ;Done!
        result (assoc aux-monitor :state new-state) 
        ]
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

        monitor-1 (apply-update monitor-1)
        
        ;This is the result
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        
        result [test-instantiated  ]
        ]
    result))

(defn test-retroactive-promulgation
  "Test for the retroactive promulgation operation using wow norms"
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

        ;Inject a new event to verify norm is operative before merge
        monitor-1 (update-monitor monitor-1 {:name "Unit"
                                             :argument-0 "z"})
        monitor-1 (update-monitor monitor-1 {:name "Unit"
                                             :argument-0 "z"
                                             :argument-1 "Archmage"})
        monitor-1 (apply-update monitor-1)

        ;Retroactively promulgate a norm
        _ (info "retroactive promulgation of a new norm")
        monitor-1 (propespective-promulgation monitor-1 norm-set-2 br)

        
        
        ;This is the result
        test-instantiated (get-instances-in-state monitor-1 "Instantiated" br)
        #_ (pprint monitor-1)
        result test-instantiated ]
    result))

(defn test-water-violation
  "Test basic norm violation using wastewater norms"
  []
   (let [;Take base rules, and the set of norms
         br (engine/base-rules)
         norm-set-1 generate-norm-set-water-1
        
        ;Create Main monitor with initial norm set and inject event
        monitor-1 (create-monitor norm-set-1 br)
        monitor-1 (update-monitor monitor-1 {:name "Weather"
                                             :argument-0 "y"
                                             :argument-1 "heavyRain"})
        monitor-1 (apply-update monitor-1)

        ;Inject violation event
        monitor-1 (update-monitor monitor-1 {:name "Discharge"
                                             :argument-0 "y"
                                             :argument-1 "waterMass"})
        monitor-1 (apply-update monitor-1)

        ;This is the result
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        #_ (pprint monitor-1)
        result test-instantiated ]
    result))

(defn test-water-fulfillment
  "Test basic norm fulfillment using wastewater norms"
  []
   (let [;Take base rules, and the set of norms
         br (engine/base-rules)
         norm-set-1 generate-norm-set-water-1
        
        ;Create Main monitor with initial norm set and inject event
        monitor-1 (create-monitor norm-set-1 br)
        monitor-1 (update-monitor monitor-1 {:name "Weather"
                                             :argument-0 "y"
                                             :argument-1 "heavyRain"})
        monitor-1 (apply-update monitor-1)

       ;Inject fulfillment event
       monitor-1 (update-monitor monitor-1 {:name "Weather"
                                             :argument-0 "y"
                                             :argument-1 "NOT heavyRain"})
        monitor-1 (apply-update monitor-1)

        
        ;This is the result
        session-all (:state monitor-1)
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        #_ (pprint monitor-1)
        result test-instantiated ]
    result))

(defn test-propespective-water
  "Test propespective norm injection using wastewater norms"
  []
   (let [;Take base rules, initial norm set and norm to promulg.
         br (engine/base-rules)
         norm-set-1 generate-norm-set-emtpy
         norm-set-2 generate-norm-set-water-1
        
        _ (info "Create Main monitor with initial norm set and inject violation")
        monitor-1 (create-monitor norm-set-1 br)
        monitor-1 (inject-event monitor-1 {:name "Weather"
                                             :argument-0 "y"
                                             :argument-1 "heavyRain"})

        monitor-1 (inject-event monitor-1 {:name "Discharge"
                                             :argument-0 "y"
                                             :argument-1 "waterMass"})
        
        _ (info "Norm is not violated")
        session-all (:state monitor-1)
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        _ (info test-instantiated) 

        _ (test-wait)

        _ (info "Prospective promulgation of norm")
        monitor-1 (propespective-promulgation monitor-1 norm-set-2 br)

       _ (info "Event is not injected, norm is violated")
        session-all (:state monitor-1)
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        _ (info test-instantiated) 

        _ (test-wait)
        
        _ (info "Re-starting test...")    
         _ (info "Create Main monitor with initial norm set and inject violation")
        monitor-1 (create-monitor norm-set-1 br)
        monitor-1 (inject-event monitor-1 {:name "Weather"
                                             :argument-0 "y"
                                             :argument-1 "heavyRain"})

        monitor-1 (inject-event monitor-1 {:name "Discharge"
                                             :argument-0 "y"
                                             :argument-1 "waterMass"})
        
        _ (info "Norm is not violated")
        session-all (:state monitor-1)
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        _ (info test-instantiated) 

        _ (test-wait)

        _ (info "Prospective promulgation of norm")
        monitor-1 (propespective-promulgation monitor-1 norm-set-2 br)

       _ (info "Inject norm violation event, norm is violated")
       monitor-1 (inject-event monitor-1 {:name "Discharge"
                                             :argument-0 "y"
                                             :argument-1 "waterMass"})
        session-all (:state monitor-1)
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        _ (info test-instantiated) 

        _ (test-wait)


        result nil ]
    result))

(defn test-retroactive-water
  "Test retroactive norm injection using wastewater norms"
  []

(let [;Take base rules, initial norm set and norm to promulg.
         br (engine/base-rules)
         norm-set-1 generate-norm-set-emtpy
         norm-set-2 generate-norm-set-water-1
        
        _ (info "Create Main monitor with initial norm set and inject violation")
        monitor-1 (create-monitor norm-set-1 br)
        monitor-1 (inject-event monitor-1 {:name "Weather"
                                             :argument-0 "y"
                                             :argument-1 "heavyRain"})

        monitor-1 (inject-event monitor-1 {:name "Discharge"
                                             :argument-0 "y"
                                             :argument-1 "waterMass"})
        
        _ (info "Norm is not violated")
        session-all (:state monitor-1)
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        _ (info test-instantiated) 

        _ (test-wait)

        _ (info "Retroactive promulgation of norm")
        monitor-1 (retroactive-promulgation monitor-1 norm-set-2 br)

       _ (info "Event is not injected, however, norm is violated")
        session-all (:state monitor-1)
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        _ (info test-instantiated) 

        _ (test-wait)
      
        result nil ]
    result))

(defn test-annulment-water
  "Test norm annulment using wastewater norms"
  []

(let [;Take base rules, initial norm set and norm to promulg.
         br (engine/base-rules)
         norm-set-1 generate-norm-set-water-1
        
        _ (info "Create Main monitor with initial norm set and inject violation")
        monitor-1 (create-monitor norm-set-1 br)
        monitor-1 (inject-event monitor-1 {:name "Weather"
                                             :argument-0 "y"
                                             :argument-1 "heavyRain"})

        monitor-1 (inject-event monitor-1 {:name "Discharge"
                                             :argument-0 "y"
                                             :argument-1 "waterMass"})
        
        _ (info "Norm is violated")
        session-all (:state monitor-1)
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        _ (info test-instantiated) 

        _ (test-wait)

        _ (info "Annulment of norm")
        monitor-1 (annulment monitor-1 norm-set-1 br)

       _ (info "Event is injected, however, norm is not violated. Violated instances disappear")
        session-all (:state monitor-1)
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        _ (info test-instantiated) 

        _ (test-wait)
      
        result nil ]
    result))

(defn test-abrogation-water
  "Test norm abrogation using wastewater norms"
  []

(let [;Take base rules, initial norm set and norm to promulg.
         br (engine/base-rules)
         norm-set-1 generate-norm-set-water-1
        
        _ (info "Create Main monitor with initial norm set and inject violation")
        monitor-1 (create-monitor norm-set-1 br)
        monitor-1 (inject-event monitor-1 {:name "Weather"
                                             :argument-0 "y"
                                             :argument-1 "heavyRain"})
        
        _ (info "Norm is not violated")
        session-all (:state monitor-1)
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        _ (info test-instantiated) 

        _ (test-wait)

        _ (info "Abrogation of norm")
        monitor-1 (abrogation monitor-1 norm-set-1 br)

       _ (info "Event is injected, however, norm is not violated")
       monitor-1 (inject-event monitor-1 {:name "Discharge"
                                             :argument-0 "y"
                                             :argument-1 "waterMass"})
        session-all (:state monitor-1)
        test-instantiated (get-instances-in-state monitor-1 "Everything" br)
        _ (info test-instantiated) 

        _ (test-wait)
      
        result nil ]
    result))