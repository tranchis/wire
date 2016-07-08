(ns wire.model
  (:require [clojure.spec :as s]))

(def variables (into #{} (map #(keyword (str (char %)))
                              (range (int \a) (inc (int \z))))))
(def types (s/or :string string? :number number?))

(s/def ::variable variables)
(s/def ::literal types)

(s/def ::state true?)

(s/def ::parameter (s/or :variable ::variable :literal ::literal))
(s/def :term/param ::parameter)
(s/def :term/pred keyword?)
(s/def ::term (s/keys :req [:term/pred :term/param]))

(s/def ::wff
  (s/or :formula (s/cat :operator #{'and 'or}
                        :terms (s/coll-of ::wff :min-count 1))
        :term ::term))

(s/def :norm/target keyword?)
(s/def :norm/fa ::wff)
(s/def :norm/fm ::wff)
(s/def :norm/fd ::wff)
(s/def :norm/fr ::wff)
(s/def :norm/timeout ::wff)

(s/def ::subs (s/map-of ::variable ::literal))

(s/def ::norm (s/keys :req [:norm/target :norm/fa :norm/fm
                            :norm/fd :norm/fr :norm/timeout]))

(s/def ::norm-instance (s/keys :req [::norm ::subs]))

(s/def :inst/ns (s/coll-of ::norm))

(s/def ::instance-set (s/coll-of ::norm-instance))

(s/def :inst/ns ::instance-set)
(s/def :inst/as ::instance-set)
(s/def :inst/vs ::instance-set)
(s/def :inst/fs ::instance-set)
(s/def :inst/ds ::instance-set)

(s/def ::inst (s/keys :req [:inst/ns :inst/as :inst/vs
                            :inst/ds :inst/fs ::state]))

(s/valid? ::wff :a)
(s/valid? ::wff [:and [:a]])
(s/valid? ::wff [:or [:a]])
(s/valid? ::wff [:and [:a :b]])
(s/valid? ::wff [:and [:a [:or [:b :c]]]])
(ffirst (s/exercise ::norm 1))
(ffirst (s/exercise ::subs 1))
(ffirst (s/exercise ::norm-instance 1))
(ffirst (s/exercise ::inst 1))
