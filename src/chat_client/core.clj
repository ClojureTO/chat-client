(ns chat-client.core
  (:require [clojure.java.io :as io])
  (:import (java.net Socket SocketException)))

(def conn (atom nil))

(defn close-connetion! []
  (when-let [c @conn]
    (.close (:socket c))
    (reset! conn nil)))

(defn send-message [message]
  (if-let [c @conn]
    (doto (:writer c)
      (.write (str "MSG " message "\n"))
      (.flush))
    (throw (ex-info "Client not connected" {:failed-message message}))))

(defn connect-user! [id]
  (when-let [c @conn]
    (close-connetion!))
  (let [socket (Socket. "localhost" 1234)
        reader (io/reader socket)
        writer (io/writer socket)]
    (reset! conn {:socket socket :reader reader :writer writer})
    (doto writer
      (.write (str "USER " id "\n"))
      (.flush))))

;; Example bot
(defn hello-bot []
  (connect-user! "hello-bot")
  (send-message "hi there")
  (future
    (try
      (loop [msg (.readLine (:reader @conn))]
        (when (nil? msg)   ; readLine returns nil when the connection closes
          (reset! conn nil))

        (when-let [nick (some-> msg
                                (and (re-matches #"^(.*):.*hello.*" msg))
                                second)]
          (send-message (str "heeeeelllllloooooo " nick "!")))
        ;; if you want to terminate the future and kill the function,
        ;; do this: (reset! chat-client.core/conn nil)
        (when @conn
          (recur (.readLine (:reader @conn)))))
      (catch Throwable e
        (println (.getMessage e) "sending" (-> e ex-data :failed-message))))))
