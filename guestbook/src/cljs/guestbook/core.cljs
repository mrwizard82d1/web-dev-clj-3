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

(rf/reg-event-db
 :message/add
 (fn [db [_ message]]
   (update db :messages/list conj message)))


(defn send-message! [fields errors]
  (if-let [validation-errors (validate-message @fields)]
    (reset! errors validation-errors)
    (POST "/message"
          {:format        :json
           :headers       {"Accept"       "application/transit+json"
                           "x-csrf-token" (.-value (.getElementById js/document "token"))}
           :params        @fields
           :handler       (fn [_]
                            (rf/dispatch [:message/add (assoc @fields :timestamp (js/Date.))])
                            (reset! fields nil)
                            (reset! errors nil))
           :error-handler (fn [e]
                            (.log js/console (str e))
                            (reset! errors (-> e
                                               :response
                                               :errors)))})))

(defn errors-component [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (c-str/join error)]))

(defn message-form []
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
         ::on-click #(send-message! fields errors)
         :value     "comment"}]])))

(rf/reg-event-db
 :messages/set
 (fn [db [_ messages]]
   (-> db
       (assoc :messages/loading? false
              :messages/list messages))))

(rf/reg-sub
 :messages/list
 (fn [db _]
   (:messages/list db [])))

(defn get-messages []
  (GET "/messages"
       {:headers {"Accept" "application/transit+json"}
        :handler #(rf/dispatch [:messages/set (:messages %)])}))

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
  (let [messages (rf/subscribe [:messages/list])]
    (rf/dispatch [:app/initialize])
    (get-messages)
    (fn []
      [:div.content>div.columns.is-centered>div.column.is-two-thirds
       (if @(rf/subscribe [:messages/loading?])
         [:h3 "Loading Messages..."]
         [:div
          [:div.columns>div.column
           [:h3 "Messages"]
           [message-list messages]]
          [:div.columns>div.column
           [message-form]]])])))

(defn ^:export start []
  (rdomc/render root [home]))

(start)
