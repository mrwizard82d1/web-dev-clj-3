(ns guestbook.core)

(-> (.getElementById js/document "content")
    (.-innerHTML)
    (set! "Hello Automatically Built ClojureScript World!"))
