(ns pimento.ingest
  (:require [clojure.data.json :as json]
            [environ.core :as env]
            [clojure.string :as str]
            [hyperphor.multitool.core :as u]
            [hyperphor.multitool.cljcore :as ju]
            [clojure.data.csv :as csv]
            ))


;;; Note: not even doing this, OpenAI says just upload the spreadsheet straight

;;; No this sucks, use something realer
(defn read-tsv-maps
  "Given a tsv file with a header line, returns seq where each elt is a map of field names to strings"
  [f]
  (let [rows (csv/read-csv (clojure.java.io/reader f) :separator \tab)
        cols (map keyword (first rows))]
    (map #(u/clean-map (zipmap cols %))
         (rest rows))))


(comment

  (def awards (->> "/Users/mt/Downloads/2025.12.04 - Export All Awarded - Salesforce Funding Request.tsv"
                   read-tsv-maps
                   #_ (map u/clean-map)))

  (def cols (map keyword (first  (csv/read-csv (clojure.java.io/reader "/Users/mt/Downloads/2025.12.04 - Export All Awarded - Salesforce Funding Request.tsv") :separator \tab))))

  (def col-counts (zipmap cols (map #(count (u/mapf % awards)) cols)))

;;; ⩓⩔⩓ Upload .csv ⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔⩓⩔

  (api-upload "/Users/mt/Downloads/2025.12.04 - Export All Awarded - Salesforce Funding Request.csv")

  {:expires_at nil,
   :purpose "assistants",
   :filename "2025.12.04 - Export All Awarded - Salesforce Funding Request.csv",
   :status "processed",
   :id "file-GFVFZJCahKRpJnr9gqkpDq",
   :bytes 7680911,
   :object "file",
   :created_at 1765173144,
   :status_details nil}

  (store-add-file pici-vector-store  "file-GFVFZJCahKRpJnr9gqkpDq")


;;; Welp, that doesn't work that well, so go back to original plan


  )

;;; Somewhat arbitrary selection; focusing more on science than funding/management
(def interesting-columns
  [
   :Funding_Request_ID__c
   :Project_Title__c
   :Project_Classifications__c
   :Project_Aims__c
   :Project_Outcomes_Summary__c
   :Project_Description_Abstract__c
   :Project_Connection_Detail__c
   :Project_Experimental_Plan__c

   :Funding_Classification__c
   :Funding_Classification_Subtype__c
   :Career_Stage_Impact__c
   :Internal_Open_Contract_Notes__c
   :Timeline_Milestones__c
   :Classification_Notes__c
   :Progress_Reporting__c
   :Keywords__c

   :outfunds__Recommended_Amount__c
   :Project_Budget_Capital_Equip_and_Infra__c
   :MCA_Funding_Amount__c
   :MCA_Grant_Term_1_1_1_1_1_3__c
   :Project_Budget_Total_Direct_Cost__c
   :Subtotal_PICI_Funding_Amount_ONLY__c

   :outfunds__Application_Date__c

   :Location__c
   :Site_Vision_Mission__c

   :Primary_Investigator_s__c
   :Key_Personnel__c
   :Other_Personnel__c
   :Affiliated_Researchers__c
   :Term_Number_of_Years_Active__c

   :Publications__c
   :Inventions__c

   :Disease_Indication__c
   :Indication_Hematological__c
   :Indication_Solid_Tumor_Brain__c
   :Demographic_Age__c
   :Demographic_Sex__c
   :Demographic_Race__c

   :Background_Third_Party_IP__c

   :DEIB_Health_Equity_Categoty__c
   :Outside_Funding_Sources__c
   :Background_Third_Party_IP_Questions__c
   :Challenges_Obstacles__c
   ])

(defn col-label
  [col]
  (str/replace (name col) #"__c$" ""))

;;; Generate seq of markdown lines
(declare awards)
(defn write-md
  ([award]
   (concat
    [""
     (str "- " (:Funding_Request_ID__c award) ": " (:Project_Title__c award))]
    (u/forf [c interesting-columns]
      (when (c award)
        (str "  - " (col-label c) ": " (str (c award)))))))
  ([]
   (mapcat write-md awards)))

(comment 
  (ju/file-lines-out "docs/grants.md" (write-md)))

