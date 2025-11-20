(ns pimento.main
  (:require [pimento.qbox :as qbox]
            [pimento.markdown :as md]
            [pimento.pins :as p])
  )

(defn ui
  []
  [:div
   [:h1 "Pimento"]
   [qbox/view :pquery]
   [md/view :response]
   (p/wire :pquery :out :response :in)
   ])
