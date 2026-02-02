(ns pimento.clipboard
  (:require [goog.object :as gobj]))

;;; This is less useful than just copying the markdown
;;; So not used at the moment, but might be useful in the future
(defn copy-content [html-element]
  (let [html-element (if (string? html-element)
                       (js/document.getElementById html-element)
                       html-element)
        html (.-innerHTML html-element)
        text (or (.-innerText html-element) 
                 (.-textContent html-element))
        html-blob (js/Blob. #js [html] #js {:type "text/html"})
        text-blob (js/Blob. #js [text] #js {:type "text/plain"})
        clipboard-item (js/ClipboardItem. 
                         #js {"text/html" html-blob
                              "text/plain" text-blob})]
    (-> (.. js/navigator -clipboard (write #js [clipboard-item]))
        (.then #(js/console.log "Content copied!"))
        (.catch #(js/console.error "Failed to copy:" %)))))

;;; Pastes md as plaintext, MacOS doesn't support markdown 
(defn copy-markdown [md]
  (let [md-blob (js/Blob. #js [md] #js {:type "text/plain"})
        clipboard-item (js/ClipboardItem. 
                         #js {"text/plain" md-blob
                              })]
    (-> (.. js/navigator -clipboard (write #js [clipboard-item]))
        (.then #(js/console.log "Content copied!"))
        (.catch #(js/console.error "Failed to copy:" %)))))


;; Usage with a button
#_
(defn setup-copy-button! []
  (when-let [btn (js/document.getElementById "copyBtn")]
    (.addEventListener btn "click"
      (fn [_]
        (when-let [widget (js/document.getElementById "markdown-widget")]
          (copy-content widget))))))
