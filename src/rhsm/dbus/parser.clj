(ns ^{:author "Jan Stavel @ Red Hat"
      :doc "RHSM Parser for DBus Type System"}
    rhsm.dbus.parser
  (:require  [instaparse.core :refer [defparser]]
             [clojure.core.match :refer [match]]
             [rhsm.dbus.ebnf :as ebnf]))

(defparser parse-type-signature
  (str "<TYPE_SIGNATURE> = TYPE TYPE_SIGNATURE | EPSILON"
       ebnf/type-signature-ebnf))

(defparser string-parser
  (str "<DATA> = (STRING | EMPTY_STRING) <DELIM> REST" ebnf/simple-data-ebnf))

(defparser escaped-string-parser
  (str "<DATA> = STRING <DELIM> REST" ebnf/simple-data-ebnf))

(defn parse-string [input]
  (let [[result rest] (string-parser input)]
    (println "result:" result "rest:" rest)
    (let [[rest-label rest-value] rest]
      (println "rest-label:" rest-label "rest-value:" rest-value)
      (match result
             [:EMPTY_STRING] ["" rest-value]
             [:STRING _] [(second result) rest-value]
             :else [nil nil]))))

;; (list [:TYPE [:BASIC [:INTEGER]]]  [:TYPE [:BASIC [:INTEGER]]])
(defn parse-data [types string]
  (loop [[type & rest] types
         accumulator []
         string string]
    (if-not type ;; the case: (list)
      accumulator
      (let [[parsed-value next-string] (match type
                                              [:TYPE [:BASIC [:STRING]]] (parse-string string)
                                              :else (list))]
        (recur rest (conj accumulator parsed-value) next-string)))))

;https://start.fedoraproject.org/; (def object-type-parser
;;   (insta/parser object-type-ebnf))

;; (def string-parser
;;   (insta/parser (str "S = STRING REST;" data-ebnf)))

;; (def string-with-escapes-parser
;;   (insta/parser (str "S = STRING_WITH_ESCAPES REST;" data-ebnf)))

;; (def integer-parser (insta/parser (str "S = INTEGER REST;" data-ebnf)))

;; (defn num-of-items-and-rest-of-array [item-type string]
;;   (let [root (-> (str "DATA = NUM_OF_ITEMS <DELIM> REST; NUM_OF_ITEMS = #'[0-9]+';" data-ebnf)
;;                  (insta/parser :output-format :enlive)
;;                  (apply [string])
;;                  clojure.zip/xml-zip)]
;;     (let [num-of-items (-> root
;;                            (xml/xml1-> :DATA :NUM_OF_ITEMS xml/text)
;;                            Integer/parseInt)
;;           rest (-> root
;;                    (xml/xml1-> :DATA :REST xml/text))]
;;       [num-of-items rest])))
