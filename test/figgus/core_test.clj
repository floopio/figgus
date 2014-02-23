(ns figgus.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :refer :all]
            [figgus.core :as cfg]
            [cheshire.core :refer :all]))

(def ^:private test-config "test/test-config.json")

(defn load-test-config [] (cfg/load-config test-config))

(use-fixtures :each (fn [t] (load-test-config) (t)))

(defn- with-mock-env [envs f]
  (with-redefs-fn {#'figgus.core/get-env (fn [e] (get envs e))}
    #(f)))

(defn- test-prop [name t value]
  (testing "Testing get property"
    (is (not (nil? (cfg/get name)))
        "Ensure that the property exists.")
    (is (= t (type (cfg/get name)))
        "Ensure that the property is of the correct type.")
    (is (= value (cfg/get name))
        "Ensure that the property has the correct value.")))

(deftest test-get-prop-types
  (test-prop "propint" Integer 1234)
  (test-prop "propstr" String "This is a string...")
  (test-prop "propvec" clojure.lang.PersistentVector ["a" "b" "c" "d" "e"])
  (test-prop "propmap.mapint" Integer 4321)
  (test-prop "propmap.mapvec" clojure.lang.PersistentVector [4 3 2 1])
  (test-prop "propmap.mapmap" clojure.lang.PersistentArrayMap {"mapmapstr" "It's nested!"})
  (test-prop "propmap.mapmap.mapmapstr" String "It's nested!"))

(deftest test-get-prop-defaults
  (testing "Testing that default values work"
    (is (= 123456 (cfg/get "some.fake.property" 123456))
        "Ensure that the defaul value is returned when getting an undefined property")
    (is (= 1234 (cfg/get "propint" 99999999))
        "Ensure that the default doesn't override the set value")))

(deftest test-sysprop-has-priority
  (testing "Testing that the system property has priority over a set property."
    (is (= "blah blah" (cfg/get "sysprop"))
        "Ensure the base value for this property is set")
    (System/setProperty "sysprop" "this is not an int")
    (is (= "this is not an int" (cfg/get "sysprop"))
        "Ensure the set system property overrides the set property")
    (is (= "this is not an int" (cfg/get "sysprop" "BAM BAM"))
        "Ensure the set system property overrides the set property, even when a default is provided")))

(deftest test-envvar-has-priority
  (testing "Testing that an env-var set has priority over a configured property"
    (is (= "my-env-prop" (cfg/get "envprop.value"))
        "Ensure the base value for this property is set")
    (with-mock-env {"ENVPROP_VALUE" "new-env-prop"}
      (fn []
        (is (= "new-env-prop" (cfg/get "envprop.value"))
            "Ensure the set env var overrides the set property")
        (is (= "new-env-prop" (cfg/get "envprop.value" "DEFAULT"))
            "Ensure the set env var overrides the set property, even when a default is provided")))))

(deftest test-envvar-over-sysprop
  (testing "Testing that an env-var will override a sysprop."
    (is (= "some-value" (cfg/get "sysenv.value"))
        "Ensure the base value for this property is set")
    (System/setProperty "sysenv.value" "new-value")
    (is (= "new-value" (cfg/get "sysenv.value"))
        "Ensure the set system property overrides the set property")
    (with-mock-env {"SYSENV_VALUE" "new-new-value"}
      (fn []
        (is (= "new-new-value" (cfg/get "sysenv.value"))
            "Ensure the set env var overrides the set system property")
        (is (= "new-new-value" (cfg/get "sysenv.value" "DEFAULT"))
            "Ensure the set env var overrides the set system property, even when a default is provided")))))

(defn- write-to-file [file data]
  (with-open [wrtr (writer file)]
    (.write wrtr (generate-string data))))

(deftest test-reload-config
  (testing "Testing that calling load-config with no params reloads the current config"
    (let [tmp-file (.getPath (java.io.File/createTempFile "test" "json"))]
      (is (nil? (cfg/get "temp-test")))
      (write-to-file tmp-file {"temp-test" 12345})
      (cfg/load-config tmp-file)
      (is (= 12345 (cfg/get "temp-test")))
      (write-to-file tmp-file {"temp-test" 54321})
      ;; reload the configuration
      (cfg/load-config)
      (is (= 54321 (cfg/get "temp-test"))))))
