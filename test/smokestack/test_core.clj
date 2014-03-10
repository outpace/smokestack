(ns smokestack.test-core
  (:require [clojure.test :refer :all]
            [smokestack.core :refer :all]))

(def e (try (+ 1 "1") (catch Exception e e)))

(deftest core-tests
  (is (locations e))
  (is (slice-source-code "src/smokestack/core.clj" 10))
  (is (exception-messages e))
  (is (exception-stack e)))
