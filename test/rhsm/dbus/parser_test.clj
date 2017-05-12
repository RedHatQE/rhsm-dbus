(ns rhsm.dbus.parser-test
  (:require [rhsm.dbus.parser :as dbus]
            [rhsm.dbus.ebnf :as ebnf]
            [instaparse.core :as insta]
            [clojure.core.match :refer [match]]
            [clojure.test :refer :all]))


(deftest parse-empty-string-test
  (->> "\"\""
       dbus/parse-string
       (= ["" nil])
       is))

(deftest parse-empty-string-test
  (->> "\"\" some result"
       dbus/parse-string
       (= ["" "some result"])
       is))

(deftest parse-string-with-escapes-test
  (->> "\" a word \\\" hello \" fad "
       dbus/parse-string
       (= [" a word \" hello " "fad"])
       is))
