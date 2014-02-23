(defproject io.floop/figgus "0.1.1-SNAPSHOT"
  :description "A library for reading configuration from system properties and other locations."
  :url "http://github.com/floopio/figgus"
  :scm {:name "git"
        :url "git@github.com:floopio/figgus.git"}
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cheshire "5.2.0"]
                 [log4j/log4j "1.2.17"]
                 [org.clojure/tools.logging "0.2.6"]]
  :lein-release {:deploy-via :shell
                 :shell ["lein" "deploy" "clojars"]})
