(ns wire.rules-test
  (:require [wire.rules :refer :all]
            [midje.sweet :refer :all]))

(facts "complete substitution"
       (fact (complete-substitution? #{[:pred :a :b]} {:a 1 :b 2}) => true)
       (fact (complete-substitution? #{[:NOT [:pred :a :b]]} {:a 1 :b 2}) => true)
       (fact (complete-substitution? #{[:NOT [:pred :a :b]]} {:a 1}) => false)
       (fact (complete-substitution? #{[:NOT [:pred :a :b]] [:pred-2 :b :c]}
                                     {:a 1 :b 2}) => false)
       (fact (complete-substitution? #{[:NOT [:pred :a :b]] [:pred-2 :b :c]}
                                     {:a 1 :b 2 :c 3}) => true))

