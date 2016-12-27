(ns cybersecuritybase-project-1.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cybersecuritybase-project-1.handler :refer :all]))

(defn sets-cookie [response name]
  (.contains (clojure.string/join "&" (get-in response [:headers "Set-Cookie"] [])) name))

(deftest test-app
  (testing "login should redirect and set cookie for succesfull login"
    (with-redefs-fn {#'cybersecuritybase-project-1.handler/authenticate (constantly true)}
      #(let [response (app (-> (mock/request :post "/login.html")
                               (mock/body {"username" "foo" "password" "bar"})))]
        (is (= (:status response) 302))
        (is (sets-cookie response "ses_id")))))
  (testing "login should redirect for invalid credentials"
    (with-redefs-fn {#'cybersecuritybase-project-1.handler/authenticate (constantly false)}
      #(let [response (app (-> (mock/request :post "/login.html")
                               (mock/body {"username" "foo" "password" "bar"})))]
        (is (= (:status response) 302)))))
  (testing "logout should expire cookie"
    (let [response (app (-> (mock/request :post "/logout.html")))]
      (is (= (:status response) 302))
      (is (sets-cookie response "ses_id"))))
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
