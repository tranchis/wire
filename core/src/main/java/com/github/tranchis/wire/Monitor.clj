(ns com.github.tranchis.wire.Monitor
  (:require [clojure.contrib.pprint :as ppr])
  (:require [com.github.tranchis.wire.RegulativeParser :as regp])
  (:require [com.github.tranchis.wire.LispToDrools :as ltd]))

(import (net.sf.ictalive.monitoring BusEventTransporter)
        (net.sf.ictalive.eventbus.exception EventBusConnectionException)
        (net.sf.ictalive.eventbus.dashboard StartEventBus)
        (net.sf.ictalive.monitoring.domain Norm Activation Maintenance Expiration Formula Proposition))

(defn create-condition [n c]
  (def method (. (class n) getDeclaredMethod (str "getNorm" (. c getSimpleName)) (into-array Class [])))
  (def ctor (. c getDeclaredConstructor (into-array Class [(class n) Formula])))
;  (def method (sq/find-first #(= (. % getName) (str "getNorm" (. c getSimpleName))) (. (class n) getDeclaredMethods)))
  (. ctor newInstance (into-array Object [n (Formula. (. method invoke n (into-array Object [])))])))

(defn build-norms [n]
;		inserts = new Vector<Object>();
;		it = norms.iterator();
;		while (it.hasNext()) {
;			n = it.next();
;			inserts.add(n);
;			inserts.add(new Activation(n, new Formula(n.getNormActivation())));
;			inserts.add(new Maintenance(n, new Formula(n.getNormCondition())));
;			inserts.add(new Expiration(n, new Formula(n.getNormExpiration()))); }
  (def conditions (drop 1 (nth n 2)))
  (def norm (Norm. (second n) (first conditions) (second conditions) (nth conditions 2)))
  (cons norm (map #(create-condition norm %) (list Activation Maintenance Expiration))))

(defn get-norms [i]
   (flatten (map build-norms (filter #(= 'norm (first %)) (drop 2 i)))))

(defn run-monitor [ip den]
  (def de (first den))
  (def n (second den))
  (def eb (BusEventTransporter. ip))
  (. de handleObservation eb)
  (. de handleObservation (get-norms n))
  (. de handleObservation (Proposition. "Context" (into-array String ["Universal"])))
  (. de evaluate)
  (. de dump)
  (while true
    (do
      (. de handleObservation (. eb take))
      (. de evaluate))))

(run-monitor "localhost" 
  (binding [*ns* (the-ns 'com.github.tranchis.wire.LispToDrools)]
    (ltd/lisp-to-drools "C:/tmp.lisp")))
