# rhsm-dbus

A Clojure library for parsing `Type System`.
It is a data format used by `dbusctl` responses.

The library can be used by clojure or java code.

## Usage

```clojure
(require '[rhsm.dbus :as dbus])
(let [[values rest-string] (dbus/parse "a{sv} 2 \"first\" i 10 \"second\" b false")]
  (is (= "" rest))
  (is (= {"first" 10
          "second" false})))
```

You can see examples of usage in [the project's unittests].

## License

Copyright © 2017 Entitlement QE team @ Red Hat

[Changelog]

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.


[the project's unittests]: https://github.com/RedHatQE/rhsm-dbus/blob/master/test/rhsm/dbus/parser_test.clj
[Changelog]: https://github.com/RedHatQE/rhsm-dbus/blob/master/CHANGELOG.md
