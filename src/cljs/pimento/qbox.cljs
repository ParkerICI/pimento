;;; Copied from hyperphor.nlflame.components.qbox (hate this)

(ns pimento.qbox
  (:require [re-frame.core :as rf]
            [com.hyperphor.way.form :as form]
            [com.hyperphor.way.web-utils :as wu]
            [com.hyperphor.way.api :as api]
            [hyperphor.multitool.core :as u]
            [com.hyperphor.way.draggable :as d] ;TODO not really
            [com.hyperphor.way.ui.config :as config]
            )
  )

;;; A generic UI component for NL queries with examples.
;;; TODO incorporate error handling (which logically should not be a separate card)
(defn base-ui
  [id {:keys [examples button-label] :or {button-label "Go"} :as args}]
  (let [query  @(rf/subscribe [:form-field-value [id :query-text]])]
    [:div
     (when examples
       (wu/select-widget
        (u/keyword-conc id :example)
        nil
        #(rf/dispatch [:qbox-recall-example id %])
        (map :nl examples)
        "Choose an example or type a query"
        false
        {:width "680px" :padding "5px" :font-size "smaller"} ;TODO change wu/select-widget to accept class args and do this through proper css
        ))
     [:div.hstack.m-3
      [form/form-field {:type :textarea
                        :path [id :query-text]
                        :label "Type a query" ;maybe only if no example
                        :style {:width 600 
                                :padding "5px"
                                :margin-right "5px"
                                :font-family "sans-serif"
                                }
                        }]
      [:div.hstack.m-3
       [:button.btn.btn-primary
        {:on-click #(rf/dispatch [:qbox-query id query])
         :style {:margin-right "3px"}} button-label]
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
 (fn [db [_ id query-text]]
   (api/ajax-get "/api/exec" {:params {:type :qbox :id id :query query-text}
                              :handler (fn [response] (rf/dispatch [:qbox-query-response id response]))})
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

;;; TODO differntiate id and type of service
(defmulti handle-response (fn [id response] id))

(defmethod handle-response :default
  [id response]
  (rf/dispatch [:set-pin id :out response]))

(defmethod handle-response :pquery
  [cid {:keys [text id] :as response}]
  (rf/dispatch [:set-pin cid :out text])
  (rf/dispatch [:set-pin cid :conversation id]))

;;; TODO methodize
(rf/reg-event-db
 :qbox-query-response
 (fn [db [_ id response]]
   ;; TODO!
   #_
   (when (:error response)
     (rf/dispatch [:open-card :nlq :error]))
   (handle-response id response)
   (-> db
       (assoc-in [:qbox id :spin?] false)
       )))

#_
(rf/reg-sub
 :qbox-response
 (fn [db [_ id]]
   (get-in db [:qbox id :response])))

#_
(rf/reg-sub
 :qbox-results
 (fn [db [_ id]]
   (get-in db [:qbox id :response :results])))
