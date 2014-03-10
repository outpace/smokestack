(ns smokestack.test-render
  (:require [clojure.test :refer :all]
            [smokestack.core :refer :all]
            [smokestack.render :refer :all]))

(def e (try (+ 1 "1") (catch Exception e e)))
(def ei (try (throw (ex-info "Oh noez" {:it "broken"} e)) (catch Exception e e)))

(def code [[1 "line a"]
           [2 "line b"]])

(deftest render-tests
  (is (text-code ["source.clj" 1 code]))
  (is (html-code ["source.clj" 1 code]))
  (is (html-print-exception e))
  (is (text-print-exception e))
  (spit "err.html" (html-print-exception e))
  ;TODO: exceptioninfo doesn't look quite right
  (spit "err2.html" (html-print-exception ei))
  (is (text-print-exception ei)))
