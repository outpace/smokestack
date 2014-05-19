(defproject smokestack "0.1.2"
  :description "Where there's smoke, there's fire. Contextual exception reporting middleware."
  :url "https://github.com/outpace/smokestack"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.classpath "0.2.2"]
                 [me.raynes/fs "1.4.5"]
                 [hiccup "1.0.5"]
                 [garden "1.1.5"]]
  :profiles {:dev {:dependencies [[compojure "1.1.6"]]}}
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler smokestack.example/handler})
