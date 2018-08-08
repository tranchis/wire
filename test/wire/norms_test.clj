(ns wire.norms-test
  (:use midje.sweet) 
  (:require [wire.monitor :as mon]
            [wire.rules :refer :all]
            [wire.model :as model]
            [clara.rules :refer :all]))

; Set of functions to ease fact generation
(defn enact-role [agent] [:enacts-role agent :d])
(defn driving [agent] [:driving agent])
(defn deny-fact [fact] '(NOT fact))
(defn conj-fact [fact-1 fact-2] '(AND fact-1 fact-2))
(defn union-fact [fact-1 fact-2] '(OR fact-1 fact-2))
(defn cross-red [agent] [:crossed-red agent])
(defn pay-fine [amount] [:fine-paid amount])

(defn init-moni [monitor agent-id]
	(-> monitor
			(mon/add-fact (enact-role agent-id))
			(mon/add-fact (driving agent-id))
			#_(mon/add-fact (deny-fact (cross-red agent-id)))))

(defn init-moni-reverse [monitor agent-id]
	(-> monitor
			#_(mon/add-fact (deny-fact (cross-red agent-id)))
			(mon/add-fact (driving agent-id))
			(mon/add-fact (enact-role agent-id))))

(defn prepare-violation [moni agent-id]
	(mon/add-fact (init-moni moni agent-id) (cross-red agent-id)))

; Extracts the norm-id of the first norm instance in the monitor
(defn extract-norm-id [status]
  (:norm-id (:norm (first status))))


(fact "A new monitor must not have any preconceived knowledge."
			(let [monitor (mon/monitor model/example-norm)]
				(empty? (mon/all-facts monitor)) => true
				(empty? (mon/status monitor)) => true))

(fact "When a new fact is added the monitor has to be aware of it."
			(let [fact (driving "any-agent")
						monitor (mon/add-fact (mon/monitor model/example-norm) fact)]
				(count (mon/all-facts monitor)) => 1
				(first (mon/all-facts monitor)) => fact))

(fact "When a fact is retracted the monitor has also to be aware of it."
			(let [fact (driving "any-agent")
						monitor (mon/add-fact (mon/monitor model/example-norm) fact)]
				(empty? (mon/all-facts (mon/remove-fact monitor fact))) => true))

(fact "A monitor must create a norm instance if the proper facts are added."
			(let [monitor (init-moni (mon/monitor model/example-norm) "any-agent")]
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-1))

(fact "Checking OR statements in norm activation."
			(let [agent-id "any-agent"
						fact '(OR (enact-role agent-id) (driving agent-id))
						monitor (mon/add-fact (mon/monitor model/example-norm-or) fact)]
				#_#_#_(empty? (mon/all-facts monitor)) => false
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-or))

(fact "Checking AND statements in norm activation."
			(let [agent-id "any-agent"
						fact '(AND (enact-role agent-id) (driving agent-id))
						monitor (mon/add-fact (mon/monitor model/example-norm-and) fact)]
				#_#_#_(empty? (mon/all-facts monitor)) => false
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-and))

(fact "Checking NOT statements in norm activation."
			(let [agent-id "any-agent"
						fact '(NOT (enact-role agent-id))
						monitor (mon/add-fact (mon/monitor model/example-norm-not) fact)]
				#_#_#_(empty? (mon/all-facts monitor)) => false
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-not))

(fact "Checking a complex statement in norm activation.")


(fact "It does not matter in which order did the facts arrived to the monitor."
			(let [monitor (init-moni-reverse (mon/monitor model/example-norm) "any-agent")]
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-1))

(fact "If a retracted fact disables an instantiated norm, the monitor has to react accordingly.")
#_(fact "A monitor has to be able to detect when a norm has been violated."
				(let [monitor (init-moni (mon/monitor model/example-norm) "any-agent")]
					(extract-norm-id ((prepare-violation monitor "any-agent"))) => :norm-1))

(fact "Everybody is innocent..."
			(let [agent-id "any-agent"
						monitor (mon/monitor model/example-norm)]
				(empty? (mon/has-violated? monitor agent-id)) => true))

(fact "...until proven guilty."
			(let [agent-id "any-agent"
						committing (cross-red agent-id)
						monitor (mon/add-fact (init-moni (mon/monitor model/example-norm) agent-id) committing)]
				(empty? (mon/has-violated? monitor agent-id)) => true))

(fact "But you can amend your errors."
			(let [agent-id "any-agent"
						committing (cross-red agent-id)
						monitor (mon/add-fact (init-moni (mon/monitor model/example-norm) agent-id) committing)
						repairing (pay-fine 200)]
				(empty? (mon/has-violated? (mon/add-fact monitor repairing) agent-id)) => false))

(fact "There are norm instances that no longer apply once its deadline occurs.")

#_(def example-norm
		(preds/->Norm
			:norm-1
			{:norm/target :agent-0
			 :norm/fa '(AND [:enacts-role :a :d] (OR [:test :d] [:driving :a]))
			 :norm/fm '(NOT [:crossed-red :a])
			 :norm/fd '(NOT [:driving :a])
			 :norm/fr '[:fine-paid 100]
			 :norm/timeout '[:time 500]}))