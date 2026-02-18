;;; Copied from kg

(ns pimento.openai
  (:require [hato.client :as client]
            [clojure.data.json :as json]
            [environ.core :as env]
            [clojure.string :as str]
            [hyperphor.multitool.core :as u]
            ))

(def default-model "gpt-4.1")

(defn api-key
  []
  (env/env :openai-api-key))


(defn api-get
  [url params]
  (:body
   (client/get (str "https://api.openai.com/v1" url)
              {:query-params params
               :headers {"Authorization" (str "Bearer " (api-key))
                         "OpenAI-Beta" "assistants=v2"}
               :as :json
               })))

(defn list-models
  []
  (api-get "/models" {}))

(defn api-post
  [url params]
  (:body
   (client/post (str "https://api.openai.com/v1" url)
                {:body (clojure.data.json/write-str params)
                 :content-type :application/json
                 :headers {"Authorization" (str "Bearer " (api-key))
                           "OpenAI-Beta" "assistants=v2"}
                 :as :json
                 })))

(defn api-post-stream
  [url params]
  (:body
   (client/post (str "https://api.openai.com/v1" url)
                {:body (clojure.data.json/write-str params)
                 :content-type :application/json
                 :headers {"Authorization" (str "Bearer " (api-key))
                           "OpenAI-Beta" "assistants=v2"}
                 :as :stream
                 ;; :async? true
                 }
                )))

(defn api-upload
  [file]
  (:body
   (client/post "https://api.openai.com/v1/files"
                {
                 :multipart [{:name "purpose" :content "assistants"} 
                             {:name "file" :content (clojure.java.io/file file)}]
                 :headers {"Authorization" (str "Bearer " (api-key))
                           "OpenAI-Beta" "assistants=v2"}
                 :as :json
                 })))

(defn run-chat-completion
  [q]
  (api-post "/chat/completions" q))

(def system-prompt "You are an expert in scientific developments in cancer immunotherapy and related fields. You like to provide clear and conscise explanations that do not rely on specialized knowledge or jargon")



(defn read-json-safe
  [s]
  (try
    (json/read-str s :key-fn keyword)
    (catch Throwable e
      s)))

;;; Extract code from a llm response (md).
;;; Assumes a type, assumes only one code block
;;; Returns [type code non-code-text]
(defn extract-code
  [s]
  (let [[m? type code] (re-find #"```(.*)\n([\s\S]*?)```" s)]
    (when m?
      [(keyword type) code (str/replace s m? "")])))

;;; TODO sometimes it returens separate ```json elements! Argh
(defn extract-json
  [s]
  (let [[type code text] (extract-code s)]
    (if (= type :json)
      [(read-json-safe code) text]
      ;; Or, maybe encoded differently
      (let [xtract (or (re-find #"JSON[\s\S]*?(\[[\s\S]*\])" s)
                       (re-find  #"(?s)[\s\S]*?(\[\s*\{[\s\S]*\}\s*\])" s))] ;finds [{ ... }]
        (if xtract
          [(read-json-safe (second xtract)) s]
          nil)))))

(defn json-query
  [q]
  (-> q
      run-chat-completion
      (get-in [:choices 0 :message :content])
      extract-json))



;;; Entirely new API

;;; Files

(defn file-ids
  []
  (->> (api-get "/files" {})
       :data
       (map :id)))

;;; api-upload

;;; Files

(defn get-file-content
  [id]
  (api-get (u/tx "/files/{{id}}/content") {}))

(comment 
  (doseq [f (hyperphor.multitool.cljcore/list-dir-recursive "/Users/mt/Downloads/Partial Grant Docs/")]
    (upload-file f)))

;;; Vector stores

(comment
  (api-get "/vector_stores" {}))

(defn make-vector-store
  [params]
  (-> (api-post "/vector_stores" params)
      :id))

(comment
  (make-vector-store {:name "PICI research docs" :file_ids (file-ids)}))

(def pici-vector-store "vs_691d3d3673808191bd8dcd8a6f64f3c4")

(defn store-status
  [store-id]
  (api-get (u/tx "/vector_stores/{{store-id}}") {}))

(comment
  (store-status pici-vector-store))

;;; Not normally called, this is done via the search tool during query
(defn search-store
  [id q]
  (api-post (u/tx "/vector_stores/{{id}}/search")
            {:query q}))

(comment
  (search-store pici-vector-store "what's happening in melanoma?"))


;;; Add file to store
(defn store-add-file
  [store-id file-id]
  (api-post (u/tx "/vector_stores/{{store-id}}/files")
            {:file_id file-id}))


;;; Returns full response, caller can unpack
(defn run-response
  [q & [params]]
  (api-post "/responses"
            (merge
             {:model default-model
              :input q}
             params)))

(defn get-response
  "Retrieve a response by ID. Used for polling background requests."
  [response-id]
  (api-get (str "/responses/" response-id) {}))


#_
(defn rag-response
  [q & [previous_response_id]]
  (run-response
   q
   {
    :previous_response_id previous_response_id ;Continue the last conversation
    :tools [{:type "file_search",
             :vector_store_ids [pici-vector-store]
             :max_num_results 12        ;Was 20, reduced to 12 for speed
             }]}))

(defn rag-response-bg
  "Submit a RAG query in background mode. Returns immediately with {:id, :status}."
  [q & [previous_response_id]]
  (run-response
   q
   {:background true
    ;; This causes chunks and doc referenmces to be returned
    ;; :include ["file_search_call.results"]
    :previous_response_id previous_response_id
    :tools [{:type "file_search",
             :vector_store_ids [pici-vector-store]
             :max_num_results 20}]}))

(def targets
  ["a five-yaar old"
   "a high-school student"
   "an intelligent layman"
   "a scientist in a different field"
   "a religious fundamentalist"])

(defn explain-term
  [term & {:keys [target] :or {target "normal person"}}]
  (->
   (run-response (u/tx "Explain the term '{{term}}' as if talking to a {{target}}")
                 {:instructions "You are an expert in cancer immunotheraphy. Keep responses brief and omit introductions and calls to action"})
   (get-in [:output 0 :content 0 :text])))



            
(comment
  ;; Copy stuff from eli.cljs here
  (defn add-term
    [term]
    {:term term
     :explanations (zipmap roles (map #(explain-term term {:target %}) roles))})

  (defn add-role
    [role]
    (map (fn [entry]
           (assoc-in entry [:explanations role]
                     (explain-term (:term entry) :target role)))
         elis)))
