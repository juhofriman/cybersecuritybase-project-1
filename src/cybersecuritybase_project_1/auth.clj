(ns cybersecuritybase-project-1.auth)

(def sessions (atom {}))

(defn- assoc-session [id]
  (do
    (swap! sessions assoc id {})
    id))

(defn valid-session?
  [session-id]
  (some? (get @sessions session-id)))

(defn authenticate!
  [auth-fn username password]
  (if (auth-fn username password)
    (assoc-session "42492024")
    nil))

(defn invalidate!
  [session-id]
  (do
    (swap! sessions dissoc session-id)
    nil))

(defn reset-sessions!
  []
  (reset! sessions {}))
