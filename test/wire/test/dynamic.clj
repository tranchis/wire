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

(defn test-basic-water
  "Wrapper for testing monitor dynamism with sample wastewater norms"
  []
  (let [])
  (info "|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|")
  (info "Testing engine basic norm wastewater violaton")
  (info "Your test is over")
  (info (dynamic/test-water-violation))
  (info "|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|")
   (info "|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|")
  (info "Testing engine basic norm wastewater fulfillment")
  (info "Your test is over")
  (info (dynamic/test-water-fulfillment))
  (info "|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|")
  nil
  )

(defn test-propespective-water
  "Wrapper for testing monitor prospective promulgation with river basin scenario"
  []
  (let [])
  (info "Testing engine propespective promulgation on waste-water use case")
  (info "Your test is over")
  (dynamic/test-propespective-water)
  )

(defn test-retroactive-water
  "Wrapper for testing monitor retroactive promulgation with river basin scenario"
  []
  (let [])
  (info "Testing engine retroactive promulgation on waste-water use case")
  (info "Your test is over")
  (dynamic/test-retroactive-water)
  )

(defn test-annulment-water
  "Wrapper for testing monitor annulment with river basin scenario"
  []
  (let [])
  (info "Testing engine annulment on waste-water use case")
  (info "Your test is over")
  (dynamic/test-annulment-water)
  )

(defn test-abrogation-water
  "Wrapper for testing monitor abrogation with river basin scenario"
  []
  (let [])
  (info "Testing engine abrogation on waste-water use case")
  (info "Your test is over")
  (dynamic/test-abrogation-water)
  )

(defn -main [& args]
  (let [operation (first args)
        param-1 (second args)
        default "Supported operations: 
                · 'plain' 
                · 'propespective'
                · 'basic-water'
                · 'propespective-water'
                · 'retroactive-water'
                · 'annulment-water'
                · 'abrogation-water'
                "
        ]
        (case operation
        "plain" (test-plain)
        "propespective" (test-propespective-promulgation)
        "basic-water" (test-basic-water)
        "propespective-water" (test-propespective-water)
        "retroactive-water" (test-retroactive-water)
        "annulment-water" (test-annulment-water)
        "abrogation-water" (test-abrogation-water)
        default)
    ))
