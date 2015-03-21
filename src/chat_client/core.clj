(ns chat-client.core
  (:require [clj-sockets.core :as socket]))

(def conn (atom nil))

(defn close-connetion! []
  (when-let [c @conn]
    (socket/close-socket c)
    (reset! conn nil)))

(defn connect-user! [id]
  (when-let [c @conn]
    (close-connetion!))
  (reset! conn (socket/create-socket "localhost" 1234))
  (socket/write-line @conn (str "USER " id)))

(defn send-message [message]
  (if-let [c @conn]
    (socket/write-line c (str "MSG " message))
    (throw (Exception. "no connection available!"))))

;(connect-user! "Foo")

;(send-message "heeeelloooooo")

;(close-connetion!)
