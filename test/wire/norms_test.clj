(ns wire.norms-test
  (:use midje.sweet) 
  (:require [wire.monitor :as mon]
            [wire.rules :refer :all]
            [wire.model :as model]
            [clara.rules.compiler :as c]
            [clara.rules :refer :all]))

; Set of functions to ease fact generation
(defn enact-role [agent] [:enacts-role agent :d])
(defn driving [agent] [:driving agent])
(defn deny-fact [fact] '(NOT fact))
(defn cross-red [agent] [:crossed-red agent])
(defn pay-fine [amount] [:fine-paid amount])

(defn init-moni [monitor agent-id]
	(-> monitor
			(mon/add-fact (enact-role agent-id))
			(mon/add-fact (driving agent-id))
			(mon/add-fact (deny-fact (cross-red agent-id)))))

(defn init-moni-reverse [monitor agent-id]
	(-> monitor
			(mon/add-fact (deny-fact (cross-red agent-id)))
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

(fact "A monitor must create a norm instance if the proper facts are added."
			(let [monitor (init-moni (mon/monitor model/example-norm) "any-agent")]
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-1))

(fact "It doesn't matter the order the facts arrive to the monitor"
			(let [monitor (init-moni-reverse (mon/monitor model/example-norm) "any-agent")]
				(empty? (mon/status monitor)) => false
				(extract-norm-id (mon/status monitor)) => :norm-1))

#_(fact "A monitor has to be able to detect when a norm has been violated."
      (let [monitor (init-moni (mon/monitor model/example-norm) "any-agent")]
				(extract-norm-id (prepare-violation monitor "any-agent")) => :norm-1))