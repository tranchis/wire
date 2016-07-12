(ns wire.logic
  (:require [wire.model :as m]
            [rolling-stones.core :as sat]))

(defn sol [wff]
  (binding [*ns* (the-ns 'rolling-stones.core)]
    (sat/solutions-formula (eval (m/valid-wff? wff)))))

(sol (:norm/fr m/example-norm))
