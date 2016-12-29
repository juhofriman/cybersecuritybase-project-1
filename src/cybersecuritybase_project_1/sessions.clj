(ns cybersecuritybase-project-1.sessions
  (:require [digest :as d]))

(def sessions (atom {}))

(defn- assoc-session
  [id]
  (do
    (swap! sessions assoc id {})
    id))

(defn- generate-session-key
  [seed]
  (d/sha-256 seed))

(defn valid-session?
  [session-id]
  (some? (get @sessions session-id)))

(defn authenticate!
  [auth-fn username password]
  (if (auth-fn username password)
    (assoc-session (generate-session-key username))
    nil))

(defn invalidate!
  [session-id]
  (do
    (swap! sessions dissoc session-id)
    nil))

(defn reset-sessions!
  []
  (reset! sessions {}))
