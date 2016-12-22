(ns cybersecuritybase-project-1.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(def app
  app-routes)
