(ns wire.norms-test
  (:use midje.sweet) 
  (:require [wire.monitor :as mon]
            [wire.rules :refer :all]
            [wire.model :as model]
						[wire.preds :as preds]
						[clojure.tools.logging :as log]
            [clara.rules :refer :all]
						[clara.tools.tracing :refer :all]))

; Set of functions to ease fact generation
(defn enact-role [agent role] [:enacts-role agent role])
(defn driver [agent] (enact-role agent :driver))
(defn driving [agent] [:driving agent])
(defn deny-fact [fact] '(NOT fact))
(defn conj-fact [fact-1 fact-2] '(AND fact-1 fact-2))
(defn union-fact [fact-1 fact-2] '(OR fact-1 fact-2))
(defn cross-red [agent] [:crossed-red agent])
(defn pay-fine [amount] [:fine-paid amount])

(def agent-id "any-agent")

(defn init-moni [monitor agent-id]
	(-> monitor
			(mon/add-fact (driver agent-id))
			(mon/add-fact (driving agent-id))
			#_(mon/add-fact (deny-fact (cross-red agent-id)))))

(defn init-moni-reverse [monitor agent-id]
	(-> monitor
			#_(mon/add-fact (deny-fact (cross-red agent-id)))
			(mon/add-fact (driving agent-id))
			(mon/add-fact (driver agent-id))))

(defn prepare-violation [monitor agent-id]
	(-> (init-moni monitor agent-id)
			(mon/add-fact (cross-red agent-id))))

; Extracts the norm-id of the first norm instance in the monitor
(defn extract-norm-id [status] (:norm-id (:norm (first status))))

(def moni (mon/add-fact (mon/monitor model/example-norm) (driving agent-id)))

(fact "A new monitor must not have any preconceived knowledge."
			(let [monitor (mon/monitor model/example-norm)]
				(empty? (mon/all-facts monitor)) => true
				(empty? (mon/status monitor)) => true))

(fact "When a new fact is added the monitor has to be aware of it."
			(let [fact (driving "any-agent")
						full-fact (concat fact (take (- 14 (count fact)) (repeat nil)))
						monitor (mon/add-fact (mon/monitor model/example-norm) fact)]
				(count (mon/all-facts monitor)) => 1
				(first (mon/all-facts monitor)) => (apply preds/->Predicate full-fact)))

(fact "When a fact is retracted the monitor has also to be aware of it."
			(let [fact (driving "any-agent")
						monitor (mon/add-fact (mon/monitor model/example-norm) fact)]
				(empty? (mon/all-facts (mon/remove-fact monitor fact))) => true))

(fact "A monitor must create a norm instance if the proper facts are added."
			(let [monitor (init-moni (mon/monitor model/example-norm) agent-id)]
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-1))


; Caution: Be careful with norm design and avoid norms that are automatically
; fulfilled depending on how the activation and deadline forms are written.
(facts "Checking logical operators"
	(fact "Checking OR statements in norm activation."
			(let [fact (driver agent-id)
						monitor (mon/add-fact (mon/monitor model/example-norm-or) fact)]
				(empty? (mon/all-facts monitor)) => false
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-or))

	(fact "Checking AND statements in norm activation."
			(let [monitor (init-moni (mon/monitor model/example-norm-and) agent-id)]
				(empty? (mon/all-facts monitor)) => false
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-and))

	(fact "Checking NOT statements in norm activation."
			(let [fact '(NOT (driver agent-id))
						monitor (mon/add-fact (mon/monitor model/example-norm-not) fact)]
				(empty? (mon/all-facts monitor)) => false
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-not)))
#_(fact "Checking a complex statement in norm activation.")


(fact "It does not matter in which order did the facts arrived to the monitor."
			(let [monitor (init-moni-reverse (mon/monitor model/example-norm) agent-id)]
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-1))

#_(fact "If a retracted fact disables an instantiated norm, the monitor has to react accordingly.")
#_(fact "A monitor has to be able to detect when a norm has been violated."
				(let [monitor (init-moni (mon/monitor model/example-norm) "any-agent")]
					(extract-norm-id (mon/status (prepare-violation monitor "any-agent"))) => :norm-1))

#_(fact "Everybody is innocent..."
			(let [monitor (mon/monitor model/example-norm)]
				(empty? (mon/has-violated? monitor agent-id)) => true))

#_(fact "...until proven guilty."
			(let [committing (cross-red agent-id)
						monitor (mon/add-fact (init-moni (mon/monitor model/example-norm) agent-id) committing)]
				(empty? (mon/has-violated? monitor agent-id)) => false))

#_(fact "But you can amend your errors."
			(let [committing (cross-red agent-id)
						monitor (mon/add-fact (init-moni (mon/monitor model/example-norm) agent-id) committing)
						repairing (pay-fine 200)]
				(empty? (mon/has-violated? (mon/add-fact monitor repairing) agent-id)) => true))

#_(fact "There are norm instances that no longer apply once its deadline occurs."
			(let [stop-driving (deny-fact (driving agent-id))
						monitor (init-moni (mon/monitor model/example-norm) agent-id)]
				(empty? (mon/status monitor)) => false
				(empty? (mon/status (mon/add-fact monitor stop-driving))) => true))

#_(def example-norm
		(preds/->Norm
			:norm-1
			{:norm/target :agent-0
			 :norm/fa '(AND [:enacts-role :a :d] (OR [:test :d] [:driving :a]))
			 :norm/fm '(NOT [:crossed-red :a])
			 :norm/fd '(NOT [:driving :a])
			 :norm/fr '[:fine-paid 100]
			 :norm/timeout '[:time 500]}))