(defproject cybersecuritybase-project-1 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring "1.5.0"]
                 [enlive "1.1.6"]
                 [digest "1.4.5"]
                 [clj-time "0.13.0"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [com.h2database/h2 "1.3.170"]
                 [org.clojure/data.json "0.2.6"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:init cybersecuritybase-project-1.messages/init-db-with-predefined-state!
         :handler cybersecuritybase-project-1.handler/app
         :nrepl {:start? true}}
  :profiles
  {:dev {:source-paths ["dev"]
         :dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
