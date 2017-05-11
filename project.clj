(defproject rhsm-dbus "0.1.0-SNAPSHOT"
  :description "DBUS Type System parser for clojure and java"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [instaparse "1.4.5"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/data.zip "0.1.2"]]
  :test-matcher #"rhsm\..*-test$"
  :profiles {:dev {:dependencies
                   [[fn.trace "1.3.2.0-SNAPSHOT"]]}}
  :plugins [[lein2-eclipse "2.0.0"]
            [quickie "0.4.2"]])
