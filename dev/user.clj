(ns user
  (:require [ring.adapter.jetty :refer :all]
            [cybersecuritybase-project-1.handler :as csb]
            [cybersecuritybase-project-1.messages :as msg]))

(defn insert-some-state
  []
  (do (msg/persist-user "bob" "spooky")
      (msg/persist-user "liz" "allied")
      (msg/persist-message {:from "Jack" :to :everybody :topic "Hello everybody!" :message "How ya'll doin'?"})
      (msg/persist-message {:from "Jack" :to "bob" :topic "Hi bob, here's my secret key!" :message "3483f83c3rc3rc"})))

(defn start!
  []
  (msg/init-db!)
  (insert-some-state)
  (run-jetty #'csb/app {:port 3000}))
