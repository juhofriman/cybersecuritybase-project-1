(ns cybersecuritybase-project-1.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [net.cgrand.enlive-html :as html]))

(defn authenticate [username password]
  ; actually this could return login hash or nil
  true)

(html/deftemplate login-template "templates/login.html"
  []
  [:head :title] (html/content "Please login")
  [:body :h1] (html/content (str "Please login ")))

(html/deftemplate main-template "templates/main.html"
  []
  [:head :title] (html/content "Application name")
  [:body :h1] (html/content (str "Nice, you're in")))

(defroutes app-routes
  (GET "/" {cookies :cookies}
       (if (contains? cookies "ses_id")
         (main-template)
         (login-template)))
  (POST "/login.html" [username password]
        (if (authenticate username password)
          {:status 302 :headers {"Location" "/"} :cookies {"ses_id" {:value "something"}}  :body ""}
          {:status 302 :headers {"Location" "/?error=invalid-credentials"} :body ""}))
  (POST "/logout.html" []
        {:status 302 :headers {"Location" "/"} :cookies { "ses_id" {:value "" :max-age -1}}})
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      wrap-cookies))
