(ns user
  (:require [ring.adapter.jetty :refer :all]
            [cybersecuritybase-project-1.handler :as csb]))

(defn start!
  []
  (run-jetty #'csb/app {:port 3000}))
