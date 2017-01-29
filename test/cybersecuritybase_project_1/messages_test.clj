(ns cybersecuritybase-project-1.messages-test
  (:require [clojure.test :refer :all]
            [cybersecuritybase-project-1.messages :refer :all]))

(defn setup-teardown
  [t]
  (init-db!)
  (t)
  (teardown-db!))

(use-fixtures :each setup-teardown)

(deftest persisting-single-message
  
  (do (persist-message {:from "jack" :to :everybody :topic "Hello" :message "hello hello!"})
      (let [m (fetch-messages "jack") message (first m)]
        (is (= 1 (count m)))
        (is (= "Hello" (:topic message)))
        (is (= "hello hello!" (:message message)))
        (is (some? (:id message)))
        (is (some? (:timestamp message))))))

(deftest persisting-two-messages

  (do (persist-message {:from "jack" :to :everybody :topic "Hello" :message "hello hello!"})
      (persist-message {:from "jack" :to :everybody :topic "Hello" :message "hello hello!"})
      (is (= 2 (count (fetch-messages "jack"))))))

(deftest persisting-private-message

  (do (persist-message {:from "jack" :to "jim" :topic "Hello" :message "hello hello!"})
      (is (= 1 (count (fetch-messages "jim"))))
      (is (empty? (fetch-messages "jack")))))

(deftest fetching-persisted-message
  ; this is tacky test. I mean we just expect entitys identity to be 1...
  (do (persist-message {:from "jack" :to "jim" :topic "Hello" :message "Hello!"})
      (let [message (fetch-message 1)]
        (is (= "Hello" (:topic message)))
        (is (= "Hello!" (:message message)))
        (is (some? (:id message)))
        (is (some? (:timestamp message))))))

(deftest database-user-creation
  ; dunno if this actually reasonable? It could also just update password...
  (do (persist-user "username" "password")
      (is (thrown? Exception (persist-user "username" "password")))))

(deftest database-authenticator

  (do (persist-user "username" "secretpassword")
      (is (authenticate "username" "secretpassword"))
      (is (not (authenticate "wrongusername" "secretpassword")))
      (is (not (authenticate "wrongusername" "wrongpassword")))
      (is (not (authenticate "username" "wrongpassword")))))

(deftest getting-recipients

  (do (persist-user "user1" "pazzz")
      (persist-user "user2" "pizzz")
      (is (= 2 (count (get-users))))))
