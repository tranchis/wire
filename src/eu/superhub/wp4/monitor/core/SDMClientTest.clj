(ns eu.superhub.wp4.monitor.core.SDMClientTest
  (:require [clojure.data.json :as json]))

;(import (eu.superhub.wp4.monitor.core SDMClient))

(defn clj-build-pollution []
  {
   "class" "eu.superhub.wp3.models.situationaldatamodel.statements.Normalized",
   "reliabilityScore" 0,
   "Content"
   [
    {
     "class" "eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.pollution.Pollution",
     "pollutionActiveArea"
     {
      "class" "eu.superhub.wp3.models.situationaldatamodel.location.Area",
      "ratio" 5000,
      "PointByCoordinates"
      {
       "class" "eu.superhub.wp3.models.situationaldatamodel.location.PointByCoordinates",
       "latitudeE6" 41600000,
       "longitudeE6" 2000000
       }
      },
     "validPeriod"
     {
      "class" "eu.superhub.wp3.models.situationaldatamodel.time.Interval",
      "end_time" 20000000,
      "init_time" 15000000
      },
     "Indexes"
     {
      "class" "eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.pollution.Indexes",
      "airQualityUndex" 0,
      "atmosphericParticulatesIndex" 0,
      "carbonDioxideIndex" 0.5,
      "nitrogenOxideIndex" 0,
      "sulfurDioxideIndex" 0,
      "volatileOrganicCompoundIndex" 0
      },
     "localReliability" 0
     }
    ]
   })

;(try
;  (let [sdm (SDMClient. "http://localhost:3000/sdm")
;        j (json/write-str (clj-build-pollution))]
;    (println j)
;    (.query sdm j))
;  (catch Exception e
;    (println (.getMessage e))))
