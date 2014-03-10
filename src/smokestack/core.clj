(ns smokestack.core
  (:require [clojure.java.io :refer [as-file reader]]))

(def context-size 15)

;; TODO: want the relative classpath for display, as well as the filename for read
(defn locations
  "Returns [[file line]*]"
  [e]
  (for [element (.getStackTrace e)
        :let [file (as-file  (.getFileName element))
              line (.getLineNumber element)
              ns-str (let [class-name (.getClassName element)
                           i (.indexOf class-name "$")]
                       (when (pos? i)
                         (subs class-name 0 i)))]
        :when (and ns-str file (.exists file))]
    [file line]))

(defn slice-source-code
  "Returns [[line-num [line*]]*]"
  [file line-num]
  (let [from-line (- line-num context-size)
        n (inc  (* 2 context-size))]
    (with-open [rdr (reader file)]
      (doall (map vector
                  (map inc (range (max 0 from-line) (+ from-line n 1)))
                  (take n (drop from-line (line-seq rdr))))))))

(defn exception-stack
  "Returns a sequence of code blocks per frame of an exception stacktrace
  [[file error-line [[line-number code]*]]*]"
  [e]
  (remove nil?
          (for [[file line] (locations e)]
            [file line (slice-source-code file line)])))

(defn exception-messages
  "Given an exception, returns [[message data]*]"
  [e]
  (for [x [e (.getCause e)]
        :when x]
    (if (instance? clojure.lang.ExceptionInfo x)
      [(.getMessage x) (.getData x)]
      [x])))
