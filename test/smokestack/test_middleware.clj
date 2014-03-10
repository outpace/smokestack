(ns smokestack.test-middleware
  (:require [clojure.test :refer :all]
            [smokestack.middleware :refer :all]))

(defn bad [request]
  (+ 1 "1"))

(deftest middleware-tests
  (is (wrap-smokestack bad))
  (is ((wrap-smokestack bad) {})))
