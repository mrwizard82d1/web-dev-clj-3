(ns guestbook.core)

(-> (.getElementById js/document "content")
    (.-innerHTML)
    (set! "Hello ClojureScript World!"))
