(ns smokestack.core
  (:require [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [clojure.java.classpath :as cp]))

(def context-size 15)

(defn locations
  "Returns [[label error-line ns-str]*]"
  [e]
  (for [element (.getStackTrace e)]
    [element
     (.getLineNumber element)
     (second (re-find #"([^$]*)[$]" (.getClassName element)))]))

(defn slice-source-code
  "Returns [[line-num code]*]"
  [file line-num]
  (let [from-line (- line-num context-size)
        n (inc  (* 2 context-size))]
    (with-open [rdr (io/reader file)]
      (doall (map vector
                  (map inc (range (max 0 from-line) (+ from-line n 1)))
                  (take n (drop from-line (line-seq rdr))))))))

(defn find-source [ns-str]
  (first (for [dir (cp/classpath-directories)
               :let [file (fs/with-cwd dir (fs/ns-path ns-str))]
               :when (fs/readable? file)]
           file)))

(defn exception-stack
  "Returns a sequence of code blocks per frame of an exception stacktrace
  [[label error-line [[line-num code]*]]*]"
  [e]
  (for [[label error-line ns-str] (locations e)
        :let [file (when ns-str (find-source ns-str))]]
    [(if file
       ns-str
       label)
     error-line
     (when file (slice-source-code file error-line))]))

(defn exception-chain
  "Given an exception, returns [[e stack]*] for all causes"
  [e]
  (cons e (when-let [cause (.getCause e)]
            (exception-chain cause))))
