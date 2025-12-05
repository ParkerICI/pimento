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
  [:div
   [qbox/view :pquery
    :width "800px"
    :title "Query"
    :examples (map (fn [e] {:nl e}) examples)]
   [md/view :response
    :width "800px"
    :height "100%"
    :title "Response"]
   (p/wire :pquery :out :response :in)

   ;; Conversation

   #_ (p/wire :pquery :conversation :pquery :conversation) ;for now. really just response id

   ;; Explaination

   [xp/generator-view :xgen
    :initial-x 900
    :initial-y 0
    :width "450px"
    ]

   (p/wire :response :selection :xgen :in)


   #_
   [md/view :x1
    :title "Tumor-Infiltrating Lymphocyte Therapy"
    :content "Tumor-Infiltrating Lymphocyte (TIL) Cell Therapy is a cancer treatment that uses a patient’s own immune cells found inside their tumor. These immune cells (lymphocytes), which can recognize and attack cancer cells, are collected from the tumor, grown and multiplied in the lab, and then infused back into the patient to help their immune system fight the cancer more effectively."
    :initial-x 900 :initial-y 0
    :height nil
    ]

   #_
   [md/view :x2
    :title "organoid"
    :content (md/markdown-content-bare "An **organoid** is a tiny, simplified version of an organ grown in a lab from stem cells. It mimics key features of the real organ—such as its structure and function—but is much smaller and less complex. Researchers use organoids to study how organs work, test drugs, and understand diseases, including cancer.")
    :initial-x 900 :initial-y 0
    :height nil
    ]
   ])
