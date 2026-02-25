(ns guestbook.core
  (:require [reagent.core :as r]
            [reagent.dom :as dom]))

(dom/render
 [:h1 "Hello, Reagent World!"]
 (.getElementById js/document "content"))
