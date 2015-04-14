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

(defn reader-loop [rdr out]
  (when-let [line (read-message rdr)]    
    (swap! out conj line)
    (recur rdr out)))

(defn start-reader-thread [{:keys [reader]} out]
  (.start (Thread. #(reader-loop reader out))))

;;example usage
;(def socket (create "localhost" 1234))
;(def out (atom []))

;(start-reader-thread socket out)
;(send-message socket "USER Bob")
;(send-message socket "MSG Hi")
;(send-message socket "LIST")
;(send-message socket "MSG Bye")  
;(close socket)
;(println @out)
