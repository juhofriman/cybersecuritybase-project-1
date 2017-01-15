(ns cybersecuritybase-project-1.messages
  (:require [clj-time.core :as t]))

(defn fetch-messages [username]
  [{:topic "Remember me?" :content "Bla bla bla bla" :sender "Mark" :timestamp (t/date-time 1983 9 12 15 52)}
   {:topic "Really? Please respond!" :message "Bla bla" :sender "Mark" :timestamp (t/date-time 1983 11 3 15 53)}])
