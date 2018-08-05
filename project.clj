(defproject wire "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  #_#_:repositories [["bintray" "http://dl.bintray.com/tranchis/clojure-snippets"]
                 ["kermeta" "http://maven.irisa.fr/artifactory/list/kermeta-public-release/"]
                 ["acceleo-releases" "https://repo.eclipse.org/content/repositories/acceleo-releases/"]
                 ["alive-maven" "http://tranchis.github.io/alive-maven/"]]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.6.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [rolling-stones "1.0.0-SNAPSHOT"]
                 [com.cerner/clara-rules "0.18.0"]
                 [org.clojure/data.json "0.2.6"]
                 [com.googlecode.json-simple/json-simple "1.1.1"]
                 [org.clojure/math.combinatorics "0.1.4"]
                 [org.clojure/test.check "0.9.0"]
                 [org.slf4j/slf4j-log4j12 "1.7.25"]
                 [clj-http "3.9.1"]
                 [midje "1.9.2"]]
  :plugins [[lein-ring "0.8.10" :exclusions [org.clojure/clojure]]
            [lein-midje "3.2"]]
  #_#_:java-source-paths ["java"]
  #_#_:java-test-paths ["java-test"]
  #_#_:ring {:handler wire.handler/app}
  #_#_:aot :all
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
