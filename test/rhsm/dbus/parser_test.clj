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


(deftest parse-data-integer-test
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

(deftest parse-data-var-object-test
  (->> "s \"string one\""
       (dbus/parse-data (list [:TYPE [:VAR]]))
       (= [["string one"] ""])
       is))

(deftest parse-var-object-test
  (->> "v s \"string one\""
       dbus/parse
       (= ["string one" ""])
       is))

(deftest parse-01-test
  (->> "s \"string one\""
       dbus/parse
       (= ["string one" ""])
       is))

(deftest parse-02-test
  (->> "sss  \"string one\" \"string two\" \"string three\" some rest"
       dbus/parse
       (= [["string one" "string two" "string three"] "some rest"])
       is))

(deftest parse-03-test
  (->> "ssi \"string\"  \"string two\" 10  some rest"
       dbus/parse
       (= [["string" "string two" 10]  "some rest"])
       is))

(deftest parse-04-test
  (->> "ssisi \"string\"  \"string two\" 10 \"string three\" 20  some rest"
       dbus/parse
       (= [["string" "string two" 10 "string three" 20]  "some rest"])
       is))

(deftest parse-05-test
  (->> "ssisi \"string\"  \"string two\" 10 \"string three\" 20"
       dbus/parse
       (= [["string" "string two" 10 "string three" 20]  ""])
       is))

(deftest parse-06-test
  (->> "ssisiv \"string\"  \"string two\" 10 \"string three\" 20 s \"string four\""
       dbus/parse
       (= [["string" "string two" 10 "string three" 20 "string four"]  ""])
       is))

(deftest parse-07-test
  (->> "ai 10 1 2 3 4 5 6 7 8 9 10"
       dbus/parse
       (= [[1 2 3 4 5 6 7 8 9 10]  ""])
       is))

(deftest parse-array-data-test
  (->> "\"string one\" \"string two\" \"string three\""
       (dbus/parse-array-data 3 [:ARRAY_ITEM [:BASIC [:STRING]]])
       (= [["string one" "string two" "string three"] ""])
       is))

(deftest parse-array-data-with-rest-test
  (->> "\"string one\" \"string two\" \"string three\"    some rest"
       (dbus/parse-array-data 3 [:ARRAY_ITEM [:BASIC [:STRING]]])
       (= [["string one" "string two" "string three"] "some rest"])
       is))

(deftest parse-array-integer-data-test
  (->> "10 20 30"
       (dbus/parse-array-data 3 [:ARRAY_ITEM [:BASIC [:INTEGER]]])
       (= [[10 20 30] ""])
       is))

(deftest parse-array-integer-data-with-rest-test
  (->> "10 20 30    some rest"
       (dbus/parse-array-data 3 [:ARRAY_ITEM [:BASIC [:INTEGER]]])
       (= [[10 20 30] "some rest"])
       is))

(deftest parse-array-test
  (->> "3 \"string one\" \"string two\" \"string three\""
       (dbus/parse-array [:ARRAY_ITEM [:BASIC [:STRING]]])
       (= [["string one" "string two" "string three"] ""])
       is))

(deftest parse-array-integers-test
  (->> "3 10 20 30"
       (dbus/parse-array [:ARRAY_ITEM [:BASIC [:INTEGER]]])
       (= [[10 20 30] ""])
       is))

(deftest parse-array-objects-test
  (->> "3 s \"string one\" i 10 i 20"
       (dbus/parse-array [:ARRAY_ITEM [:VAR]])
       (= [["string one" 10 20] ""])
       is))

(deftest parse-hashmap-test
  (->> "1 \"content\" 10"
       (dbus/parse-hashmap [:HASHMAP [:KEY [:BASIC [:STRING]]] [:VALUE [:BASIC [:INTEGER]]]])
       (= [{"content" 10} ""])
       is))

(deftest parse-hashmap-more-integer-items-test
  (->> "2 \"content\" 10 \"status\" 300"
       (dbus/parse-hashmap [:HASHMAP [:KEY [:BASIC [:STRING]]] [:VALUE [:BASIC [:INTEGER]]]])
       (= [{"content" 10 "status" 300} ""])
       is))

(deftest parse-hashmap-more-string-items-test
  (->> "2 \"content\" \"some content\" \"status\" \"some status\""
       (dbus/parse-hashmap [:HASHMAP [:KEY [:BASIC [:STRING]]] [:VALUE [:BASIC [:STRING]]]])
       (= [{"content" "some content" "status" "some status"} ""])
       is))

(deftest parse-hashmap-more-var-items-test
  (->> "2 \"content\" s \"some content\" \"status\" i 300"
       (dbus/parse-hashmap [:HASHMAP [:KEY [:BASIC [:STRING]]] [:VALUE [:VAR]]])
       (= [{"content" "some content" "status" 300} ""])
       is))

(deftest parse-hashmap-more-var-items-with-rest-test
  (->> "2 \"content\" s \"some content\" \"status\" i 300  some rest"
       (dbus/parse-hashmap [:HASHMAP [:KEY [:BASIC [:STRING]]] [:VALUE [:VAR]]])
       (= [{"content" "some content" "status" 300} "some rest"])
       is))

(deftest parse-hashmap-with-definition
  (->> "a{sv} 2 \"content\" s \"some content\" \"status\" i 300  some rest"
       dbus/parse
       (= [{"content" "some content" "status" 300} "some rest"])
       is))


(deftest parse-array-integers-hashmap-with-definition
  (->> "aia{sv} 2 10 20 2 \"content\" s \"some content\" \"status\" i 300  some rest"
       dbus/parse
       (= [[[10 20]  {"content" "some content" "status" 300}] "some rest"])
       is))

(deftest parse-integer-array-integers-hashmap-with-definition
  (->> "iaia{sv} 123 2 10 20 2 \"content\" s \"some content\" \"status\" i 300  some rest"
       dbus/parse
       (= [[123 [10 20]  {"content" "some content" "status" 300}] "some rest"])
       is))
