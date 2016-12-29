(ns cybersecuritybase-project-1.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cybersecuritybase-project-1.handler :refer :all]))

(defn sets-cookie [response name value]
  (let [cookies (clojure.string/join "&" (get-in response [:headers "Set-Cookie"]))]
    (and (.contains cookies name) (.contains cookies value))))

(defn expires-cookie [response name]
  (let [cookies (clojure.string/join "&" (get-in response [:headers "Set-Cookie"]))]
    (and (.contains cookies name) (.contains cookies "Max-Age=-1"))))

(deftest test-app

  (testing "login should redirect and set cookie for succesfull login"
    (with-redefs-fn {#'cybersecuritybase-project-1.sessions/authenticate! (constantly "hash12345")}
      #(let [response (app (-> (mock/request :post "/login.html")
                               (mock/body {"username" "foo" "password" "bar"})))]
        (is (= (:status response) 302))
        (is (sets-cookie response "ses_id" "hash12345")))))

  (testing "login should redirect for invalid credentials"
    (with-redefs-fn {#'cybersecuritybase-project-1.sessions/authenticate! (constantly nil)}
      #(let [response (app (-> (mock/request :post "/login.html")
                               (mock/body {"username" "foo" "password" "bar"})))]
        (is (= (:status response) 302)))))

  (testing "logout should expire cookie"
    (let [response (app (-> (mock/request :post "/logout.html")))]
      (is (= (:status response) 302))
      (is (expires-cookie response "ses_id"))))

  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))))

  (testing "not-found route"
    (with-redefs-fn
      {#'cybersecuritybase-project-1.sessions/authenticate! (constantly "hash12345")
       #'cybersecuritybase-project-1.sessions/valid-session? (constantly true)}
      #(let [response (app (mock/request :get "/invalid"))]
        (is (= (:status response) 404))))))

(deftest middleware-wrap-auth

  (testing "Must prevent without cookie"
    (let [resp ((wrap-auth (constantly "should not see me") []) {:uri "/" :cookies {} })]
      (is (= (:status resp) 401))))

  (testing "Must pass defined uris"
    (let [resp ((wrap-auth (constantly "response") ["/pass"]) {:uri "/pass"})]
      (is (= "response" resp))))

  (testing "Must allow with valid cookie"
    (with-redefs-fn {#'cybersecuritybase-project-1.sessions/valid-session? (constantly true)}
      #(let [resp ((wrap-auth (constantly "You see me!") []) {:uri "/" :cookies {"ses_id" "49242something2932"}})]
         (is (= "You see me!" resp)))))

  (testing "Must prevent with invalid cookie"
    (with-redefs-fn {#'cybersecuritybase-project-1.sessions/valid-session? (constantly false)}
      #(let [resp ((wrap-auth (constantly "You see me!") []) {:uri "/" :cookies {"ses_id" "49242something2932"}})]
         (is (= (:status resp) 401)))))

  (testing "Must assoc :user"
    (with-redefs-fn
      {#'cybersecuritybase-project-1.sessions/valid-session? (constantly true)
       #'cybersecuritybase-project-1.sessions/get-session (constantly {:principal "bob"})}
      #(let [resp ((wrap-auth (fn [{:keys [user]}] (:principal user)) []) {:uri "/" :cookies {"ses_id" "4294something224"}})]
         (is (= "bob" resp))))))
