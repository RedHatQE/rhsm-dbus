(ns rhsm.dbus.ebnf-test
  (:require [rhsm.dbus.parser :as dbus]
            [rhsm.dbus.ebnf :as ebnf]
            [instaparse.core :as insta]
            [clojure.data.zip.xml :as xml]
            [clojure.core.match :refer [match]]
            [clojure.test :refer :all]))

(deftest string-type-test
  (->> "s"
      dbus/parse-type-signature
      (= (list [:TYPE [:BASIC [:STRING]]]))
      is))

(deftest two-strings-type-test
  (->> "ss"
       dbus/parse-type-signature
       (= (list [:TYPE [:BASIC [:STRING]]]
                [:TYPE [:BASIC [:STRING]]]))
       is))

(deftest integer-type-test
  (->> "i"
      dbus/parse-type-signature
      (= (list [:TYPE [:BASIC [:INTEGER]]]))
      is))

(deftest var-type-test
  (->> "v"
      dbus/parse-type-signature
      (= (list [:TYPE [:VAR]]))
      is))

(deftest array-hashmap-type-test
  (->> "a{sv}"
      dbus/parse-type-signature
      (= (list [:TYPE
                [:ARRAY
                 [:ARRAY_ITEM
                  [:HASHMAP
                   [:KEY [:BASIC [:STRING]]]
                   [:VALUE [:VAR]]]]]]))
      is))

(deftest array-of-integers-type-test
  (->> "ai"
      dbus/parse-type-signature
      (= (list [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:INTEGER]]]]]))
      is))

(deftest array-of-strings-type-test
  (->> "as"
      dbus/parse-type-signature
      (= (list [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:STRING]]]]]))
      is))

(deftest more-types-test
  (->> "ssiasasaiiiia{si}a{sv}"
      dbus/parse-type-signature
      (= (list [:TYPE [:BASIC [:STRING]]]
               [:TYPE [:BASIC [:STRING]]]
               [:TYPE [:BASIC [:INTEGER]]]
               [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:STRING]]]]]
               [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:STRING]]]]]
               [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:INTEGER]]]]]
               [:TYPE [:BASIC [:INTEGER]]]
               [:TYPE [:BASIC [:INTEGER]]]
               [:TYPE [:BASIC [:INTEGER]]]
               [:TYPE [:ARRAY
                       [:ARRAY_ITEM
                        [:HASHMAP [:KEY [:BASIC [:STRING]]]
                         [:VALUE [:BASIC [:INTEGER]]]]]]]
               [:TYPE [:ARRAY
                       [:ARRAY_ITEM
                        [:HASHMAP [:KEY [:BASIC [:STRING]]]
                         [:VALUE [:VAR]]]]]]))
      is))
