(ns pimento.handler
  (:require [compojure.core :refer [defroutes context GET POST make-route routes]]
            [com.hyperphor.way.handler :as wh]
            [com.hyperphor.way.views.html :as html]
            [ring.util.response :as response]
            [hyperphor.multitool.core :as u]
            [hyperphor.multitool.cljcore :as ju]
            [pimento.openai :as openai]
            [pimento.logging.dynamo :as dlog]
            )
  )

(defroutes site-routes
  ;; None needed for now, it's all SPA
  )

;;; query I guess could be a string or a map
(defmulti exec (fn [id query] id))

(defmethod exec :default
  [id query]
  (throw (ex-info "No handler" {:id id  :query query})))

(defroutes api-routes
  ;; Could maybe be done under data, but seems kind of different
  (context "/api" []
    (GET "/exec" [id query]
      (wh/content-response
       (exec (keyword id) query)))))

(defn app
  []
  (wh/app site-routes api-routes))

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

;;; This is the whole server logic
(defmethod exec :pquery
  [_ query]
  (let [aug-query (str query query-extra)
        response (openai/run-response aug-query)]
    (in-background
      ;; TODO time (duration)
     (dlog/write-item {:query query :response response})) ;TODO conversation 
    {:id (:id response)
     :text (get-in* response [:output #(= (:type %) "message") :content 0 :text])}))
      
