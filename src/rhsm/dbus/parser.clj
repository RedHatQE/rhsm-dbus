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
  (str "<DATA> = STRING REST" ebnf/simple-data-ebnf))

(defparser integer-parser
  (str "<DATA> = INTEGER REST" ebnf/simple-data-ebnf))

(defparser boolean-parser
  (str "<DATA> = BOOLEAN REST" ebnf/simple-data-ebnf))

(defn parse-string [input]
  (let [[result rest-of-string] (string-parser input)]
    (let [[rest-label rest-value] rest-of-string
          value-of-element (fn [el] (if (-> el first (= :ESCAPED_DOUBLE_QUOTE))
                                     "\\\""
                                     (-> el second)))]
      (let [value-of-string (->> result rest (map value-of-element) (reduce str))]
        [value-of-string (.trim rest-value)]))))

(defn parse-boolean [input]
  (let [[result rest] (boolean-parser input)]
    (let [[rest-label rest-value] rest]
      (match result
             [:BOOLEAN "false"] [false (.trim rest-value)]
             [:BOOLEAN "true"]  [true  (.trim rest-value)]
             :else [nil nil]))))

(defn parse-integer [input]
  (let [[result rest] (integer-parser input)]
    (let [[rest-label rest-value] rest]
      (match result
             [:INTEGER _] [(-> result second Integer/parseInt)
                           (.trim rest-value)]
             :else [nil nil]))))

(declare parse)
(declare parse-array)

(defn parsers-for-hashmap [hashmap-type]
  (let [[_ key-type value-type] hashmap-type]
    ;;(println "key-type:" key-type "item-type:" value-type)
    (let [key-parser (match key-type
                            [:KEY [:BASIC [:STRING]]]  parse-string
                            [:KEY [:BASIC [:INTEGER]]] parse-integer
                            [:KEY [:BASIC [:BOOLEAN]]] parse-boolean 
                            [:KEY [:VAR]]              parse)
          value-parser (match value-type
                              [:VALUE [:BASIC [:STRING]]]  parse-string
                              [:VALUE [:BASIC [:INTEGER]]] parse-integer
                              [:VALUE [:BASIC [:BOOLEAN]]] parse-boolean
                              [:VALUE [:VAR]]              parse
                              [:VALUE [:ARRAY _]]          (partial parse-array (-> value-type second second)))
          ]
      [key-parser value-parser])))

(defn parse-hashmap [type input]
  ;;(println "parse-hashmap:" type "input:" input)
  (let [[key-parser value-parser] (parsers-for-hashmap type)
        [num-of-items rest] (parse-integer input)]
    ;;(println "\tnum-of-items:" num-of-items "type:" type "string:" input)
    (loop [ii num-of-items
           rest rest
           accumulator {}]
      ;;(println "\t ii:" ii "string:" rest "accumulator:" accumulator)
      (if (-> ii (<= 0))
        [accumulator rest]
        (let [[parsed-key rest] (key-parser rest)
              [parsed-value rest] (value-parser rest)]
          (recur (dec ii) rest (into accumulator {parsed-key parsed-value})))))))

(defn parse-array-data [num-of-items items-type string]
  ;;(println "parse-array-data - num-of-items:" num-of-items items-type string)
  (let [parser (match items-type
                      [:ARRAY_ITEM [:BASIC [:STRING]]]  parse-string
                      [:ARRAY_ITEM [:BASIC [:INTEGER]]] parse-integer
                      [:ARRAY_ITEM [:BASIC [:BOOLEAN]]] parse-boolean
                      [:ARRAY_ITEM [:HASHMAP _ _]]        (partial parse-hashmap (-> items-type second))
                      [:ARRAY_ITEM [:ARRAY [:ARRAY_ITEM [:HASHMAP _ _]]]] (partial parse-hashmap (-> items-type second second second))
                      [:ARRAY_ITEM [:ARRAY _]] (partial parse-array (-> items-type second second))
                      [:ARRAY_ITEM [:VAR]]              parse)]
    ;;(println "num-of-items:" num-of-items "items-type:" items-type "string:" string)
    (loop [ii num-of-items
           string string
           accumulator []]
      ;;(println "\t ii:" ii "string:" string "accumulator:" accumulator)
      (if (-> ii (<= 0))
        [accumulator string]
        (let [[parsed-value next-string] (parser string)]
          (recur (dec ii) next-string (conj accumulator parsed-value)))))))

;; 3 "string one" "string two" "string three"
;; 3 s "string one" i 10 s "string two"
(defn parse-array [items-type input]
  (let [[result rest] (integer-parser input)
        num-of-items (-> result second Integer/parseInt)
        rest-string  (-> rest second (.trim))]
    (parse-array-data num-of-items items-type (.trim rest-string))))

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
                                              [:TYPE [:BASIC [:BOOLEAN]]] (parse-boolean string)
                                              [:TYPE [:VAR]]              (parse string)
                                              [:TYPE [:ARRAY
                                                      [:ARRAY_ITEM
                                                       [:HASHMAP _ _]]]] (parse-hashmap
                                                                          (-> type second second second)
                                                                          string)
                                              [:TYPE [:ARRAY _]]          (parse-array
                                                                           (-> type second second)
                                                                           string)
                                              :else (list))]
        (recur rest (conj accumulator parsed-value) next-string)))))

(defn parse [string]
  (let [[ts data] (parse-type-signature (.trim string))]
    (let [[result rest] (parse-data ts (.trim data))]
      (if (-> result count (= 1))
        [(first result) rest]
        [result rest]))))
