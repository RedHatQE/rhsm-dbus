(ns rhsm.dbus.parser-test
  (:require [rhsm.dbus.parser :as dbus]
            [rhsm.dbus.ebnf :as ebnf]
            [instaparse.core :as insta]
            [clojure.core.match :refer [match]]
            [clojure.test :refer :all]))

(deftest string-parser-test
  (->> "\"1111\""
       dbus/string-parser
       (= (list [:STRING "1111"] [:REST ""]))
       is))

(deftest string-parser-test
  (->> "\"1111\" 11432"
       dbus/string-parser
       (= (list [:STRING "1111"] [:REST " 11432"]))
       is))

(deftest string-parser-test
  (->> "\"\""
       dbus/string-parser
       (= (list [:EMPTY_STRING] [:REST ""]))
       is))

(deftest string-parser-test
  (->> "\"\" some rest"
       dbus/string-parser
       (= (list [:EMPTY_STRING] [:REST " some rest"]))
       is))

(deftest parse-empty-string-test
  (->> "\"\""
       dbus/parse-string
       (= ["" ""])
       is))

(deftest parse-empty-string-test
  (->> "\"\" some result"
       dbus/parse-string
       (= ["" "some result"])
       is))

;; ;; (deftest parse-string-with-escapes-test
;; ;;   (->> "\" a word \\\" hello \" fad "
;; ;;        dbus/parse-string
;; ;;        (= [" a word \" hello "    "fad"])
;; ;;        is))

(deftest parse-integer-test
  (->> "1123"
       dbus/parse-integer
       (= [1123 ""])
       is))

(deftest integer-parser-test
  (->> "1111"
       dbus/integer-parser
       (= (list [:INTEGER "1111"] [:REST ""]))
       is))

(deftest integer-parser-with-rest-test
  (->> "1111 some rest"
       dbus/integer-parser
       (= (list [:INTEGER "1111"] [:REST " some rest"]))
       is))

(deftest parse-integer-test
  (->> "11234  some result"
       dbus/parse-integer
       (= [11234 "some result"])
       is))

(deftest parse-data-test
  (->> "11123  some rest"
       (dbus/parse-data (list [:TYPE [:BASIC [:INTEGER]]]))
       (= [[11123]  "some rest"])
       is))

(deftest parse-data-more-integers-test
  (->> "11123  13432324  some rest"
       (dbus/parse-data (list [:TYPE [:BASIC [:INTEGER]]]
                              [:TYPE [:BASIC [:INTEGER]]]))
       (= [[11123 13432324]  "some rest"])
       is))

(deftest parse-data-more-strings-test
  (->> "\"string\"  \"string two\"  some rest"
       (dbus/parse-data (list [:TYPE [:BASIC [:STRING]]]
                              [:TYPE [:BASIC [:STRING]]]))
       (= [["string" "string two"]  "some rest"])
       is))

(deftest parse-01-test
  (->> "sss  \"string one\" \"string two\" \"string three\" some rest"
       dbus/parse
       (= [["string one" "string two" "string three"] "some rest"])
       is))

(deftest parse-02-test
  (->> "ssi \"string\"  \"string two\" 10  some rest"
       dbus/parse
       (= [["string" "string two" 10]  "some rest"])
       is))

(deftest parse-02-test
  (->> "ssisi \"string\"  \"string two\" 10 \"string three\" 20  some rest"
       dbus/parse
       (= [["string" "string two" 10 "string three" 20]  "some rest"])
       is))

(deftest parse-03-test
  (->> "ssisi \"string\"  \"string two\" 10 \"string three\" 20"
       dbus/parse
       (= [["string" "string two" 10 "string three" 20]  ""])
       is))
