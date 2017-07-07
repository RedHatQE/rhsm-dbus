(ns rhsm.dbus.ebnf-test
  (:require [rhsm.dbus.parser :as dbus]
            [rhsm.dbus.ebnf :as ebnf]
            [instaparse.core :as insta]
            [clojure.core.match :refer [match]]
            [clojure.test :refer :all]))

(deftest one-type-and-rest-parser-test
  (->> "ss \"string\" \"string two\""
       dbus/one-type-and-rest-parser
       (= (list [:TYPE [:BASIC [:STRING]]]
                [:REST "s \"string\" \"string two\""]))
       is))

(deftest one-type-and-rest-parser-stop-test
  (->> "\"string\" \"string two\""
       dbus/one-type-and-rest-parser
       type
       (= instaparse.gll.Failure)
       is))

(deftest string-type-test
  (->> "s"
      dbus/parse-type-signature
      (= [(list [:TYPE [:BASIC [:STRING]]]) ""])
      is))

(deftest two-strings-type-test
  (->> "ss"
       dbus/parse-type-signature
       (= [(list [:TYPE [:BASIC [:STRING]]]
                 [:TYPE [:BASIC [:STRING]]]) ""])
       is))

(deftest two-strings-type-test
  (->> "ii"
       dbus/parse-type-signature
       (= [(list [:TYPE [:BASIC [:INTEGER]]]
                 [:TYPE [:BASIC [:INTEGER]]]) ""])
       is))

(deftest two-strings-type-test
  (->> "ii some rest"
       dbus/parse-type-signature
       (= [(list [:TYPE [:BASIC [:INTEGER]]]
                 [:TYPE [:BASIC [:INTEGER]]]) " some rest"])
       is))

(deftest integer-type-test
  (->> "i"
       dbus/parse-type-signature
       (= [(list [:TYPE [:BASIC [:INTEGER]]]) ""])
       is))

(deftest boolean-type-test
  (->> "b"
       dbus/parse-type-signature
       (= [(list [:TYPE [:BASIC [:BOOLEAN]]]) ""])
       is))

(deftest var-type-test
  (->> "v"
      dbus/parse-type-signature
      (= [(list [:TYPE [:VAR]]) ""])
      is))

(deftest array-hashmap-type-test
  (->> "a{sv}"
      dbus/parse-type-signature
      (= [(list [:TYPE
                 [:ARRAY
                  [:ARRAY_ITEM
                   [:HASHMAP
                    [:KEY [:BASIC [:STRING]]]
                    [:VALUE [:VAR]]]]]]) ""])
      is))

(deftest array-of-integers-type-test
  (->> "ai"
      dbus/parse-type-signature
      (= [(list [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:INTEGER]]]]]) ""])
      is))

(deftest array-of-strings-type-test
  (->> "as"
      dbus/parse-type-signature
      (= [(list [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:STRING]]]]]) ""])
      is))

(deftest array-of-hashmaps-test
  (->> "aa{sv}"
       dbus/parse-type-signature
       (= [(list [:TYPE
                  [:ARRAY
                   [:ARRAY_ITEM
                    [:ARRAY
                     [:ARRAY_ITEM
                      [:HASHMAP
                       [:KEY [:BASIC [:STRING]]]
                       [:VALUE [:VAR]]]]]]]]) ""])
       is))

(deftest more-types-test
  (->> "ssiasasaiiiba{si}a{sv}"
      dbus/parse-type-signature
      (= [(list [:TYPE [:BASIC [:STRING]]]
                [:TYPE [:BASIC [:STRING]]]
                [:TYPE [:BASIC [:INTEGER]]]
                [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:STRING]]]]]
                [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:STRING]]]]]
                [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:INTEGER]]]]]
                [:TYPE [:BASIC [:INTEGER]]]
                [:TYPE [:BASIC [:INTEGER]]]
                [:TYPE [:BASIC [:BOOLEAN]]]
                [:TYPE [:ARRAY
                        [:ARRAY_ITEM
                         [:HASHMAP [:KEY [:BASIC [:STRING]]]
                          [:VALUE [:BASIC [:INTEGER]]]]]]]
                [:TYPE [:ARRAY
                        [:ARRAY_ITEM
                         [:HASHMAP [:KEY [:BASIC [:STRING]]]
                          [:VALUE [:VAR]]]]]]) ""])
      is))
