(ns cybersecuritybase-project-1.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [net.cgrand.enlive-html :as html]))

(html/deftemplate main-template "templates/main.html" [])

(defroutes app-routes
  (GET "/" [] (main-template))
  (route/not-found "Not Found"))

(def app
  app-routes)
