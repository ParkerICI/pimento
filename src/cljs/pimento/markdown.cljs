(ns pimento.markdown
  (:require [re-frame.core :as rf]
            [com.hyperphor.way.draggable :as d] ;TODO not really
            [pimento.clipboard :as clip]
            ["markdown-it" :as MarkdownIt]
            [clojure.string :as str]
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
  [:div
   {:id (name id)
    :on-mouse-up #(let [selection (.getSelection js/window)
                        selected-text (when selection (str/trim (str selection)))]
                    (when (not (empty? selected-text)) 
                      (rf/dispatch [:set-pin id :selection selected-text])))
    :dangerouslySetInnerHTML {:__html (markdown->html text)}
    }
   ])

(defn markdown-content-bare
  [text]
  [:div
   {:dangerouslySetInnerHTML {:__html (markdown->html text)}
    }
   ])

;;; Note: reusing id for DOM ID of markdown, seems OK?
(defn view
  [id & {:keys [title] :as args}]
  (let [value @(rf/subscribe [:pin id :in])]
    [d/draggable-widget
     (merge
      {:id id
       ;; TODO should show a conf message. Also pull out and generalize
       :title [:div title
               (when value
                 [:span.float-end
                  [:button.btn.btn-secondary.btn-sm
                   #_ {:on-click #(clip/copy-content (name id))} ; copy as html
                   {:on-click #(clip/copy-markdown value)}       ; copy as md
                   "Copy"]])]
       :content
       (when value
         [markdown-content value id])
       :width "450px"
       :height "500px"}
      (dissoc args :title))]))








