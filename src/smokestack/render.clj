(ns smokestack.render
  (:require [smokestack.core :refer [exception-messages exception-stack]]
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
  [[filename error-line slices]]
  [:div.file
   [:h3 filename]
   [:ul.lines
    (for [[line-number code] slices]
      [:li {:class (if (= line-number error-line)
                     "error-line"
                     "line")}
       [:span.ln line-number]
       [:code code]])]])

(defn html-print-exception
  "Renders the exception in HTML"
  [e]
  (html
   [:head
    [:style style]
    [:title "Error"]]
   [:body
    [:div
     (for [[a b] (exception-messages e)]
       [:div.message
        [:h1 a [:h3 b]]])]
    [:div
     (map html-code (exception-stack e))]]))


(defn text-code
  "Renders the block of code around a line"
  [[filename error-line slices]]
  (apply str
         filename \newline
         (for [[line-number code] slices]
           (format "%s%4d: %s\n"
                   (if (= line-number error-line)
                     "*"
                     " ")
                   line-number
                   code))))

(defn text-print-exception
  "Renders the exception as text"
  [e]
  (clojure.string/join \newline
                       (concat
                        (map #(apply str %) (exception-messages e))
                        (map text-code (exception-stack e)))))
