(ns pimento.markdown
  (:require [re-frame.core :as rf]
            [com.hyperphor.way.draggable :as d] ;TODO not really
            [pimento.clipboard :as clip]
            ["markdown-it" :as MarkdownIt]
            ))

;; Initialize markdown-it with options
(def md (MarkdownIt. #js {:html true
                          :linkify true
                          :typographer true}))

;; Convert markdown to HTML and render safely
(defn markdown->html
  [text]
  (.render md text))

(defn markdown-content
  [text id]
  [:div {:id id :dangerouslySetInnerHTML {:__html (markdown->html text)}} ;TODO using a fixed id, bad when we have >1
  ])

;;; Note: reusing id for DOM ID of markdown, seems OK?
(defn view
  [id & {:keys [title] :as args}]
  (let [value @(rf/subscribe [:pin id :in])]
    [d/draggable-widget
     (merge
      {:id id
       ;; TODO should show a conf message. Also pull out and generalize
       :title [:div title (when value [:span.float-end [:button.btn.btn-secondary.btn-sm {:on-click #(clip/copy-content (name id))} "Copy"]])]
       :content
       (when value
         [markdown-content value (name id)])
       :width "450px"
       :height "500px"}
      (dissoc args :title))]))








