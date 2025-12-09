(ns pimento.main
  (:require [pimento.qbox :as qbox]
            [pimento.markdown :as md]
            [pimento.explain :as xp]
            [pimento.pins :as p])
  )

(def examples
  ["What efforts exist to analyze adverse immune events?"
   "Which researchers know the most about CAR-T therapy?"
   "Are there any projects or researchers looking into pancreatic cancer?"
   "Who does PICI fund at UCSF?"
   "what grants have gone to Dr. Beatriz Carreno?"
   "Tell me everthing about this project \"Mutant KRAS TCR – from Proof-of-Concept clinical trial to second generation TCR therapy.\""
   "List the 10 most recent PICI grants for amounts over $1,000,000."
   "Who is using LLMs for understanding cancer and therapy mechanisms? Please list relevant projects."
   "tell me about Allison Betof Warner's work on therapy evolution"
   "Explain the role of organoid models in immunotherapy research in layman's terms."
   "What kind of unique IP has PICI developed?"
   "Tell me everything about the pHLA-TCR Mapping Platform with a focus on IP."
   ])

(defn ui
  []
  (let
      [v
       [:div.hstack.gap-3
        [:div.vstack.col-md-1.gap-3
         [qbox/view :pquery
          :width "100%"
          :title "Query"
          :examples (map (fn [e] {:nl e}) examples)]
         [md/view :response
          :width "100%"
          :height "100%"
          :title "Response"]]
        [:div.vstack.col-md-1.gap-3
         [xp/generator-view :xgen
          :width "450px"
          ]
         [xp/saved-terms]]]]    

    (p/wire :pquery :out :response :in)

    ;; Conversation

    #_ (p/wire :pquery :conversation :pquery :conversation) ;for now. really just response id

    ;; Explaination

    (p/wire :response :selection :xgen :in)
    v
    ))
