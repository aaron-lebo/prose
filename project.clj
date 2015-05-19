(defproject prose "0.1.0-SNAPSHOT"
  :description "a simple language, inspired by Io and Ioke, that compiles to Clojure and ClojureScript"
  :url "http://github.com/aaron-lebo/prose"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [instaparse "1.3.5"]]
  :plugins [[lein-cljfmt "0.1.10"]]
  :eval-in-leiningen true)
