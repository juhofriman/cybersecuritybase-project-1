(ns cybersecuritybase-project-1.templates
  (:require [net.cgrand.enlive-html :as html]
            [clj-time.format :as f]))

(def time-formatter (f/formatter "d.M.yyyy HH:mm"))

(html/deftemplate login-template "templates/login.html"
  []
  [:head :title] (html/content "Please login")
  [:body :h1] (html/content (str "Please login ")))

(html/defsnippet login-info "templates/snippets/login-info.html"
  [:div.login]
  [principal]
  [:span.principal] (html/content (str "Hello, " principal "!")))

(html/defsnippet message-listing "templates/snippets/message-listing.html"
  [:div.listing]
  [messages]
  [:div.listing :div.message] (html/clone-for [{topic :topic
                                                message :message
                                                sender :sender
                                                timestamp :timestamp} messages]
                                              [:div.topic] (html/content  topic)
                                              [:div.ellipsis] (html/content message)
                                              [:div.sender] (html/content sender)
                                              [:div.time] (html/content (f/unparse time-formatter timestamp))))

(html/defsnippet new-message "templates/snippets/new-message.html"
  [:div.message]
  [])


(html/deftemplate main-template "templates/main.html"
  [{:keys [principal]} content-snippet]
  [:head :title] (html/content "Application name")
  [:body :div.login] (html/append (login-info principal))
  [:body :h1] (html/content (str "Nice, you're in " principal))
  [:body :div.content] (html/content content-snippet))


