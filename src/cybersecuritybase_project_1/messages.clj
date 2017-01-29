(ns cybersecuritybase-project-1.messages
  (:require [clj-time.coerce :as c]
            [clojure.java.jdbc :as j]
            [digest :as d]))

(def dbspec {
    :classname   "org.h2.Driver"
    :subprotocol "h2:mem"
    :subname     "demo;DB_CLOSE_DELAY=-1"
    :user        "sa"
    :password    ""})
 
(defn init-db!
  []
  (j/db-do-commands dbspec
                    [(j/create-table-ddl :messages
                                         [[:id "integer identity"]
                                          [:sender "varchar(200)"]
                                          [:recipient "varchar(200)"]
                                          [:topic "varchar(500)"]
                                          [:message "text"]
                                          [:timestamp "TIMESTAMP"]])
                     (j/create-table-ddl :users
                                         [[:username "varchar(299) PRIMARY KEY"]
                                          [:password "varchar(64)"]])]))

(defn teardown-db!
  []
  (j/db-do-commands dbspec
                    [(j/drop-table-ddl :messages)
                     (j/drop-table-ddl :users)]))

(defn- has-one-element? [seq] (= 1 (count seq)))

(defn persist-user
  "Persists user to system with given username and password. After this user is able to login."
  [username password]
  (j/execute! dbspec ["INSERT INTO users VALUES(?, ?)" username (d/sha-256 password)]))

(defn authenticate
  "Authenticates user with password. Yields true|false."
  [username password]
  (has-one-element? (j/query dbspec ["SELECT * FROM users WHERE username = ? AND password = ?" username (d/sha-256 password)])))

(defn get-users
  "Returns an array of every usename persisted"
  []
  (j/query dbspec ["SELECT username FROM users"]))

(defn persist-message
  "Persists message. Message is expected to be an associative with keys :from :topic :message.
  :to is optional, if it is missing or it equals :everybody, everybody is able to read message."
  [{:keys [from to topic message]}]
  (j/execute! dbspec ["INSERT INTO messages VALUES(default, ?, ?, ?, ?, now())" from (if (= :everybody to) nil to) topic message]))

(defn fetch-messages
  "Fetches messages username is allowed to read"
  [username]
  (j/query dbspec ["SELECT * FROM messages WHERE recipient IS NULL OR recipient = ?" username]))

(defn fetch-message
  "Fetches message by id"
  [id]
  (first (j/query dbspec ["SELECT * FROM messages WHERE id = ?" id])))

; Some utilities for mapping data more easily
(defn clob-to-string [clob]
  (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
    (apply str (line-seq rdr))))

(extend-protocol j/IResultSetReadColumn                                
  java.sql.Date
  (result-set-read-column [v _ _] (c/from-sql-date v))                    
  java.sql.Clob
  (result-set-read-column [v _ _] (clob-to-string v)) 
  java.sql.Timestamp
  (result-set-read-column [v _ _] (c/from-sql-time v)))

(defn init-db-with-predefined-state!
  "Initializes database with some initial state"
  []
  (do (init-db!)
      (persist-user "bob" "spooky")
      (persist-user "liz" "allied")
      (persist-user "mark" "personal")
      (persist-user "jack" "mystery")
      (persist-message {:from "jack" :to :everybody :topic "Hello everybody!" :message "How ya'll doin'? In SECMSG you can send global and private messages."})
      (persist-message {:from "jack" :to "bob" :topic "Hi bob, here's the secret we talked about!" :message "Open safe with code: 73819384"})))
