(ns cybersecuritybase-project-1.templates
  (:require [net.cgrand.enlive-html :as html]))

(html/deftemplate login-template "templates/login.html"
  []
  [:head :title] (html/content "Please login")
  [:body :h1] (html/content (str "Please login ")))

(html/deftemplate main-template "templates/main.html"
  [{:keys [principal]}]
  [:head :title] (html/content "Application name")
  [:body :h1] (html/content (str "Nice, you're in " principal)))
