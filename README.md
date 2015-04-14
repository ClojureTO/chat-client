# chat-client

A Clojure library designed to communicate with the [chat-server](https://github.com/ClojureTO/chat-server).

## Usage

Require the client library in your namespace:

```clojure
(ns myns
  (:require [chat-client.core :as client]))
```

Create a new socket connection by calling the `create` function.
This will return a map containing the keys `:socket`, `:reader`, and `:writer`.

```clojure
(def socket (create "localhost" 1234))
```

We can now create a thread for reading messages from the server by passing it the socket map and a handler function that will be responsible for processing the incoming messages.

```clojure
(def out (atom []))

(defn message-handler [out]
  (fn [message]
    (swap! out conj message)))
    
(start-reader-thread socket (message-handler out))    
```
Above, we create an atom to stored the messages we've received so far and a message handler function that will be used to populate it. The function is a closure that returns the handler. We'll initialize it with the `out` atom and pass it to the `start-reader-thread` function.

We can send messages to the socket using the `send-message` function and close the socket using the `close` function when we're finished.

```clojure
(send-message socket "USER Bob")
(send-message socket "MSG Hi")
(send-message socket "LIST")
(send-message socket "MSG Bye")  
(close socket)
(println @out)
```

  
