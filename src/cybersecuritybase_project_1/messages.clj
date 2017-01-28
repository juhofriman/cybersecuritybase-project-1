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
                                         [[:sender "varchar(200)"]
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

(defn persist-user [username password]
  (j/execute! dbspec ["INSERT INTO users VALUES(?, ?)" username (d/sha-256 password)]))

(defn authenticate [username password]
  (has-one-element? (j/query dbspec ["SELECT * FROM users WHERE username = ? AND password = ?" username (d/sha-256 password)])))

(defn persist-message [{:keys [from to topic message]}]
  (j/execute! dbspec ["INSERT INTO messages VALUES(?, ?, ?, ?, now())" from (if (= :everybody to) nil to) topic message]))

(defn fetch-messages [username]
  (j/query dbspec ["SELECT * FROM messages WHERE recipient IS NULL OR recipient = ?" username]))

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
