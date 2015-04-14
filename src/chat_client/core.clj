(ns chat-client.core
  (:require [clojure.java.io :as io])
  (:import java.io.IOException
           [java.net Socket SocketException]))

(defn create [hostname port]
  (let [socket (Socket. hostname port)
        rdr    (io/reader socket)
        wrt    (io/writer socket)]
    {:socket socket
     :reader rdr
     :writer wrt}))

(defn close [{:keys [socket]}]
  (when (and socket (not (.isClosed socket)))
    (.close socket)))

(defn send-message [{:keys [writer]} message]
  (.write writer (str message "\n"))
  (.flush writer))

(defn read-message [rdr]
  (try (.readLine rdr) (catch IOException _)))

(defn reader-loop [rdr handler]
  (when-let [line (read-message rdr)]    
    (handler line)
    (recur rdr handler)))

(defn start-reader-thread [{:keys [reader]} handler]
  (.start (Thread. #(reader-loop reader handler))))
