(ns guestbook.core
  (:require [reagent.core :as r]
            [reagent.dom :as dom]))

(dom/render
 ;; Specify tag and attributes **without** shortcuts
 [:div {:id "hello", :class "content"}
  [:h1 "Hello, Reagent World!"]]
 (.getElementById js/document "content"))
