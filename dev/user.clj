(ns user
  (:require [ring.adapter.jetty :refer :all]
            [cybersecuritybase-project-1.handler :as csb]
            [cybersecuritybase-project-1.messages :as msg]))

(defn insert-some-state
  []
  (do (msg/persist-user "bob" "spooky")
      (msg/persist-user "liz" "allied")
      (msg/persist-user "mark" "personal")
      (msg/persist-user "jack" "mystery")
      (msg/persist-message {:from "jack" :to :everybody :topic "Hello everybody!" :message "How ya'll doin'? In SECMSG you can send global and private messages."})
      (msg/persist-message {:from "jack" :to "bob" :topic "Hi bob, here's the secret we talked about!" :message "Open safe with code: 73819384"})))

(defn start!
  []
  (msg/init-db!)
  (insert-some-state)
  (run-jetty #'csb/app {:port 3000}))
