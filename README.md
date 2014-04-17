# smokestack

Where there's smoke, there's fire!

## Usage

Add [smokestack](https://clojars.org/smokestack) to your project.clj as a dependency and use smokestack.middleware.wrap-smokestack when defining your handler.

![latest-version](https://clojars.org/smokestack/latest-version.svg)

[example](https://github.com/outpace/smokestack/blob/master/src/smokestack/example.clj)


`lein ring server` to run the example

`lein test` to run the tests

## Dev only

I recommend only using smokestack middleware in development mode:

```
(ns middleware.maybe-dev
  (:require [util.config :as config]
            [smokestack.middleware :as smokestack]
            [middleware.bomb-proof :as bomb-proof]
            [ring.middleware.reload :as ring-reload]))

(defn wrap-maybe-dev
  "pass in the var, not the routes"
  [routes-var]
  (if (#{:dev} config/env)
    (-> routes-var
        ring-reload/wrap-reload
        smokestack/wrap-smokestack)
    (bomb-proof/wrap-bomb-proof @routes-var)))
```

## License

Copyright © 2014 Outpace Systems Inc

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
