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
      (= [:TYPE_SIGNATURE [:TYPE [:BASIC [:STRING]]] [:TYPE_SIGNATURE]])
      is)
  (->> "ss"
      dbus/parse-type-signature
      (= [:TYPE_SIGNATURE [:TYPE [:BASIC [:STRING]]]
          [:TYPE_SIGNATURE [:TYPE [:BASIC [:STRING]]]
           [:TYPE_SIGNATURE]]])
      is))

(deftest integer-type-test
  (->> "i"
      dbus/parse-type-signature
      (= [:TYPE_SIGNATURE [:TYPE [:BASIC [:INTEGER]]] [:TYPE_SIGNATURE]])
      is))

(deftest var-type-test
  (->> "v"
      dbus/parse-type-signature
      (= [:TYPE_SIGNATURE [:TYPE [:VAR]] [:TYPE_SIGNATURE]])
      is))

(deftest array-hashmap-type-test
  (->> "a{sv}"
      dbus/parse-type-signature
      (= [:TYPE_SIGNATURE
          [:TYPE [:ARRAY [:ARRAY_ITEM [:HASHMAP [:KEY [:BASIC [:STRING]]]
                                       [:VALUE [:VAR]]]]]]
          [:TYPE_SIGNATURE]])
      is))

(deftest array-of-integers-type-test
  (->> "ai"
      dbus/parse-type-signature
      (= [:TYPE_SIGNATURE
          [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:INTEGER]]]]]
          [:TYPE_SIGNATURE]])
      is))

(deftest array-of-strings-type-test
  (->> "as"
      dbus/parse-type-signature
      (= [:TYPE_SIGNATURE
          [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:STRING]]]]]
          [:TYPE_SIGNATURE]])
      is))

(deftest more-types-test
  (->> "ssiasasaiiiia{si}a{sv}"
      dbus/parse-type-signature
      (= [:TYPE_SIGNATURE [:TYPE [:BASIC [:STRING]]]
          [:TYPE_SIGNATURE [:TYPE [:BASIC [:STRING]]]
           [:TYPE_SIGNATURE [:TYPE [:BASIC [:INTEGER]]]
            [:TYPE_SIGNATURE [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:STRING]]]]]
             [:TYPE_SIGNATURE [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:STRING]]]]]
              [:TYPE_SIGNATURE [:TYPE [:ARRAY [:ARRAY_ITEM [:BASIC [:INTEGER]]]]]
               [:TYPE_SIGNATURE [:TYPE [:BASIC [:INTEGER]]]
                [:TYPE_SIGNATURE [:TYPE [:BASIC [:INTEGER]]]
                 [:TYPE_SIGNATURE [:TYPE [:BASIC [:INTEGER]]]
                  [:TYPE_SIGNATURE [:TYPE [:ARRAY
                                           [:ARRAY_ITEM
                                            [:HASHMAP [:KEY [:BASIC [:STRING]]]
                                             [:VALUE [:BASIC [:INTEGER]]]]]]]
                   [:TYPE_SIGNATURE [:TYPE [:ARRAY
                                            [:ARRAY_ITEM
                                             [:HASHMAP [:KEY [:BASIC [:STRING]]]
                                              [:VALUE [:VAR]]]]]]
                    [:TYPE_SIGNATURE]]]]]]]]]]]])
      is))
