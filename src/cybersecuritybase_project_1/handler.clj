(ns cybersecuritybase-project-1.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.params :refer [wrap-params]]
            [cybersecuritybase-project-1.sessions :as sessions]
            [cybersecuritybase-project-1.templates :as templates]
            [cybersecuritybase-project-1.messages :as messages]
            [clojure.set :refer [rename-keys]]))

(def authenticator messages/authenticate)

(defn rename-keys-fn
  "Renames keys in associative using suplied function. 
  Clojure's own rename-keys expects already given mapping like this
  (rename-keys {:a 1} {:a :b}) -> {:b 1}"
  [associative fn]
  (rename-keys associative (into {} (map #(vector % (fn %))) (keys associative))))

(defn empty-to-nil
  "Transforms a value with key in associave to nil if it is empty"
  [associative key]
  (if (empty? (get associative key))
    (assoc associative key nil)
    associative))

(defroutes app-routes
  (GET "/" {cookies :cookies}
       (let [cookie-value (get-in cookies ["ses_id" :value])]
         (if (sessions/valid-session? cookie-value)
           (templates/main-template (sessions/get-session cookie-value)
                                    (templates/message-listing (messages/fetch-messages (:principal (sessions/get-session cookie-value)))))
           (templates/login-template))))
  (POST "/login.html" [username password]
        (if-let [auth-id (sessions/authenticate! authenticator username password)]
          {:status 302 :headers {"Location" "/"} :cookies {"ses_id" {:value auth-id}}  :body ""}
          {:status 302 :headers {"Location" "/?error=invalid-credentials"} :body ""}))
  (POST "/logout.html" {cookies :cookies}
        (do
          (sessions/invalidate! (get cookies "ses_id"))
          {:status 302 :headers {"Location" "/"} :cookies { "ses_id" {:value "" :max-age -1}}}))
  (GET "/message.html" {user :user qp :query-params} (templates/main-template user (templates/message (messages/fetch-message (get qp "id")))))
  (GET "/new-message.html" {user :user} (templates/main-template user (templates/new-message (messages/get-users))))
  (POST "/new-message.html" {user :user params :form-params}
        (do (messages/persist-message (-> params
                                          (rename-keys-fn keyword)
                                          (empty-to-nil :to)
                                          (assoc :from (:principal user))))
            {:status 302 :headers {"Location" "/"}}))
  (GET "/info.html" [] "Info. This is accessible without session key.")
  (GET "/secret.html" [] "This is a secret. Accessible only with valid session key")
  (route/not-found "Not Found"))

(defn wrap-auth
  "Wraps authentication to requests. Pass is an seq of uris which DO NOT require authentication."
  [handler pass]
  (fn [{:keys [uri cookies] :as req}]
    (if (some #{uri} pass)
      (handler req)
      (let [cookie-value (get-in cookies ["ses_id" :value])]
        (if (sessions/valid-session? cookie-value)
          (handler (assoc req :user (sessions/get-session cookie-value)))
          {:status 401 :body "No dice without login!"})))))

(def app
  (-> app-routes
      (wrap-params)
      (wrap-auth ["/" "/login.html" "/logout.html" "/info.html"])
      (wrap-cookies)))
