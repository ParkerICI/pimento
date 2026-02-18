(ns pimento.handler
  (:require [compojure.core :refer [defroutes context GET POST make-route routes]]
            [com.hyperphor.way.handler :as wh]
            [com.hyperphor.way.oauth :as oauth]
            [com.hyperphor.way.views.html :as html]
            [ring.util.response :as response]
            [hyperphor.multitool.core :as u]
            [hyperphor.multitool.cljcore :as ju]
            [pimento.openai :as openai]
            [pimento.logging.dynamo :as dlog]
            [com.hyperphor.way.data :as data]
            )
  )

(defroutes site-routes
  (GET "/authenticated" req
    (response/redirect "/")))

(defmulti exec (fn [params] (:id params)))

(defmethod exec :default
  [params]
  (throw (ex-info "No handler" params)))

(defroutes api-routes
  ;; Could maybe be done under data, but seems kind of different
  (context "/api" []
    (GET "/exec" req
      (let [params (-> (:params req)
                       (update :id keyword)
                       (update :type keyword)
                       (update :conversation #(if (= % "null") nil %)))]
        (wh/content-response
         (exec params))))))

(defn app
  []
  (wh/app (oauth/wrap-oauth site-routes) api-routes))

(defn get*
  [from key]
  (if (and (fn? key) (vector? from))
    (u/some-thing key from)
    (get from key)))

(defn get-in*
  "Like core/get-in but a key (selector) can be a predicate fn, cvalue is first true element. "
  [m ks]
  (reduce get* m ks))

;;; Or, this could go in :instructions field
(def query-extra " Focus on PICI sponsored projects and network members.")

(defmacro in-background
  [& body]
  `(future-call (fn [] ~@body)))

;;; Submit query in background mode to avoid Heroku 30s timeout.
;;; Returns response-id immediately; frontend polls via :poll.
(defmethod exec :pquery
  [{:keys [query conversation] :as params}]
  (let [aug-query (str query query-extra)
        response (openai/rag-response-bg aug-query conversation)]
    {:response-id (:id response)
     :status (:status response)
     :query query}))

;;; Poll for a background response by ID
(defmethod exec :poll
  [{:keys [response-id query] :as params}]
  (let [response (openai/get-response response-id)]
    (case (:status response)
      "completed"
      (do
        (in-background
          (dlog/write-item {:query query
                            :response response}))
        {:status "completed"
         :id (:id response)
         :text (get-in* response [:output #(= (:type %) "message") :content 0 :text])})
      ("failed" "cancelled" "incomplete")
      {:status "error"
       :message (str "Query " (:status response))}
      ;; queued, in_progress
      {:status (:status response)
       :response-id (:id response)})))
      
;;; Using Way datafeed mechanism here, could do that elsewhere


(def explain-term-memoized (memoize openai/explain-term))

(defmethod data/data :explain
  [{:keys [term role]}]
  (let [explanation (explain-term-memoized term {:target role}) ]
    [{:explanation explanation}]))
