(ns ^{:author "Jan Stavel @ Red Hat"
      :doc "RHSM Parser for DBus Type System"}
    rhsm.dbus.parser
  (:require  [instaparse.core :refer [defparser]]
             [rhsm.dbus.ebnf :as ebnf]))

(defparser parse-type-signature
  (str "TYPE_SIGNATURE = TYPE TYPE_SIGNATURE | EPSILON"
       ebnf/type-signature-ebnf))

;; (def object-type-parser
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
