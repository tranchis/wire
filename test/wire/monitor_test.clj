(ns wire.monitor-test
  (:require [wire.monitor :refer :all]
            [midje.sweet :refer :all]
            [wire.logic :as logic]
            [wire.preds :as preds]
            [clara.rules.compiler :as c]
            [clojure.spec.test.alpha :as stest]))

(stest/instrument `monitor)

(fact "monitor"
      (monitor {:norm/target :agent-0
                :norm/fa '(AND [:enacts-role :a :d] (OR [:test :d] [:driving :a]))
                :norm/fm '(NOT [:crossed-red :a])
                :norm/fd '(NOT [:driving :a])
                :norm/fr '[:fine-paid 100]
                :norm/timeout '[:time 500]}) => {}
      #_(provided
       (logic/norm->inserts {}) => [[] []]
       (c/mk-session anything) => {}))
