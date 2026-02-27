;; React 18+ **does not** support the `render` method; instead, one must use
;; `reagent.dom.client/create-root` to create the application root. We create
;; this instance only once.

(ns guestbook.core
  (:require [reagent.core :as r]
            [reagent.dom.client :as rdomc]
            [re-frame.core :as rf]
            [ajax.core :refer [GET POST]]
            [clojure.string :as c-str]
            [guestbook.validation :refer [validate-message]]))

(defonce root (rdomc/create-root (.getElementById js/document "content")))

(defn send-message! [fields errors messages]
  (POST "/message"
        {:format        :json
         :headers       {"Accept"       "application/transit+json"
                         "x-csrf-token" (.-value (.getElementById js/document "token"))}
         :params        @fields
         :handler       (fn [_]
                          (swap! messages conj (assoc @fields
                                                      :timestamp (js/Date.)))
                          (reset! fields nil)
                          (reset! errors nil))
         :error-handler (fn [e]
                          (.log js/console (str e))
                          (reset! errors (-> e
                                             :response
                                             :errors)))}))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (c-str/join error)]))

(defn message-form [messages]
  (let [fields (r/atom {})
        errors (r/atom nil)]
    (fn []
      [:div
       [errors-component errors :server-error]
       [:div.field
        [:label.label {:for :name} "Name"]
        [errors-component errors :name]
        [:input.input
         {:type :text
          :name :name
          :on-change #(swap! fields
                             assoc :name (-> % .-target .-value))
          :value (:name @fields)}]]
       [:div.field
        [:label.label {:for :message} "Message"]
        [errors-component errors :message]
        [:textarea.textarea
         {:name      :message
          :value     (:message @fields)
          :on-change #(swap! fields
                             assoc :message (-> % .-target .-value))}]]
       [:input.button.is-primary
        {:type      :submit
         ::on-click #(send-message! fields errors messages)
         :value     "comment"}]])))

(defn get-messages [messages]
  (GET "/messages"
       {:headers {"Accept" "application/transit+json"}
        :handler #(reset! messages (:messages %))}))

(defn message-list [messages]
  [:ul.messages
   (for [{:keys [timestamp message name]} @messages]
     ^{:key timestamp}
     [:li
      [:time (.toLocaleString timestamp)]
      [:p message]
      [:p " - " name]])])

(rf/reg-event-fx
 :app/initialize
 (fn [_ _]
   {:db {:messages/loading? true}}))

(rf/reg-sub
 :messages/loading?
 (fn [db]
   (:messages/loading? db)))

(defn home []
  (let [messages (r/atom nil)]
    (get-messages messages)
    (fn []
      (if @(rf/subscribe [:messages/loading?])
        [:div>div.row>div.span12>h3 "Loading Messages..."]
        [:div.content>div.columns.is-centered>div.column.is-two-thirds
         [:div.columns>div.column
          [:h3 "Messages"]
          [message-list messages]]
         [:div.columns>div.column
          [message-form messages]]]
        ))))

(defn ^:export start []
  (rdomc/render root [home]))

(start)
