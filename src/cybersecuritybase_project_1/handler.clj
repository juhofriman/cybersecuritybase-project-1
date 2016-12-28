(ns cybersecuritybase-project-1.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.params :refer [wrap-params]]
            [cybersecuritybase-project-1.auth :as auth]
            [net.cgrand.enlive-html :as html]))

(html/deftemplate login-template "templates/login.html"
  []
  [:head :title] (html/content "Please login")
  [:body :h1] (html/content (str "Please login ")))

(html/deftemplate main-template "templates/main.html"
  []
  [:head :title] (html/content "Application name")
  [:body :h1] (html/content (str "Nice, you're in")))

(defn authenticator
  [username password]
  (and (= username "bob") (= password "spooky")))

(defroutes app-routes
  (GET "/" {cookies :cookies}
       (if (auth/valid-session? (get-in cookies ["ses_id" :value]))
         (main-template)
         (login-template)))
  (POST "/login.html" [username password]
        (if-let [auth-id (auth/authenticate! authenticator username password)]
          {:status 302 :headers {"Location" "/"} :cookies {"ses_id" {:value auth-id}}  :body ""}
          {:status 302 :headers {"Location" "/?error=invalid-credentials"} :body ""}))
  (POST "/logout.html" {cookies :cookies}
        (do
          (auth/invalidate! (get cookies "ses_id"))
          {:status 302 :headers {"Location" "/"} :cookies { "ses_id" {:value "" :max-age -1}}}))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-params)
      (wrap-cookies)))
