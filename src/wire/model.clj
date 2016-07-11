(ns wire.model
  (:require [clojure.spec :as s]
            [rolling-stones.core :as sat
             :refer [! NOT AND OR XOR IFF IMP NOR NAND]]))

(def variables (into #{} (map #(keyword (str (char %)))
                              (range (int \a) (inc (int \z))))))
(def types (s/or :string string? :number number?))

(s/def ::b-operators #{'XOR 'IFF 'IMP})
(s/def ::m-operators #{'AND 'OR 'NOR 'NAND})
(s/def ::u-operators #{'! 'NOT})
(s/def ::variable variables)
(s/def ::literal types)

(s/def ::state true?)

(s/def ::parameter (s/or :variable ::variable :literal ::literal))
(s/def :term/param ::parameter)
(s/def :term/pred keyword?)
(s/def ::term (s/or
               :propositional keyword?
               :fol (s/cat :predicate :term/pred :parameters (s/* ::parameter))))

(s/def ::wff
  (s/or :u-formula (s/cat :operator-u ::u-operators :term-u ::wff)
        :b-formula (s/cat :operator-b ::b-operators
                          :term-1 ::wff :term-2 ::wff)
        :m-formula (s/cat :operator-m ::m-operators :terms (s/* ::wff))
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
#_(ffirst (s/exercise ::norm 1))
#_(ffirst (s/exercise ::subs 1))
#_(ffirst (s/exercise ::norm-instance 1))
#_(ffirst (s/exercise ::inst 1))
#_(ffirst (s/exercise ::wff 1))
#_(ffirst (s/exercise operators 1))
(ffirst (s/exercise ::term 1))
(s/valid? ::term '(:pred :x 2 3 4))
(s/valid? ::wff '(XOR (AND :p :q (! :r)) (IFF :p (IMP :q :r))))
(ffirst (s/exercise ::wff 1))
