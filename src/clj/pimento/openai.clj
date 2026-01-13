;;; Copied from kg

(ns pimento.openai
  (:require [hato.client :as client]
            [clojure.data.json :as json]
            [environ.core :as env]
            [clojure.string :as str]
            [hyperphor.multitool.core :as u]
            ))

(defn api-key
  []
  (env/env :openai-api-key))

(def default-model "gpt-5-mini")

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

(defn text-query
  [text]
  (-> {:model default-model
       :messages [{:role "system" :content system-prompt}
                  {:role "user" :content text}
                  ]}
      run-chat-completion
      (get-in [:choices 0 :message :content])
      ))

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

(defn key-concepts
  [text]
  (-> {:model default-model
       :messages [{:role "system" :content system-prompt}
                  {:role "user" :content   "Identify the key topics in the following text. Include the key phrase or concept and what type it is and/or its role. Output as an array of json maps, each should have fields: term, kind, description. where kind is eg gene, protein, disease, description is a one line description "}
                  {:role "user" :content text}
                  ]}
      json-query
      first
      )
  )

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


;;; Returns frull response, caller can unpack
(defn run-response
  [q & [params]]
  (api-post "/responses"
            (merge
             {:model "gpt-4.1"
              :input q
              ;; :conversation ...
              ;; :instructions
              }
             params)
            ))

(defn rag-response
  [q & [previous_response_id]]
  (run-response
   q
   {
    ;; This causes chunks and doc referenmces to be returned
    ;; :include ["file_search_call.results"]
    :previous_response_id previous_response_id
    :tools [{:type "file_search",
             :vector_store_ids [pici-vector-store]
             :max_num_results 12        ;Was 20, but hopefully this will be faster
             }]}))

(defn rag-plus-compute-response
  [q]
  (run-response
   []
   {
    ;; This causes chunks and doc referenmces to be returned
    :include ["file_search_call.results"]
    :tools [{:type "file_search",
             :vector_store_ids [pici-vector-store]
             :max_num_results 20        ;Was 20, but hopefully this will be faster
             }
            #_ {:type "code_interpreter"
             :container {:type "auto"}
             }]
    }))

(def targets
  ["a five-yaar old"
   "a high-school student"
   "an intelligent layman"
   #_  "a stupid layman"                    ;well they exist ... probably not going to be reading this though!
   "a scientist in a different field"
   "a religious fundamentalist"])

(defn explain-term
  [term & {:keys [target] :or {target "normal person"}}]
  (->
   (run-response (u/tx "Explain the term '{{term}}' as if talking to a {{target}}")
                 {:instructions "You are an expert in cancer immunotheraphy. Keep responses brief and omit introductions and calls to action"})
   (get-in [:output 0 :content 0 :text])))

(defn explain-multiple
  [term]
  (zipmap targets (map (fn [target] (explain-term term :target target)) targets)))


;;; Not yet used, streaming needs to work on fe, maybe with websocket? Argh
;;; Returns a stream of events, see https://platform.openai.com/docs/guides/streaming-responses?api-mode=responses
(defn run-response-stream
  [q & params]
   (api-post-stream
    "/responses"
    (merge
     {:model "gpt-4.1"
      :input q
      :stream true
      ;; :conversation ...
      ;; :instructions
      ;; :previous_response_id

      ;; This causes chunks and doc referenmces to be returned
      ;; :include ["file_search_call.results"]
      :tools [{:type "file_search",
               :vector_store_ids [pici-vector-store]
               :max_num_results 20
               }]
      }
     params)))

(defn print-stream
  [stream]
  (let [reader (clojure.java.io/reader stream)
        lines (take-while identity (repeatedly #(.readLine reader)))]
    (doseq [line lines]
      (when (str/starts-with? line "data:")
          (let [data (json/read-str (subs line 6) :key-fn keyword)
                delta (:delta data)]
            (when delta
              (print delta))
            )))))
            
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
