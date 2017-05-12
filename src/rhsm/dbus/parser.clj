(ns ^{:author "Jan Stavel @ Red Hat"
      :doc "RHSM Parser for DBus Type System"}
    rhsm.dbus.parser
  (:require  [instaparse.core :refer [defparser]]
             [clojure.core.match :refer [match]]
             [rhsm.dbus.ebnf :as ebnf]))

(defparser parse-type-signature
  (str "<TYPE_SIGNATURE> = TYPE TYPE_SIGNATURE | EPSILON"
       ebnf/type-signature-ebnf))

(defparser parse-simple-data
  (str "DATA = SIMPLE_ITEM REST"
       ebnf/simple-data-ebnf))

;; [:TYPE_SIGNATURE [:TYPE [:BASIC [:INTEGER]]] [:TYPE_SIGNATURE]]
(defn parse-data [ts string]
  (loop [[label & rest] ts
         accumulator []
         string string]
    (if-not rest ;; the case: [:TYPE_SIGNATURE] - empty signature, end of the recursion
      accumulator
      (let [[type & next-ts] rest]
        (let [[parsed-value next-string] (match type
                                                [:TYPE [:BASIC [:STRING]]] (parse-simple-data string)
                                                :else [[:TYPE_SIGNATURE] ""])]
          (recur next-ts (conj accumulator "string") next-string))))))

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
