(defproject wire "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :repositories [["bintray-sdm" "http://dl.bintray.com/kemlg/sdm"]
                 ["superhub-wp3" {:url "https://developers.superhub-project.eu/artifactory/simple/sh-wp3-snapshots-local/"
                                  :username "salvarez"
                                  :password "\\{DESede\\}wJlR1j6DvEz5NZsnU4OPcNqYR4BLGFGsBZhNoyUCRZ4="}]
                 ["superhub-wp4" {:url "https://developers.superhub-project.eu/artifactory/simple/sh-wp4-snapshots-local/"
                                  :username "salvarez"
                                  :password "\\{DESede\\}wJlR1j6DvEz5NZsnU4OPcNqYR4BLGFGsBZhNoyUCRZ4="}]
                 ["superhub-wp5" {:url "https://developers.superhub-project.eu/artifactory/simple/sh-wp5-snapshots-local/"
                                  :username "salvarez"
                                  :password "\\{DESede\\}wJlR1j6DvEz5NZsnU4OPcNqYR4BLGFGsBZhNoyUCRZ4="}]
                 ["bintray" "http://dl.bintray.com/tranchis/clojure-snippets"]
                 ["kermeta" "http://maven.irisa.fr/artifactory/list/kermeta-public-release/"]
                 ["acceleo-releases" "https://repo.eclipse.org/content/repositories/acceleo-releases/"]
                 ["alive-maven" "http://tranchis.github.io/alive-maven/"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [alive.jms/imqbroker "1.0.0"]
                 [alive.jms/imqjmx "1.0.0"]
                 [alive.jms/jms "1.0.0"]
                 [alive.jms/imq "1.0.0"]
                 [org.toomuchcode/clara-rules "0.4.0"]
                 [com.googlecode.json-simple/json-simple "1.1.1"]
                 [org.eclipse.gmf.runtime/notation "1.5.0-v20110309-2159"]
                 [alive.jms/imqutil "1.0.0"]
                 [net.sf.ictalive/services "1.0.3"]
                 [net.sf.ictalive/XSDSchema "1.0.0"]
                 [net.sf.ictalive/operetta "1.0.0"]
                 [net.sf.ictalive/runtime "1.0.0"]
                 [net.sf.ictalive/coordination "1.0.2"]
                 [net.sf.ictalive/owls "1.0.1"]
                 [net.sf.ictalive/rules "1.0.4"]
                 [net.sf.ictalive/normInstances "1.0.0"]
                 [org.clojure/math.combinatorics "0.0.3"]
                 [org.eclipse.core/org.eclipse.core.runtime "3.6.0.v20100505"]
                 [org.eclipse.core/org.eclipse.core.contenttype "3.4.200.v20130326-1255"]
                 [org.eclipse.birt.runtime/org.eclipse.core.jobs "3.5.300.v20130429-1813"]
                 [org.eclipse.equinox/org.eclipse.equinox.common "3.6.200.v20130402-1505"]
                 [org.eclipse.emf/org.eclipse.emf.ecore "2.9.1.v20130827-0309"]
                 [org.eclipse.emf/org.eclipse.emf.common "2.9.1.v20130827-0309"]
                 [org.drools/drools-core "5.5.0.Final"]
                 [org.drools/drools-compiler "5.5.0.Final"]
                 [net.sf.saxon/Saxon-HE "9.5.1-3"]
                 [eu.superhub.wp4.models/policymodel "0.0.2-SNAPSHOT"]
                 [org.apache.activemq/activemq-client "5.8.0"]
                 [org.slf4j/slf4j-log4j12 "1.7.3"]
                 [eu.superhub.wp4.encoder/encoder.server "0.0.1-SNAPSHOT"]
                 [eu.superhub.wp3/situationaldata-model "0.0.1-SNAPSHOT"]
                 [eu.superhub.wp3/situationaldata-provider "0.0.1-SNAPSHOT"]
                 [org.apache.camel/camel-spring "2.10.4"]
                 [org.apache.activemq/activemq-camel "5.8.0"]
                 [clj-http "0.7.8"]
                 [eu.superhubproject.wp3/trafficsituation-fromcellnet-datamodel "1.0.0-SNAPSHOT"]
                 [org.eclipse.emf/org.eclipse.emf.ecore "2.9.1.v20130827-0309"]
                 [org.eclipse.emf/org.eclipse.emf.ecore.xmi "2.9.1.v20130827-0309"]]
  :plugins [[lein-ring "0.8.10"]
            [no-man-is-an-island/lein-eclipse "2.0.0"]]
  :java-source-paths ["java"]
  :java-test-paths ["java-test"]
  :ring {:handler wire.handler/app}
  :aot :all
  :prep-tasks  [["compile" "eu.superhub.wp4.monitor.core.SDMClient"] "javac"]
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
