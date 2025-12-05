(ns pimento.explain
  (:require [re-frame.core :as rf]
            [com.hyperphor.way.draggable :as d] ;TODO not really
            [pimento.clipboard :as clip]
            [pimento.markdown :as md]
            ))

;; Initialize markdown-it with options


;;; Note: reusing id for DOM ID of markdown, seems OK?
(defn generator-view
  [id & {:keys [title] :as args}]
  (let [term @(rf/subscribe [:pin id :in]) ;TODO :term
        explanation (when term (get-in @(rf/subscribe [:data :explain {:term term}])
                                       [0 :explanation]))]
    [d/draggable-widget
     (merge
      {:id id
       :title [:div term
               (when explanation
                 [:span.float-end
                  [:button.btn.btn-secondary.btn-sm ;TODO abstract these buttons
                   {:on-click #(rf/dispatch [::save id])}
                   "Save"]])]
       :content
       (when explanation
         [md/markdown-content-bare explanation])
       }
      (dissoc args :title))]))

;;; This wants to "inherit" some of qbox functionality, man do I miss having a decent object system.














