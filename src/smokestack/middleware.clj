(ns smokestack.middleware
  (:require [smokestack.render :as render]))

;; TODO: just set Content-Type to be accept? and use (def renderers {})
(defn text-response [e]
  {:body (render/text-exception e)
   :headers {"Content-Type" "text/text"}})
(defn html-response [e]
  {:body (render/html-exception-page e)
   :headers {"Content-Type" "text/html"}})

(defn wrap-smokestack
  "Middleware to catch exceptions and display contextual code blocks"
  [handler & [opts]]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (merge request
               {:status  500}
               (let [accept (get-in request [:headers "accept"])]
                 (if (and accept (re-find #"^text/html" accept))
                   (html-response e)
                   (text-response e))))))))
