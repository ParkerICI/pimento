(ns pimento.logging.dynamo
  (:require [cognitect.aws.credentials :as credentials]
            [hyperphor.multitool.core :as u]
            [hyperphor.multitool.cljcore :as ju]
            [cognitect.aws.client.api :as aws]
            [environ.core :as env]
            ))

;;; Fairly generic client for dynamodb (copied from Vaguely)
;;; TODO should go in Way (except I hate dependencies)

(u/defn-memoized client [service]
  (aws/client {:api service
               :region "us-east-1"
               :credentials-provider (credentials/environment-credentials-provider)}))

(defn invoke-with-error
  [service op]
  (let [result (aws/invoke (client service) op)]
    (when (or (:Error result)
              (:cognitect.anomalies/category result))
      (throw (ex-info "AWS error" {:op op :aws-result result})))
    result))


#_  #:shape{AttributeValue
          {:L [:seq-of shape/AttributeValue],
           :M [:map-of string shape/AttributeValue],
           :NS [:seq-of string],
           :BOOL boolean,
           :B blob,
           :SS [:seq-of string],
           :NULL boolean,
           :S string,
           :BS [:seq-of blob],
           :N string}}

;;; This could do more. 
(defn tag-attribute
  [v]
  (cond (boolean? v) {:BOOL v}
        (sequential? v) {:L (map tag-attribute v)}
        :else
        {:S (str v)}))

(defn tag-attributes
  [map]
  (u/map-values tag-attribute map))

(defn untag-attributes
  [map]
  (u/map-values (comp u/coerce-numeric first vals) map))

(def table-name "pimento_log")
(def version 1)                         

(defn list-items-1
  [start-key]
  (let [resp (invoke-with-error
              :dynamodb
              {:op :Scan
               :request {:TableName table-name
                         :ExclusiveStartKey start-key
                         }})]
    (concat 
     (->> resp
          :Items
          (map untag-attributes))
     (when-let [key (:LastEvaluatedKey resp)]
       (list-items-1 key)
       ))))

(defn list-items
  []
  (->> (list-items-1 nil)
       (sort-by :date-created)
       reverse))


(def last-item (atom nil))

(defn assign-uuid
  [map]
  (assoc map :uuid (str (java.util.UUID/randomUUID))))

(defn add-version
  [map]
  (assoc map :version version))

;;; → Multitool


(defn add-datetime
  [map]
  (assoc map :datetime (ju/date-format (ju/now) "YYYY-MM-dd HH:MM:SS")))

(defn add-user
  [map]
  (assoc map :user (env/env :logname))) ;TODO see what this does on Heruo,


(defn write-item
  [map]
  (reset! last-item map)
  (let [item (-> map
                 add-version
                 assign-uuid
                 add-datetime           ;TODO these should be options or configurable
                 add-user
                 )]
    (invoke-with-error
     :dynamodb
     {:op :PutItem
      :request {:TableName table-name
                :Item (tag-attributes item)}})
    ))

(defn delete-item
  [item]
  (invoke-with-error
   :dynamodb
   {:op :DeleteItem
    :request  {:TableName table-name
               :Key (tag-attributes (select-keys item [:datetime]))}}))


;;; TODO have a web page with responses
(defn report
  []
  (sort-by first (map (juxt :datetime :query)
                      (list-items))))
