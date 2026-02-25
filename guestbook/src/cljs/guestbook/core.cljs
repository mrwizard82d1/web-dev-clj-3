(ns guestbook.core
  (:require [reagent.core :as r]
            [reagent.dom :as dom]))

(dom/render
 ;; Use CSS-style shorthand to set id and class attributes
 [:div#hello.content
  [:h1 "Hello, Reagent World!"]]
 (.getElementById js/document "content"))
