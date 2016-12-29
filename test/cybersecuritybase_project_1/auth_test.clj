(ns cybersecuritybase-project-1.auth-test
  (:require [clojure.test :refer :all]
            [cybersecuritybase-project-1.auth :refer :all]))

(defn reset
  [f]
  (reset-sessions!)
  (f))

(use-fixtures :each reset)

(deftest authentication-tests
  
  (testing "authenticator must accept authentication fn"
    ; Authenticator-fn should return true or false, but authenticate! returns session id
    (is (some? (authenticate! (fn [username password] true) "bob" "spooky")))
    (is (nil? (authenticate! (fn [username password] false) "bob" "spooky"))))

  (testing "non valid session should yield false"
    (is (not (valid-session? "9393833"))))
  
  (testing "on authenticator success a session must be initialised"
    (let [ses-id (authenticate! (constantly true) "bob" "spooky")]
      (is (valid-session? ses-id))))

  (testing "invalidating session"
    (let [ses-id (authenticate! (constantly true) "bob" "spooky")]
      (do
        (is (valid-session? ses-id))
        (invalidate! ses-id)
        (is (not (valid-session? ses-id)))))))
