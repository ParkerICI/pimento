(ns pimento.main
  (:require [pimento.qbox :as qbox]
            [pimento.markdown :as md]
            [pimento.pins :as p])
  )

(def examples
  ["What efforts exist to analyze adverse immune events?"
   "Which researches know the most about CAR-t therapy?"
   "Are there any projects or researchers looking into pancreatinc cancer?"
   "Who does PICI fund at UCSF?"
   ])

(defn ui
  []
  [:div
   [qbox/view :pquery :width "800px" :title "Query" :examples (map (fn [e] {:nl e}) examples)]
   [md/view :response :width "800px" :title "Response"]
   (p/wire :pquery :out :response :in)
   ])
