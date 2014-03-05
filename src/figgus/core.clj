(ns figgus.core
  (:require [cheshire.core :refer :all]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clojure.tools.reader.edn :as edn]))

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

(defn- locate-value [key]
  (let [config-val (get-in @config (string/split key #"\."))
        sysprop-val (System/getProperty key)
        env-val (-> key (string/replace "." "_") (string/upper-case) (get-env))]
    (if env-val
      env-val
      (if sysprop-val
        sysprop-val
        (when config-val
          config-val)))))

(defn- str->type [val]
  "Takes a string representation of a value, and if it is a string attempts to turn it into its type.
   e.g. Takes the string \"[1 2 3]\" and returns a vector [1 2 3]" 
  (if (string? val)
    (let [tval (edn/read-string val)]
      ;; Only return the typed value if it is of one of our whitelisted types.
      ;; Otherwise we return the string representation of it.
      (if (reduce #(or %1 (%2 tval)) false [vector? number? list? map?])
        tval
        val))
    val))

(defn get
  ([key]
     "Return the value of the key, resolving system properties and then loaded properties. Returns nil if it is not found."
     (get key nil))
  ([key default-value]
     "Return the value of the key, resolving system properties and then loaded properties. Returns default-value if it is not found."
     (if-let [val (locate-value key)]
       (str->type val)
       default-value)))

;; Init, loading the default config if it exists.
(defn- init []
  (try
    (log/info "Loading default config")
    (load-config (System/getProperty default-config-sysprop default-config))
    (catch Exception e
      (log/warn "Unable to load the default configuration:" (.getMessage e))))
  nil)
(init)





