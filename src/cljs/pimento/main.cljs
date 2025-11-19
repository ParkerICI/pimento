(ns pimento.main
  (:require [pimento.qbox :as qbox])
  )

(defn ui
  []
  [:div
   [:h1 "Pimento"]
   [:input {:id :query}]
   [:textarea]
   [qbox/view :pimento]])
