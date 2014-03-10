(defproject smokestack "0.1.0-SNAPSHOT"
  :description "Where there's smoke, there's fire. Contextual exception reporting middleware."
  :url "https://github.com/outpace/smokestack"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[hiccup "1.0.5"]
                 [garden "1.1.5"]
                 [org.clojure/clojure "1.5.1"]
                 ;TODO: this is only for the example, would like to put it in a profile
                 [compojure "1.1.6"]]
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler smokestack.example/handler})
