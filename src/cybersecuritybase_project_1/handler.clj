(ns cybersecuritybase-project-1.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [] "Hello World Test from cider")
  (route/not-found "Not Found"))

(def app
  app-routes)
