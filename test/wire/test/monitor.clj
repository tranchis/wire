(ns wire.test.monitor
  (:use [clojure.tools.logging :only (info error)])
  (:require
        [eu.superhub.wp4.monitor.core.regulative-parser :as parser] 
        [eu.superhub.wp4.monitor.core.lisp-to-clara :as engine]
        [clojure.pprint]
        ))

#_(load-file "/Users/igomez/deapt/dea-repo/wire-github/wire/src/eu/superhub/wp4/monitor/core/regulative_parser.clj")
#_(use 'eu.superhub.wp4.monitor.core.regulative-parser)
#_(pprint (parse-file (.getPath (clojure.java.io/resource "TestOpera.opera"))))

(defn test-parse
  "Wrapper for testing monitor opera file parser. Equivalent to commands above"
  [file]
  (info "Parsing file '" file  "'")
  (clojure.pprint/pprint (parser/parse-file (.getPath (clojure.java.io/resource "TestOpera.opera"))))
  (info "Your test is over")
  )

#_(load-file "/Users/igomez/deapt/dea-repo/wire-github/wire/src/eu/superhub/wp4/monitor/core/lisp_to_clara.clj")
#_(use 'eu.superhub.wp4.monitor.core.lisp-to-clara)
#_(time (start-engine))

(defn test-engine
  "Wrapper for testing monitor opera file parser. Equivalent to commands above"
  [file]
  (info "Testing engine with file '" file  "'")
  (clojure.pprint/pprint (engine/start-engine file))
  (info "Your test is over")
  )

(defn -main [& args]
  (let [operation (first args)
        param-1 (second args)
        default "Supported operations are 'parse' and 'engine'"
        ]
        (case operation
        "parse" (test-parse param-1)
        "engine" (test-engine param-1)
        default)
    ))
