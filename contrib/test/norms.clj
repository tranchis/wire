(ns wire.test.norms
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

#_(def mon-1 (mon/monitor model/example-norm))
#_(def mon-2 (mon/monitor model/example-norm-2))
#_(def mon-3 (mon/monitor model/example-norm-3))

; Set of facts
(defn enact-role [driver] [:enacts-role driver :d])
(defn driving [driver] [:driving driver])
(defn cross-red [driver] [:crossed-red driver])
(defn pay-fine [amount] [:fine-paid amount])

(defn init-moni [moni]
  (mon/add-fact (mon/add-fact moni (driving :driver)) (enact-role :driver)))

(defn prepare-violation [moni]
  (mon/add-fact (init-moni moni) [:crossed-red :driver]))

(defn prepare-facts-reverse [moni]
  (mon/add-fact (mon/add-fact moni (enact-role :driver)) (driving :driver)))

; Just for the sake of simpliying code. 
; Extracts the norm-id of the norm instance that should be in the monitor (there should be only one)
(defn extract-norm-id [status]
  (:norm-id (:norm (first status))))

(fact "A monitor must create norm instances if the proper facts are added."
     (extract-norm-id (mon/status (init-moni mon/mon-1))) => nil #_:norm-1
     (extract-norm-id (mon/status (init-moni mon/mon-2))) => :norm-2 
     (extract-norm-id (mon/status (init-moni mon/mon-3))) => :norm-3)


(fact "An instantiated norm can be violated and the monitor has to detect it."
      (extract-norm-id (prepare-violation mon/mon-1)) => :norm-1
      (extract-norm-id (prepare-violation mon/mon-2)) => :norm-2
      (extract-norm-id (prepare-violation mon/mon-3)) => :norm-3)
