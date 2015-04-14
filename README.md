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

```
(def socket (create "localhost" 1234))
```

We can now create a thread for reading messages from the server by passing it the socket map and an atom to collect the output.
The atom should contain a vector where the incoming messages will be appended as they're received from the server.

```
(def out (atom []))
(start-reader-thread socket out)
```

We can send messages to the socket using the `send-message` function and close the socket using the `close` function when we're finished.

```clojure
(send-message socket "USER Bob")
(send-message socket "MSG Hi")
(send-message socket "LIST")
(send-message socket "MSG Bye")  
(close socket)
(println @out)
```

  
