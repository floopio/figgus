# figgus

A Clojure library that handles reading configuration from json files (locally and remotely) and allowing system property and environment variable overrides using clojure types.

## Usage

#### Leiningen

    [io.floop/figgus "0.2.3"]

#### Using it

By default figgus looks for a file named `config.json`, this can be set to anything else by setting the `figgus.config` system property. It can even be a URL, as long as it points to JSON.

    (:require [figgus.core :as fig])
    (fig/get "some.property.name" "default-value")
    (fig/get "some.other.property")

##### Types

System properties and environment variables only allow you to set string values - where as the json config allows you to set typed values such as maps, numbers, vectors and strings.

Figgus will attempt to convert the system properties and environment vars to one of those clojure types.

For example, in a repl:

    user=> (System/setProperty "foo.bars" "[:foo :bar :baz]")
    nil
    user=> (fig/get "foo.bars")
    [:foo :bar :baz]
    user=> (System/setProperty "foo.count" "3")
    nil
    user=> (fig/get "foo.count")
    3
    

#### Reloading configuration

You can atomically reload the configuration by invoking the `load-config` function.

    (load-config)
    
This will re-load the config from the last location it was loaded. This function also accepts a parameter specifying a new location to load the configuration from.

#### Environment variables

Environment variables can be used to override system properties and configuration values, the caveat here is that they are in upper case and have the `.` substituted with a `_`.

For example: The system property `figgus.config` could be overridden with the environment variable `FIGGUS_CONFIG`.

The order of evaluation is:

    Environment Variable -> System Property -> Configuration File -> Default Value (if provided)

#### Properties

Properties specified in the configuration location will be the type that they are in the file (integer, string, map, vector).

Properties are referenced by using a `.` delimiter, as you can see with the property `prop-map.prop2.prop3` in the example below; `get`ing `prop-map.prop2` will return you the map that is `prop2`.

Property names that contain a `.` are not yet supported.

    {
        "prop-int": 200,
        "prop-str": "aaa",
        "prop-vec": ["a", "b", "c", "d"],
        "prop-map": {
            "nested-prop": "bbb",
            "prop2": {
                "prop3": "ccc"
            }
        }
    }
        
## License

Copyright © 2014 floop.io

Distributed under the Apache License, Version 2.0.

http://www.apache.org/licenses/LICENSE-2.0.html
