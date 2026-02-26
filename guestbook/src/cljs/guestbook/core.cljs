;; React 18+ **does not** support the `render` method; instead, one must use
;; `reagent.dom.client/create-root` to create the application root. We create
;; this instance only once.

(ns guestbook.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdomc]
            [ajax.core :refer [GET POST]]))

(defonce root (rdomc/create-root (.getElementById js/document "content")))

(defn send-message! [fields]
  (POST "/message"
        {:params @fields
         :handler #(.log js/console (str "response: " %))
         :error-handler #(.error js/console (str "error: " %))}))

(defn message-form []
  (let [fields (r/atom {})]
    (fn []
      [:div
       [:div.field
        [:label.label {:for :name} "Name"]
        [:input.input {:type :text
                       :name :name
                       :on-change #(swap! fields
                                          assoc :name (-> % .-target .-value))
                       :value (:name @fields)}]]
       [:div.field
        [:label.label {:for :message} "Message"]
        [:textarea.textarea
         {:name :message
          :value (:message @fields)
          :on-change #(swap! fields
                             assoc :message (-> %
                                                .-target .-value))}]]
       [:input.button.is-primary
        {:type :submit
         ::on-click #(send-message! fields)
         :value "comment"}]
       [:div {:style {:margin-top "40px"}}
        [:strong "Debug"]
        [:div {:style {:margin-left "10px"}}
          [:p "Name: " (:name @fields)]
          [:p "Message: " (:message @fields)]]]])))

(defn home []
  [:div.content>div.columns.is-centered>div.column.is-two-thirds
   [:div.columns>div.column
    [message-form]]])

(defn ^:export start []
  (rdomc/render root [home]))

(start)
