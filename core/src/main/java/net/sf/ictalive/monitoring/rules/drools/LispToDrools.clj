(ns net.sf.ictalive.monitoring.rules.drools.LispToDrools
  (:require [clojure.contrib.pprint :as ppr])
  (:require [net.sf.ictalive.monitoring.rules.drools.RegulativeParser :as regp]))

(eval (regp/parse-file "/Users/sergio/Documents/Research/wire/core/src/main/java/Warcraft3ResourceGathering.opera"))