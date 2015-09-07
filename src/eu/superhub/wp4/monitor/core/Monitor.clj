(ns eu.superhub.wp4.monitor.core.Monitor
  (:require [clojure.pprint :as ppr])
  (:require [eu.superhub.wp4.monitor.core.regulative-parser :as regp])
  (:require [eu.superhub.wp4.monitor.core.LispToDrools :as ltd]))

(import (eu.superhub.wp4.monitor.core                           IMonitor BusEventTransporter
                                                                RuleEngine EventTransporter
                                                                IMonitorListener)
        (eu.superhub.wp4.monitor.eventbus.exception             EventBusConnectionException)
        (eu.superhub.wp4.monitor.eventbus.instance              StartEventBus)
        (eu.superhub.wp4.monitor.core.rules.drools              DroolsEngine)
        (net.sf.ictalive.runtime.event                          Event)
        (net.sf.ictalive.runtime.fact                           Fact Content SendAct Message)
        (eu.superhub.wp4.monitor.core.domain                    Norm Activation Maintenance
                                                                Expiration Formula Proposition
                                                                Session))

(defn array-of [cname]
  "Utility function to allow array-based type hinting"
  (-> cname resolve .newInstance list into-array class .getName))

(def logger (org.slf4j.LoggerFactory/getLogger "eu.superhub.wp4.monitor.core.Monitor"))
(def monitor-map (ref {}))
(def running-instances (ref {}))

(defn collect-events [^IMonitor im]
  ;; TODO: Fix concurrency model
  (. logger info (str im " started collecting events"))
  (let [m (get @monitor-map im)]
    (loop [agent-map @m]
      (if (not (:active agent-map))
        (. logger info (str im " stopped collecting events"))
        (do
          (let [eb (:bus agent-map)]
            (. logger debug (str im " polling event bus..."))
            (. logger debug (str "size of queue: " (. eb getSize)))
            (let [ev (. eb take)
                  agent-map @m
                  engine (:engine agent-map)]
              (. logger info (str im " has received " ev))
              (. logger debug (str "event timestamp: " (. (. ev getTimestamp) getTime)))
              (. engine handleObservation ev)
              (send m assoc :count (inc (:count agent-map)))
              (recur agent-map))))))))

