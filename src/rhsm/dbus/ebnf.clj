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

(def data-ebnf "
  STRING = <'\"'> #'[^\"]+' <'\"'>;
  STRING_WITH_ESCAPES = <'\"'> (#'[^\"\\\\]+' | #'\\\\.' )+ <'\"'>;
  EMTPY_STRING = <'\"'> <'\"'>;
  INTEGER = #'[0-9]+';
  DELIM = #' +';
  REST = #'.*';")
