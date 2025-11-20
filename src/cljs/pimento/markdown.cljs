(ns pimento.markdown
  (:require [re-frame.core :as rf]
            #_ [com.hyperphor.way.draggable :as d]
            [com.hyperphor.way.draggable :as d] ;TODO not really
            ["markdown-it" :as MarkdownIt]
            ))

;; Initialize markdown-it with options
(def md (MarkdownIt. #js {:html true
                          :linkify true
                          :typographer true}))

;; Convert markdown to HTML and render safely
(defn markdown->html [text]
  (.render md text))

(defn markdown-content [text]
  [:div {:dangerouslySetInnerHTML {:__html (markdown->html text)}}])

(defn view
  [id & {:keys [] :as args}]
  (let [value @(rf/subscribe [:pin id :in])]
    [d/draggable-widget
     (merge
      {:id id
       :title "Explanation "
       :content
       (when value
         [markdown-content value])
       :width "450px"
       :height "500px"}
      args)]))








