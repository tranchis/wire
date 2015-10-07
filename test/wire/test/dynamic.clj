(ns wire.test.dynamic
  (:use [clojure.tools.logging :only (info error)])
  (:require
        [edu.upc.igomez.nomodei.dynamic :as dynamic] 
        [clojure.pprint]
        ))

(defn test-plain
  "Wrapper for testing monitor dynamism with sample wow file"
  []
  (let [])
  (info "Testing engine with sample wow file")
  (info "Your test is over")
  (dynamic/plain)
  )

(defn test-propespective-promulgation
  "Wrapper for testing monitor dynamism with sample wow file"
  []
  (let [])
  (info "Testing engine propespective promulgation")
  (info "Your test is over")
  (dynamic/test-propespective-promulgation)
  )

(defn -main [& args]
  (let [operation (first args)
        param-1 (second args)
        default "Supported operations are 'plain' 
                 and 'propespective"
        ]
        (case operation
        "plain" (test-plain)
        "propespective" (test-propespective-promulgation)
        default)
    ))
