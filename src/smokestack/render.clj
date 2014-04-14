(ns smokestack.render
  (:require [smokestack.core :refer [exception-chain exception-stack]]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [hiccup.core :refer [html]]
            [garden.core :refer [css]]))

(def style
  (css [:html {:background "black"
               :color "white"
               :font-size "85%"
               :font-family "Consolas, \"Liberation Mono\", Courier, monospace"}]

       [:.msg
        [:h1 :h3
         {:font-weight 800
          :font-size "1em"
          :line-height "40px"
          :padding [["10px" "0" "10px" "20px"]]
          :background "green"}]]

       [:.msg
        [ :h3
         {:font-weight 600
          :font-size "0.8em"
          :line-height "30px"
          :margin-top "10px"}]]

       [:code
        {:margin "20px"}]

       [:.error-line
        {:background "#a60000"
         :font-weight 800
         :margin "10px 0"
         :font-size "120%"
         :padding "10px 0"}]

       [:ul
        {:list-style-type "none"}]

       [:.ln
        {:margin-right "10px"}]

       [:.stack
        {:text-align "right"
         :padding-right "30px"}]))

(defn html-code
  "Renders the block of code around a line"
  [[label error-line slices]]
  (if slices
    [:div.file
     [:h3 label]
     [:ul.lines
      (for [[line-number code] slices]
        [:li {:class (if (= line-number error-line)
                       "error-line"
                       "line")}
         [:span.ln line-number]
         [:code code]])]]
    [:div label]))

(defn html-one-exception
  "A single exception as a div"
  [e]
  [:div
   [:div.message
    [:h1 (.getMessage e)]
    (when-let [data (ex-data e)]
      [:code (with-out-str (pprint/pprint data))])
   [:div
    (map html-code (exception-stack e))]]])

(defn html-exception
  "An exception and all its causes as html"
  [e]
  [:div
   (interpose [:h2 "Caused by:"]
              (for [ex (exception-chain e)]
                (html-one-exception ex)))])

(defn html-exception-page
  "Renders the exception as a html page"
  [e]
  (html
   [:head [:style style] [:title "Error"]]
   [:body (html-exception e)]))


(defn text-code
  "Renders the block of code around a line"
  [[label error-line slices]]
  (string/join \newline
               (cons label
                     (for [[line-number code] slices]
                       (format "%s%4d: %s"
                               (if (= line-number error-line)
                                 "*"
                                 " ")
                               line-number
                               code)))))

(defn text-one-exception
  "Renders a single exception as text"
  [e]
  (string/join \newline
               (concat
                (if-let [data (ex-data e)]
                  [(.getMessage e) (with-out-str (pprint/pprint data))]
                  [(.getMessage e)])
                (map text-code (exception-stack e)))))

(defn text-exception
  "Renders the exception and all its causes as text"
  [e]
  (string/join \newline
               (interpose (str \newline  "== Caused By ==")
                          (for [ex (exception-chain e)]
                            (text-one-exception ex)))))
