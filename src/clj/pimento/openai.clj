(ns pimento.openai
  (:require [clojure.java.io :as io]
            [hyperphor.multitool.core :as u]
            [hyperphor.ellum.providers.openai :as openai]
            ))

;;; Most code that was here has migrated to Ellum

(def system-prompt "You are an expert in scientific developments in cancer immunotherapy and related fields. You like to provide clear and conscise explanations that do not rely on specialized knowledge or jargon")

;;; Files

(defn file-ids
  []
  (->> (openai/list-files)
       (map :id)))

;;; Files

(comment 
  (doseq [f (hyperphor.multitool.cljcore/list-dir-recursive "path/to/docs/")]
    (openai/upload-file f)))

;;; Vector stores

(comment
  (openai/create-vector-store {:name "PICI research docs" :file_ids (file-ids)}))

(def pici-vector-store "vs_691d3d3673808191bd8dcd8a6f64f3c4")

(comment
  (openai/search-vector-store pici-vector-store "what's happening in melanoma?"))

#_
(defn rag-response
  [q & [previous_response_id]]
  (openai/run-response
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
  (openai/run-response
   q
   {:background true
    ;; This causes chunks and doc referenmces to be returned
    ;; :include ["file_search_call.results"]
    :previous_response_id previous_response_id
    :tools [{:type "file_search",
             :vector_store_ids [pici-vector-store]
             :max_num_results 20}]}))

(def targets
  ["a five-year old"
   "a high-school student"
   "an intelligent layman"
   "a scientist in a different field"
   "a religious fundamentalist"])

(defn explain-term
  [term & {:keys [target] :or {target "normal person"}}]
  (->
   (openai/run-response (u/tx "Explain the term '{{term}}' as if talking to a {{target}}")
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
