(ns smokestack.pretty-exception
  (:require [clojure.string :as string]
            [hiccup.core :refer [html]]
            [garden.core]))

(defn extract-file-name-and-number
  [^String error-stack]
  (if-not (empty? error-stack)
    (let [ns-index (.indexOf error-stack "$")
          namespace (if (>= ns-index 0)
                      (.substring error-stack 0 ns-index))
          ln-index (.lastIndexOf error-stack ":")
          ln (if (>= ln-index 0)
               (.substring error-stack (inc ln-index) (dec (.length error-stack)))
               "-1")]
      (if-not (or (empty? namespace) (= "smokestack.pretty_exception" namespace))
        [(str (.replace namespace "." "/") ".clj") (Long/parseLong ln)]))))

(defn slice-source-code
  "Returns [source-file-name [[line-number code]*]]"
  [source-file-name error-line-num]
  (if-not (empty? source-file-name)
    (let [path (str (System/getProperty "user.dir") "/src/" source-file-name)
          from-line (- error-line-num 15)
          to-line (+ error-line-num 15)
          error-line-num (dec error-line-num)]
      (if (.exists (java.io.File. ^String path))
        (with-open [rdr (clojure.java.io/reader path)]
          (loop [lines (line-seq rdr)
                 index 0
                 source-code []]
            (if (and (not (empty? lines)) (<= index to-line))
              (recur (rest lines) (inc index)
                     (if (< index from-line)
                       source-code
                       (conj source-code [(inc index) (first lines) (= index error-line-num)])))
              [source-file-name source-code])))))))

(slice-source-code "service/contacts.clj" 50)


(def style [:style "html {
           background:black;
           color:white;
           font-size:85%;
           font-family: Consolas, \"Liberation Mono\", Courier, monospace
          }
          .msg h1,.msg h3{
            font-weight : 800;
            font-size : 1em;
            line-height:40px;
            padding:10px 0 10px 20px;
            background:green;
          }
          .msg h3{
            font-weight :600;
            font-size:0.8em;
            line-height:30px;
            margin-top:10px;
          }
          pre {
            margin:20px;
          }
          .error-line{
           background:#a60000;
           font-weight:800;
           margin: 10px 0;
           font-size :120%;
           padding:10px 0;
          }
          ul{
            list-style-type:none;
          }
          .ln{
            margin-right:10px;
          }
          .stack{
           text-align : right;
           padding-right:30px;
          }"])

(defn html-file-line
  [filename slices]
  [:div.file
   [:h3 filename]
   [:ul.lines
    (for [[line-number code error-line?] slices]
      [:li {:class (if error-line?
                     "error-line"
                     "line")}
       [:span.ln line-number]
       [:code code]])]])

(html-file-line "xxx" [[1 "xxxx" false]
                       [2 "ffff" true]])

(defn exception-messages [e]
  (for [x [e (.getCause e)]
        :when x]
    (if (instance? clojure.lang.ExceptionInfo x)
      [(.getMessage x) (.getData x)]
      [x])))

(def e (try (+ 1 " ") (catch Exception e e)))

(exception-messages e)

(defn exception-stack [e]
  (remove nil?
          (for [x (.getStackTrace e)
                :let [[file line :as file-line] (extract-file-name-and-number (str x))]
                :when file-line]
            (slice-source-code file line))))

(exception-stack e)

(defn exception-details
  [e]
  [(exception-messages e) (exception-stack e)])

(exception-details e)

(defn html-print-exception
  "pretty exception"
  [[messages stack]]
  (html
   [:head
    style
    [:title "Error"]]
   [:body
    [:div
     (for [[a b] messages]
       [:div.message
        [:h1 a [:h3 b]]])]
    [:div
     (for [[filename slices] stack]
       (html-file-line filename slices))]]))

(defn text-file-line [filename slices]
  (apply str
         filename \newline
         (for [[line-number code error-line?] slices]
           (format "%s%4d: %s\n"
                   (if error-line? "*" " ")
                   line-number
                   code))))

(defn text-print-exception
  [[messages stack]]
  (string/join \newline
               (concat
                (for [[a b] messages]
                  (str a b))
                (for [[filename slices] stack]
                  (text-file-line filename slices)))))

(text-print-exception (exception-details e))

#_(def xinfo (clojure.lang.ExceptionInfo. "ahahaha"))



(spit "/Users/timothypratley/err.html" (html-print-exception (exception-details e)))

(defn text-response [details]
  {:body (text-print-exception details)
   :headers {"Content-Type" "text/text"}})
(defn html-response [details]
  {:body (html-print-exception details)
   :headers {"Content-Type" "text/html"}})

(defn wrap-pretty-exception
  "catch exception and pretty print"
  [handler & [opts]]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (merge request
               {:status  500}
               (let [accept (get-in request [:headers "accept"])
                     details (exception-details e)]
                 (if (and accept (re-find #"^text/html" accept))
                   (html-response details)
                   (text-response details))))))))
