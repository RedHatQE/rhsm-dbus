(ns rhsm.dbus.parser-test
  (:require [rhsm.dbus.parser :as dbus]
            [rhsm.dbus.ebnf :as ebnf]
            [instaparse.core :as insta]
            [clojure.data.zip.xml :as xml]
            [clojure.core.match :refer [match]]
            [clojure.test :refer :all]))

(deftest parse-empty-data-test
  (->> ""
       (dbus/parse-data (list))
       (= [])
       is))

(deftest parse-string-data-test
  (->>  "\"fads\""
        (dbus/parse-data (list [:TYPE [:BASIC [:STRING]]]))
        (= ["fasd"])
        is))


;; ;; (deftest string-test
;; ;;   (->> "\" hello \""
;; ;;        dbus/parse-simple-data
;; ;;        (= [:a])
;; ;;        is)
;; ;;   (->> "\" hello \" rest data"
;; ;;        dbus/parse-simple-data
;; ;;        (= [:a])
;; ;;        is))

;; (deftest string-data-test
;;   (->> "\" hello \""
;;        (dbus/parse-data [:TYPE_SIGNATURE [:TYPE [:BASIC [:STRING]]]
;;                          [:TYPE_SIGNATURE]])
;;        (= "")
;;        is))
