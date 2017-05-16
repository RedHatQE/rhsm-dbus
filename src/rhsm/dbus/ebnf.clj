(ns rhsm.dbus.ebnf)

(def type-signature-ebnf
  "TYPE = ARRAY | BASIC | VAR;
  BASIC = STRING | INTEGER;
  STRING = <'s'>;
  INTEGER = <'i'>;
  VAR = <'v'>;
  ARRAY = <'a'> ARRAY_ITEM;
  ARRAY_ITEM = BASIC | VAR | HASHMAP;
  HASHMAP = <'{'> KEY VALUE <'}'>;
  KEY = BASIC;
  VALUE = BASIC | VAR;
  DELIM = #' +';")

(def simple-data-ebnf "
  SIMPLE_ITEM = STRING |  INTEGER;
  STRING = <DOUBLE_QUOTE> (ESCAPED_DOUBLE_QUOTE / NO_DOUBLE_QUOTE)* <DOUBLE_QUOTE>
  DOUBLE_QUOTE = '\"';
  NO_DOUBLE_QUOTE = #'[^\"]';
  ESCAPED_DOUBLE_QUOTE = BACKSLASH DOUBLE_QUOTE;
  BACKSLASH = '\\\\';
  INTEGER = #'[0-9]+';
  DELIM = #' +';
  REST = #'.*';")
