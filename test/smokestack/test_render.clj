(ns smokestack.test-render
  (:require [clojure.test :refer :all]
            [smokestack.core :refer :all]
            [smokestack.render :refer :all]))

(def e (try
         (+ 1 "1")
         (catch Exception e
           e)))

(def ei (try
          (throw (ex-info "Oh no" {:it-is "broken" :range (range 10)} e))
          (catch Exception e
            e)))

(def code [[1 "line a"]
           [2 "line b"]])

(deftest render-tests
  (is (text-code ["source.clj" 1 code]))
  (is (html-code ["source.clj" 1 code]))
  (is (html-one-exception e))
  (is (text-one-exception e))
  (is (html-exception e))
  (is (text-exception e))
  (spit "err.html" (html-exception-page e))
  (spit "err.txt" (text-exception e))
  (spit "err2.html" (html-exception-page ei))
  (spit "err2.txt" (text-exception ei))
  (is (html-exception-page ei))
  (is (text-exception ei)))