(defn create-monitor
  "Implementation of IMonitor interface"
  ([]
    (create-monitor "localhost" "61616"))
  ([^String ip ^String port]
  ;TODO: precondition that ip is a valid ip address
    (let [im
        (reify
          IMonitor
          ;void		initialise(Package[] listOfRules);
          ;void		updateRules(Package[] listOfRules) throws RuleUpdateException;
          ;EventTransporter	getEventTransporter();
          ;RuleEngine		getRuleEngine();
          (^void initialise [^IMonitor this #^#=(array-of eu.superhub.wp4.monitor.core.rules.drools.schema.Package) rules]
            {:pre (not (:active @(get @monitor-map this)))}
            (let [m (get @monitor-map this)
                  engine (DroolsEngine.)
                  bus (BusEventTransporter. (:ip @m) (str (:port @m)))]
              (dorun (map #(. engine addPackage %) (seq rules)))
              (send m assoc 
                            :engine engine
                            :endpoints {}
                            :subscriptions {}
                            :listeners {}
                            :watched {}
                            :active false
                            :bus bus)
              (await m))
            nil)
          (^void updateRules [^IMonitor this #^#=(array-of eu.superhub.wp4.monitor.core.rules.drools.schema.Package) rules]
            (let [engine (:engine @(get @monitor-map this))]
              (map #(. engine addPackage %) rules))
            nil)
          (^EventTransporter getEventTransporter [^IMonitor this]
            (:bus @(get @monitor-map this)))
          (^RuleEngine getRuleEngine [^IMonitor this]
            (:engine @(get @monitor-map this))))]
    (dosync
      (alter monitor-map merge-with
             {im
              (agent
                {
                 :count 0             ;; Number of messages processed
                 :ip ip               ;; EventBus IP address
                 :port port           ;; EventBus port
                 :bus nil             ;; EventBus instance
                 :engine nil          ;; Drools Engine
                 :endpoints nil       ;; Map<Session,Endpoint>
                 :subscriptions nil   ;; Map<Fact,Set<Session>>
                 :watched nil})}))    ;; Map<Session,Set<Fact>>
    im)))

;; Private methods ;; TODO: Fix idiomatic implementation
(defn subscribe [^IMonitor this ^Event ev ^IMonitorListener ml]
  (let [m (get @monitor-map this)
        agent-map @m]
    (send
      m
      (:listeners agent-map)
      (merge-with (:listeners agent-map) {ev ml}))))

(defn clean-sessions-for-fact [agent-map ^Session s ^Fact f]
  (let [sessions (get (:subscriptions agent-map) f)]
    (if (not (nil? sessions))
      (dissoc sessions s))))

(defn cancel-subscription [^IMonitor this ^Session session]
  (let [m (get @monitor-map this)
        agent-map @m
        endpoints (get (:endpoints agent-map) session)
        facts (get (:watched agent-map) session)]
    (map #(clean-sessions-for-fact agent-map session %) facts)
    (send m #(do (dissoc (:endpoints agent-map) session) (dissoc (:watched agent-map) session)))))

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

(defn restart-monitor [^IMonitor im]
  (let [m (get @monitor-map im) agent-map @m]
    (. logger info (str agent-map))
    (send m assoc :active true)
    (await m)
    (. logger debug (str @(get @monitor-map im)))
    (let [f (future (collect-events im))]
      (dosync (alter running-instances merge-with {im f})))))

(defn pause-monitor [^IMonitor im]
  (let [m (get @monitor-map im)]
    (send m assoc :active false)
    (let [instance (get @running-instances im)
          correct (future-cancel instance)]
      (. logger info (str instance " stopped correctly")))
    (dosync (alter running-instances dissoc im))))
    
(defn stop-monitor [^IMonitor im]
  (pause-monitor im))

(defn list-instances []
  (map val @monitor-map))

(defn get-opera-package [file-name]
  (binding [*ns* (the-ns 'eu.superhub.wp4.monitor.core.LispToDrools)]
    (ltd/opera-to-drools file-name)))

(defn get-count [^IMonitor im]
  (:count @(get @monitor-map im)))

(defn start-monitor [^IMonitor im]
  (let [file-name (. (clojure.java.io/resource "TestOpera.opera") getFile)]
    (. im initialise (-> (get-opera-package file-name) list into-array))
    (let [m         (get @monitor-map im)
          agent-map @m
          eb        (:bus agent-map)
          engine    (:engine agent-map)]
      (doto engine
        (. handleObservation eb)
        (. handleObservation (Proposition. "Context" (into-array String ["Universal"])))
        (. evaluate)
        (. dump))
      (restart-monitor im))))

(defn new-event [^String xml]
  (let [epkg (net.sf.ictalive.runtime.event.EventFactory/eINSTANCE)
        fpkg (net.sf.ictalive.runtime.fact.FactFactory/eINSTANCE)
        ev (. epkg createEvent)
        k (. epkg createKey)
        c (. fpkg createContent)
        sa (. fpkg createSendAct)
        m (. fpkg createMessage)]
    (. k setId (str (java.util.UUID/randomUUID)))
    (. ev setLocalKey k)
    (. ev setContent c)
    (. c setFact sa)
    (. sa setSendMessage m)
    (. m setBody xml)
    ev))

(defn send-event [^EventTransporter et ^Event ev]
  (. et push ev))

;(start-monitor (create-monitor "tranchis.mooo.com" 61616))
;(push-statement (Interpreted.))

;(run-monitor "localhost" 
;  (binding [*ns* (the-ns 'eu.superhub.wp4.monitor.core.LispToDrools)]
;    (ltd/lisp-to-drools "/tmp/norm.lisp")))

;(let [m (create-monitor "tranchis.mooo.com" "61616")
;  (run-monitor! m))
