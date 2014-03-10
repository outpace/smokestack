(ns smokestack.example
  (:require [smokestack.middleware :refer [wrap-smokestack]]
            [compojure.core :refer :all]
            [hiccup.core :refer [html]]))

(defn broken [request]
  (ex-info "Oh no!" {:it "broken"}))

(defn bad [request]
  (+ 1 "1"))

(defroutes app
  (GET "/" [] (html [:a {:href "bad"} "bad"]
                    [:a {:href "broken"} "broken"]))
  (GET "/bad" request (bad request))
  (GET "/broken" request (broken request)))

(def handler (wrap-smokestack app))
