;; React 18+ **does not** support the `render` method; instead, one must use
;; `reagent.dom.client/create-root` to create the application root. We create
;; this instance only once.
;;
;; Note that other solutions initialized the root using an atom initially
;; defined as `nil`. The state was changed using either `reset!` or `swap!`.
;; I could not get these solutions to work; I am uncertain why. Sigh...
;;
;; But this code works. (Fingers crossed.)

(ns guestbook.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdom]))

(defonce root (rdom/create-root (.getElementById js/document "content")))

(defn hello-reagent-world []
  [:div#hello.content
   [:h1 "Hello, Reagent World!"]])

(defn ^:export start []
  (rdom/render root [hello-reagent-world]))

(start)
