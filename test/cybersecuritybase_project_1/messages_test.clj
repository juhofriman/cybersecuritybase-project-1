(ns cybersecuritybase-project-1.messages-test
  (:require [clojure.test :refer :all]
            [cybersecuritybase-project-1.messages :refer :all]))

(defn setup-teardown
  [t]
  (do (init-db!)
      (t)
      (teardown-db!)))

(use-fixtures :each setup-teardown)

(deftest persisting-single-message
  
  (do (persist-message {:from "jack" :to :everybody :topic "Hello" :message "hello hello!"})
      (let [m (fetch-messages "jack") message (first m)]
        (is (= 1 (count m)))
        (is (= "Hello" (:topic message)))
        (is (= "hello hello!" (:message message)))
        (is (some? (:timestamp message))))))

(deftest persisting-two-messages

  (do (persist-message {:from "jack" :to :everybody :topic "Hello" :message "hello hello!"})
      (persist-message {:from "jack" :to :everybody :topic "Hello" :message "hello hello!"})
      (is (= 2 (count (fetch-messages "jack"))))))

(deftest persisting-private-message
  (do (persist-message {:from "jack" :to "jim" :topic "Hello" :message "hello hello!"})
      (is (= 1 (count (fetch-messages "jim"))))
      (is (empty? (fetch-messages "jack")))))
