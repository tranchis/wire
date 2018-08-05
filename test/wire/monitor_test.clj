(ns wire.monitor-test
  (:require [wire.monitor :refer :all]
            [midje.sweet :refer :all]
            [wire.logic :as logic]
            [wire.preds :as preds]
            [clara.rules.compiler :as c]
            [clara.rules.engine :as eng]
            [clojure.spec.test.alpha :as stest]
            [wire.model :as m])
  (:import (clara.rules.engine LocalSession)))

(stest/instrument `monitor)

(defn example-session []
  (eng/assemble {:rulebase []
                 :memory []
                 :transport []
                 :listeners []
                 :get-alphas-fn identity}))

(fact "monitor"
      (let [norm-example {:norm/target :agent-0
                          :norm/fa '[:driving :a]
                          :norm/fm '[:crossed-red :a]
                          :norm/fd '[:driving :a]
                          :norm/fr '[:fine-paid 100]
                          :norm/timeout '[:time 500]}]
        (let [session (monitor norm-example)]
          (eng/components session))
        => {:get-alphas-fn identity
            :listeners []
            :memory []
            :rulebase []
            :transport []}
        (provided
         (logic/norm->inserts norm-example) => [[] []]
         (c/mk-session anything) => (example-session))))

#_(fact "end-to-end monitor"
      (let [m (monitor {:norm/target :agent-0
                        :norm/fa '(AND [:enacts-role :a :d] (OR [:test :d] [:driving :a]))
                        :norm/fm '(NOT [:crossed-red :a])
                        :norm/fd '(NOT [:driving :a])
                        :norm/fr '[:fine-paid 100]
                        :norm/timeout '[:time 500]})]
        (status (add-fact (add-fact m [:enacts-role :a :d]) [:test :d])))
      => {})
