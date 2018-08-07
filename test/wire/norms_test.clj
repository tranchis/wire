(ns wire.norms-test
  (:use midje.sweet) 
  (:require [wire.monitor :as mon]
            [wire.preds :as preds]
            [wire.logic :as logic]
            [wire.rules :refer :all]
            [wire.model :as model]
            [clojure.math.combinatorics :as combo]
            [clara.rules.compiler :as c]
            [clara.tools.inspect :as insp]
            [clara.rules :refer :all]))

; Set of monitors to facilitate testing (put on wire.monitor to avoid error: 
; Can't take value of a macro: #'midje.sweet/formula)

(def mon-1 (mon/monitor model/example-norm))
(def mon-2 (mon/monitor model/example-norm-2))
(def mon-3 (mon/monitor model/example-norm-3))

; Set of facts
(defn enact-role [agent] [:enacts-role agent :d])
(defn driving [agent] [:driving agent])
(defn cross-red [agent bool]
  (if bool [:crossed-red agent] '(NOT [:crossed-red agent])))
(defn pay-fine [amount] [:fine-paid amount])

(defn init-moni [moni agent-id]
  (mon/add-fact (mon/add-fact (mon/add-fact moni (driving agent-id)) (enact-role agent-id)) (cross-red agent-id false)))

(defn prepare-violation [moni agent-id]
  (mon/add-fact (init-moni moni agent-id) (cross-red agent-id)))

(defn prepare-facts-reverse [moni agent-id]
  (mon/add-fact (mon/add-fact moni (enact-role agent-id)) (driving agent-id)))

; Just for the sake of simpliying code. 
; Extracts the norm-id of the norm instance that should be in the monitor (there should be only one)
(defn extract-norm-id [status]
  (:norm-id (:norm (first status))))

(fact "A monitor must create norm instances if the proper facts are added."
     (extract-norm-id (mon/status (init-moni mon-1 "my-agent"))) => :norm-1
     (extract-norm-id (mon/status (init-moni mon-2 "my-agent"))) => :norm-2
     (extract-norm-id (mon/status (init-moni mon-3 "my-agent"))) => :norm-3)


#_(fact "An instantiated norm can be violated and the monitor has to detect it."
      (extract-norm-id (prepare-violation mon-1)) => :norm-1
      (extract-norm-id (prepare-violation mon-2)) => :norm-2
      (extract-norm-id (prepare-violation mon-3)) => :norm-3)
