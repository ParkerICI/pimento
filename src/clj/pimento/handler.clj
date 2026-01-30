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

;;; This is the whole server logic
(defmethod exec :pquery
  [{:keys [query conversation] :as params}]
  (let [aug-query (str query query-extra)
        start-time (ju/now)
        response (openai/rag-response aug-query conversation)
        end-time (ju/now)]
    (in-background
      ;; TODO should store githash or something. And version the text doc collection somehow. 
      (dlog/write-item {:query query
                        :response response ;serialized edn of full response
                        :duration (- (.getTime end-time) (.getTime start-time))}))

    {:id (:id response)
     :text (get-in* response [:output #(= (:type %) "message") :content 0 :text])}))
      
;;; Using Way datafeed mechanism here, could do that elsewhere


(def explain-term-memoized (memoize openai/explain-term))

(defmethod data/data :explain
  [{:keys [term role]}]
  (let [explanation (explain-term-memoized term {:target role}) ]
    [{:explanation explanation}]))
