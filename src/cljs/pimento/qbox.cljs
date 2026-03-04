;;; Copied from hyperphor.nlflame.components.qbox (hate this)

(ns pimento.qbox
  (:require [re-frame.core :as rf]
            [hyperphor.way.form :as form]
            [hyperphor.way.web-utils :as wu]
            [hyperphor.way.api :as api]
            [hyperphor.multitool.core :as u]
            [hyperphor.way.draggable :as d] ;TODO not really
            )
  )

(defmethod form/form-field :textareax
  [{:keys [type path label id hidden? disabled? value-fn style] :as args :or {value-fn identity width "100%"}}]
  [:textarea.form-control
   {:id id
    :auto-focus true                    ;ADDING THIS
    :style style
    :value @(rf/subscribe [:form-field-value path])
;    :disabled false
    :on-change (fn [e]
                 (rf/dispatch
                  [:set-form-field-value path (-> e .-target .-value)]))
    }])

;;; A generic UI component for NL queries with examples.
;;; TODO incorporate error handling (which logically should not be a separate card)
(defn base-ui
  [id {:keys [examples button-label] :or {button-label "Go"} :as args}]
  (let [query @(rf/subscribe [:form-field-value [id :query-text]])
        conversation @(rf/subscribe [:pin id :conversation])] ;TODO hardwiring this, since it is really within the component?
    [:div
     (when examples
       (wu/select-widget
        (u/keyword-conc id :example)
        nil
        #(rf/dispatch [:qbox-recall-example id %])
        (map :nl examples)
        "Choose a sample query or type one below"
        false
        {:padding "5px" :font-size "smaller"} ;TODO change wu/select-widget to accept class args and do this through proper css
        ))
     [:div.hstack.m-3
      [form/form-field {:type :textareax
                        :path [id :query-text]
                        :label "Type a query" ;maybe only if no example
                        :style {:padding "5px"
                                :margin-right "5px"
                                :font-family "sans-serif"
                                }
                        }]
      [:div.hstack.m-3
       [:button.btn.btn-primary
        {:style {:margin-right "3px"}
         :on-click #(rf/dispatch [:qbox-query id query {:conversation conversation}])} ;TODO backend
        button-label]
       (when @(rf/subscribe [:qbox-spin? id])
         [wu/spinner 2])]]]))

(defn view
  [id & {:keys [] :as args}]
  [d/draggable-widget
   (merge
    {:id id
     :title "NL Query"                   ;TODO
     :content [base-ui id args]
     :width "450px"}
    args)]
  )

(rf/reg-event-db
 :qbox-query
 (fn [db [_ id query-text & rest]]
   (api/ajax-get "/api/exec" {:params (merge {:type :qbox :id id :query query-text} (into {} rest))
                              :handler (fn [response] (rf/dispatch [:qbox-start-polling id response]))})
   (assoc-in db [:qbox id :spin?] true)))

(rf/reg-sub
 :qbox-spin?
 (fn [db [_ id]]
   (get-in db [:qbox id :spin?])))


(rf/reg-event-db
 :qbox-recall-example
 (fn [db [_ id text]]
   (-> db
       (assoc-in [:form id :query-text] text)
       )))

;;; TODO not really using multimethod, flesh out or simplify
(defmulti handle-response (fn [id response] id))

(defmethod handle-response :default
  [id response]
  (rf/dispatch [:set-pin id :out response]))

(defmethod handle-response :pquery
  [cid {:keys [text id] :as response}]
  (rf/dispatch [:set-pin cid :out text])
  (rf/dispatch [:set-pin cid :conversation id]))

;;; Polling for background OpenAI responses

(defn- clear-polling
  "Clear polling interval and timeout for a query box."
  [db id]
  (when-let [interval-id (get-in db [:qbox id :poll-interval])]
    (js/clearInterval interval-id))
  (when-let [timeout-id (get-in db [:qbox id :poll-timeout])]
    (js/clearTimeout timeout-id))
  (update-in db [:qbox id] dissoc :poll-interval :poll-timeout :response-id))

(rf/reg-event-db
 :qbox-start-polling
 (fn [db [_ id {:keys [response-id status query] :as initial}]]
   (if (= status "completed")
     (do (handle-response id initial)
         (assoc-in db [:qbox id :spin?] false))
     (let [interval-id (js/setInterval
                        (fn []
                          (api/ajax-get "/api/exec"
                                        {:params {:type :qbox
                                                  :id :poll
                                                  :response-id response-id
                                                  :query query}
                                         :handler (fn [resp]
                                                    (rf/dispatch [:qbox-poll-response id resp]))}))
                        3000)
           timeout-id (js/setTimeout
                       (fn [] (rf/dispatch [:qbox-poll-timeout id]))
                       120000)]
       (-> db
           (assoc-in [:qbox id :poll-interval] interval-id)
           (assoc-in [:qbox id :poll-timeout] timeout-id)
           (assoc-in [:qbox id :response-id] response-id))))))

(rf/reg-event-db
 :qbox-poll-response
 (fn [db [_ id {:keys [status] :as response}]]
   (case status
     "completed"
     (do (handle-response id response)
         (-> (clear-polling db id)
             (assoc-in [:qbox id :spin?] false)))
     "error"
     (do (rf/dispatch [:flash {:class "alert-danger" :message (:message response)}])
         (-> (clear-polling db id)
             (assoc-in [:qbox id :spin?] false)))
     ;; queued, in_progress — keep polling
     db)))

(rf/reg-event-db
 :qbox-poll-timeout
 (fn [db [_ id]]
   (rf/dispatch [:flash {:class "alert-danger" :message "Query timed out. Please try again."}])
   (-> (clear-polling db id)
       (assoc-in [:qbox id :spin?] false))))

