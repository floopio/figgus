(defproject io.floop/figgus "0.2.4-SNAPSHOT"
  :description "A library for reading configuration from system properties and other locations."
  :url "http://github.com/floopio/figgus"
  :scm {:name "git"
        :url "git@github.com:floopio/figgus.git"}
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [org.clojure/tools.reader "0.8.3"]
                 [cheshire "5.2.0"]
                 [log4j/log4j "1.2.17"]]
  :plugins [[lein-cloverage "1.0.2"]
            [lein-kibit "0.0.8"]
            [lein-release "1.0.5"]]
  :lein-release {:deploy-via :shell
                 :shell ["lein" "deploy" "clojars"]})
