(defproject wire "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  #_#_:repositories [["bintray" "http://dl.bintray.com/tranchis/clojure-snippets"]
                 ["kermeta" "http://maven.irisa.fr/artifactory/list/kermeta-public-release/"]
                 ["acceleo-releases" "https://repo.eclipse.org/content/repositories/acceleo-releases/"]
                 ["alive-maven" "http://tranchis.github.io/alive-maven/"]]
  :dependencies [[org.clojure/clojure "1.9.0-alpha10"]
                 [compojure "1.5.2"]
                 [org.clojure/tools.logging "0.3.1"]
                 #_[alive.jms/imqbroker "1.0.0"]
                 [rolling-stones "1.0.0-SNAPSHOT"]
                 #_[alive.jms/imqjmx "1.0.0"]
                 #_[alive.jms/jms "1.0.0"]
                 #_[alive.jms/imq "1.0.0"]
                 [com.cerner/clara-rules "0.13.0"]
                 [org.clojure/data.json "0.2.6"]
                 [com.googlecode.json-simple/json-simple "1.1.1"]
                 #_[org.eclipse.gmf.runtime/notation "1.5.0-v20110309-2159"]
                 #_[alive.jms/imqutil "1.0.0"]
                 #_[net.sf.ictalive/services "1.0.3"]
                 #_[net.sf.ictalive/XSDSchema "1.0.0"]
                 #_[net.sf.ictalive/operetta "1.0.0"]
                 #_[net.sf.ictalive/runtime "1.0.0"]
                 #_[net.sf.ictalive/coordination "1.0.2"]
                 #_[net.sf.ictalive/owls "1.0.1"]
                 #_[net.sf.ictalive/rules "1.0.4"]
                 #_[net.sf.ictalive/normInstances "1.0.0"]
                 #_[org.clojure/math.combinatorics "0.0.3"]
                 #_[org.eclipse.core/org.eclipse.core.runtime "3.6.0.v20100505"]
                 #_[org.eclipse.core/org.eclipse.core.contenttype "3.4.200.v20130326-1255"]
                 #_[org.eclipse.birt.runtime/org.eclipse.core.jobs "3.5.300.v20130429-1813"]
                 #_[org.eclipse.equinox/org.eclipse.equinox.common "3.6.200.v20130402-1505"]
                 #_[org.eclipse.emf/org.eclipse.emf.ecore "2.9.1.v20130827-0309"]
                 #_[org.eclipse.emf/org.eclipse.emf.common "2.9.1.v20130827-0309"]
                 #_[org.drools/drools-core "5.5.0.Final"]
                 #_[org.drools/drools-compiler "5.5.0.Final"]
                 [org.clojure/test.check "0.9.0"]
                 #_[net.sf.saxon/Saxon-HE "9.5.1-3"]
                 #_[org.apache.activemq/activemq-client "5.8.0"]
                 [org.slf4j/slf4j-log4j12 "1.7.25"]
                 #_[org.apache.camel/camel-spring "2.10.4"]
                 #_[org.apache.activemq/activemq-camel "5.8.0"]
                 [clj-http "3.4.1"]
                 [midje "1.8.3"]
                 #_[org.eclipse.emf/org.eclipse.emf.ecore "2.9.1.v20130827-0309"]
                 #_[org.eclipse.emf/org.eclipse.emf.ecore.xmi "2.9.1.v20130827-0309"]
                 #_[com.github.tranchis/clojure-snippets "0.0.2"]]
  :plugins [[lein-ring "0.8.10"]
            [no-man-is-an-island/lein-eclipse "2.0.0"]]
  #_#_:java-source-paths ["java"]
  #_#_:java-test-paths ["java-test"]
  #_#_:ring {:handler wire.handler/app}
  #_#_:aot :all
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
