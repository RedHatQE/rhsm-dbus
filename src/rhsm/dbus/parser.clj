(ns ^{:author "Jan Stavel @ Red Hat"
      :doc "RHSM Parser for DBus Type System"}
    rhsm.dbus.parser
  (:require  [instaparse.core :refer [defparser]]
             [clojure.core.match :refer [match]]
             [rhsm.dbus.ebnf :as ebnf]
             [clojure.string :as str]))

(defparser one-type-and-rest-parser
    (str "<PARTS> = TYPE REST; REST = #'.*';" ebnf/type-signature-ebnf))

(defn parse-type-signature [string]
  (loop [x string
         accumulator ()]
    (let [result (one-type-and-rest-parser x)]
      (if (-> result type (= instaparse.gll.Failure))
        [(reverse accumulator) x]
        (let [[ts rest] result]
          (recur  (-> rest second) (conj accumulator ts)))))))

(defparser string-parser
  (str "<DATA> = (STRING_WITH_ESCAPES | STRING | EMPTY_STRING) REST" ebnf/simple-data-ebnf))

(defparser integer-parser
  (str "<DATA> = INTEGER REST" ebnf/simple-data-ebnf))

(defn parse-string [input]
  (let [[result rest] (string-parser input)]
    (let [[rest-label rest-value] rest]
      (match result
             [:EMPTY_STRING] ["" (.trim rest-value)]
             [:STRING _] [(second result) (.trim rest-value)]
             :else [nil nil]))))

(defn parse-integer [input]
  (let [[result rest] (integer-parser input)]
    (let [[rest-label rest-value] rest]
      (match result
             [:INTEGER _] [(-> result second Integer/parseInt)
                           (.trim rest-value)]
             :else [nil nil]))))

;; (list [:TYPE [:BASIC [:INTEGER]]]  [:TYPE [:BASIC [:INTEGER]]])
(defn parse-data [types string]
  (loop [[type & rest] types
         accumulator []
         string string]
    (if-not type ;; the case: (list)
      [accumulator string]
      (let [[parsed-value next-string] (match type
                                              [:TYPE [:BASIC [:STRING]]]  (parse-string string)
                                              [:TYPE [:BASIC [:INTEGER]]] (parse-integer string)
                                              :else (list))]
        (recur rest (conj accumulator parsed-value) next-string)))))

(defn parse [string]
  (let [[ts data] (parse-type-signature string)]
    (parse-data ts (.trim data))))

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
