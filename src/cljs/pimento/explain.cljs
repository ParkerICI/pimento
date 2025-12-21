(ns pimento.explain
  (:require [re-frame.core :as rf]
            [com.hyperphor.way.draggable :as d] ;TODO not really
            [pimento.markdown :as md]
            ))

;; Initialize markdown-it with options


;;; TODO → way/feeds. This is like [:data ] but will be nil during fetch, instead of returning the old data
(rf/reg-sub
 :data-valid
 (fn [db [_ data-id params]]
   (let [data (get-in db [:data data-id])]
     (let [status (get-in db [:data-status data-id])
           last-params (get-in db [:data-params data-id])
           invalid? (or (nil? data)
                        (= status :invalid)
                        (= status :fetching)
                        (not (= params last-params))
                        )]
       (when-not (= status :error)
         (when invalid?
           (rf/dispatch [:fetch data-id params])))
       (when-not invalid?
         data)))))


;;; Note: reusing id for DOM ID of markdown, seems OK?
(defn generator-view
  [id & {:keys [title] :as args}]
  (let [term @(rf/subscribe [:pin id :in]) ;TODO :term
        role @(rf/subscribe [:pin id :role])
        ;; TODO spinner should be in sane place. 
        explanation (when term
                      (get-in @(rf/subscribe
                                [:data-valid :explain {:term term :role role}]) ;TODO neither regular :data nor this feels right
                              [0 :explanation]))]
    (when term
      (rf/dispatch [:wire id :selection :xgen :in])
      [d/draggable-widget
       (merge
        {:id id
         :title [:span term
                 (when explanation
                   [:span.float-end
                    [:button.btn.btn-secondary.btn-sm.mb-0 ;TODO abstract these buttons
                     {:on-click #(rf/dispatch [::save {:term term
                                                       :explanation explanation
                                                       :id id}])}
                     "Save"]])]
         :content
         (when explanation
           [md/markdown-content explanation id])
         }
        (dissoc args :title))])))

;;; This wants to "inherit" some of qbox functionality, man do I miss having a decent object system.

;;; Sigh this really wants to be a full REPL, but whatever
(defn saved-terms
  []
  (let [saves @(rf/subscribe [::saves])
        widgets
        (for [{:keys [term explanation]} saves
              :let [id (str "term" term)]]
          ^{:key id}
          [d/draggable-widget              ;TODO open/close and a delete affordance
           {:title [:div term]
            :id id
            :width "450px"
            :content
            [md/markdown-content explanation id]
            }])]
    (doseq [w widgets]
      ;; TODO slightly crufty but works
      (rf/dispatch [:wire (:id (second w)) :selection :xgen :in]))
    [:div widgets]))

(rf/reg-event-db
 ::save
 (fn [db [_ save]]
   (rf/dispatch [:set-pin (:id save) :in nil]) ;clear the exp box
   (update db ::saves conj save)))

(rf/reg-sub
 ::saves
 (fn [db _]
   (::saves db)))



