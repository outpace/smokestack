(ns smokestack.example
  (:require [smokestack.middleware :refer [wrap-smokestack]]
            [compojure.core :refer :all]
            [hiccup.core :refer [html]]))

(defn broken [request]
  (throw (ex-info "Oh no!" {:it-is "broken"})))

(defn bad [request]
  (+ 1 "1"))

(defroutes app
  (GET "/" [] (html [:h1 "Smokestack example"]
                    [:p "Click on a broken link to see the exception. Also try curl localhost:3000/bad to see a text response."]
                    [:ul
                     [:li [:a {:href "bad"} "bad"]]
                     [:li [:a {:href "broken"} "broken"]]]))
  (GET "/bad" request (bad request))
  (GET "/broken" request (broken request)))

(def handler (wrap-smokestack app))
