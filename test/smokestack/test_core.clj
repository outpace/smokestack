(ns smokestack.test-core
  (:require [clojure.test :refer :all]
            [smokestack.core :refer :all]))

(def e (try (+ 1 "1") (catch Exception e e)))

(deftest core-tests
  (is (seq (locations e)))
  (is (seq (slice-source-code "src/smokestack/core.clj" 10)))
  (is (seq (exception-messages e)))
  (is (seq (exception-stack e))))

(run-tests)
