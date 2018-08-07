(ns wire.logic-test
  (:require [wire.logic :refer :all]
            [midje.sweet :refer :all]
            [rolling-stones.core :as sat]))

(facts "bang->not"
       (fact (bang->not :a => :a))
       (fact (bang->not 1 => 1))
       (fact (bang->not [:a :b :c]) => [:a :b :c])
       (fact (bang->not (sat/! :a)) => [:NOT :a])
       (fact (bang->not (sat/! [:a :b :c])) => [:NOT [:a :b :c]])
       (fact (bang->not [:a (sat/! :b) :c]) => [:a [:NOT :b] :c])
       (fact (bang->not [:b (sat/! :a) :d :c]) => [:b [:NOT :a] :d :c])
       (fact (bang->not (list [[:d :e :f] (sat/! [:a :b :c])]
                              [(sat/! [:d :e :f]) [:a :b :c]]
                              [[:d :e :f] [:a :b :c]]))
             => (just (list [[:d :e :f] [:NOT [:a :b :c]]]
                            [[:NOT [:d :e :f]] [:a :b :c]]
                            [[:d :e :f] [:a :b :c]]) :in-any-order))
       (fact (bang->not (list [:b (sat/! :a) :d :c] [:b :a :d :c] [:b :a (sat/! :d) :c]
                              [(sat/! :b) :a (sat/! :d) :c] [:b (sat/! :a) (sat/! :d) :c]
                              [(sat/! :b) :a :d :c] [:b :a :d (sat/! :c)]
                              [(sat/! :b) :a :d (sat/! :c)] [:b (sat/! :a) :d (sat/! :c)]))
             => '([:b [:NOT :a] :d [:NOT :c]] [[:NOT :b] :a :d [:NOT :c]]
                  [:b :a :d [:NOT :c]] [[:NOT :b] :a :d :c]
                  [:b [:NOT :a] [:NOT :d] :c] [[:NOT :b] :a [:NOT :d] :c]
                  [:b :a [:NOT :d] :c] [:b :a :d :c] [:b [:NOT :a] :d :c])))

(facts "sol"
       (fact "should return term with one term"
             (sol :a) => #{#{:a}})
       (fact "should return numerical term with one numerical term"
             (sol 1) => #{#{1}})
       (fact "should return one solution with one argument"
             (sol [:a :b :c]) => #{#{[:a :b :c]}}
             (provided
              (sat/solutions-symbolic-formula [:a :b :c])
              => '([:a :b :c])))
       (fact "should return two solutions with AND of two arguments"
             (sol '(AND [:a :b :c] [:d :e :f])) => #{#{[:a :b :c] [:d :e :f]}}
             (provided
              (sat/solutions-symbolic-formula (sat/AND [:a :b :c] [:d :e :f]))
              => '([[:d :e :f] [:a :b :c]])))
       (fact "should return two solutions with OR of two terms"
             (sol '(OR [:a :b :c] [:d :e :f]))
             => (just #{#{[:d :e :f] [:NOT [:a :b :c]]}
                        #{[:NOT [:d :e :f]] [:a :b :c]}
                        #{[:d :e :f] [:a :b :c]}}) 
             (provided
              (sat/solutions-symbolic-formula (sat/OR [:a :b :c] [:d :e :f]))
              => (list [[:d :e :f] (sat/! [:a :b :c])]
                       [(sat/! [:d :e :f]) [:a :b :c]] [[:d :e :f] [:a :b :c]])))
       (fact "should return from nested operators"
             (sol '(AND (OR :a :b) (OR :c :d))) => 
             (just #{#{[:NOT :b] :c :a [:NOT :d]} #{:c [:NOT :a] :b [:NOT :d]}
                     #{[:NOT :c] [:NOT :b] :d :a} #{[:NOT :c] :b :d :a}
                     #{[:NOT :b] :c :d :a} #{[:NOT :c] [:NOT :a] :b :d}
                     #{:c :b :d :a} #{:c :b :a [:NOT :d]} #{:c [:NOT :a] :b :d}}
                   :in-any-order)
             #_(provided
              (sat/solutions-symbolic-formula
               (sat/AND (sat/OR :a :b) (sat/OR :c :d))
               => ))))

(facts "clause->vars"
       (fact "should drop pred"
             (clause->vars [:NOT [:crossed-red :a]]) => (just [:a]))
       (fact "should drop value"
             (clause->vars [:pred :a :b]) => (just [:a :b]))
       (fact "should treat Not"
             (clause->vars (rolling-stones.core/->Not '(:a :b))) => (just [:b])))
