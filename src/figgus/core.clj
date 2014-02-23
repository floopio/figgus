(ns figgus.core
  (:require [cheshire.core :refer :all]
            [clojure.string :as string]
            [clojure.tools.logging :as log]))

(def ^:private default-config "config.json")

(def ^:private default-config-sysprop "figgus.config")

(def ^:private config (ref {}))

(def ^:private config-location (ref default-config))

(defn get-env [key]
  (System/getenv key))

(defn load-config
  ([]
     "Reloads the configuration from the last loaded location."
     (load-config @config-location))
  ([loc]
     "Loads the configuration from the given location. Raises an exception if the location could not be found."
     (dosync
      (log/info "Loading configuration:" loc)
      (alter config (fn [_] (-> loc slurp parse-string)))
      (alter config-location (fn [_] loc))
      nil)))

(defn get
  ([key]
     "Return the value of the key, resolving system properties and then loaded properties. Returns nil if it is not found."
     (get key nil))
  ([key default-value]
     "Return the value of the key, resolving system properties and then loaded properties. Returns default-value if it is not found."
     (let [config-val (get-in @config (string/split key #"\."))
           sysprop-val (System/getProperty key)
           env-val (-> key (string/replace "." "_") (string/upper-case) (get-env))]
       (if env-val
         env-val
         (if sysprop-val
           sysprop-val
           (if config-val
             config-val
             default-value))))))

;; Init, loading the default config if it exists.
(defn- init []
  (try
    (log/info "Loading default config")
    (load-config (System/getProperty default-config-sysprop default-config))
    (catch Exception e
      (log/warn "Unable to load the default configuration:" (.getMessage e))))
  nil)
(init)





