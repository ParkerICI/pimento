(ns pimento.core
  (:require
   [com.hyperphor.way.tabs :as tabs]
   [com.hyperphor.way.ui.init :as init]
   [com.hyperphor.way.flash :as flash]
   [com.hyperphor.way.modal :as modal]
   com.hyperphor.way.feeds
   [com.hyperphor.way.ui.config :as config]
   [re-frame.core :as rf]
   [org.candelbio.multitool.browser :as browser]
   [com.hyperphor.way.web-utils :as wu]
   [pimento.main :as main]
   [pimento.eli :as eli]
   )) 

(defn header
  []
  [:div.header
   [:h1 "PIMENTO: PICI Institutional Memory"
    [:span.m-3 
     (when @(rf/subscribe [:loading?])
       (wu/spinner 1))]
    ]])

(defn about
  []
  [:div.p-3 {:style {:width "800px"}}
   [:p
    "PIMENTO is a tool for querying PICI's institutional memory. You can ask about research projects, network members, grants, results, companies, or anything else related. See the sample questions selector on the main page."]

   [:p "Current dataset includes scientific meeting summaries, project reviews, grant funding, proposals, site reports."]

   #_ [:p "A product of " [:a {:href "https://hyperphor.com"} "Hyperphor"]", sponsored by the " [:a {:href "https://parkerici.org"} "Parker Institute for Cancer Immunotherapy"] "."]
   
   [:p
    "Feedback is welcome, please  post to "
    [:a {:href "https://pici.slack.com/channels/pimento"} "#pimento"]
    " or email "
    [:a {:href "mailto:mtravers@parkerici.org"} "Mike Travers"], 
    [:a {:href "https://github.com/parkerici/pimento"} ]]
   [:p
    [:a {:href "https://github.com/parkerici/pimento"} "Source"]]
   ])

;;; Email to Slack
;;; #pimento <pimento-aaaaspsx6iaqn7hwts7pgu2q6y@pici.slack.com>

;;; TEMP replaces one in  way/ionit.cljs
;;; No aparently not loaded in time, put it in core
(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   {:app (config/config :app-title)
    :route [:home]             ;TODO router not in Way yet, but Traverse uses this
    }))

(defn app-ui
  []
  [:div
   [modal/modal]
   [header]
   [flash/flash]
   [tabs/tabs-nav
    :tab
    (array-map
     :main main/ui
     :about about
     :ELI eli/elit
     )
    []]
   ])

;;; TODO back to → way
(defn way-init
  [app-ui inits]
  (reset! init/root-ui app-ui)
  (config/init
   #(let [path (browser/browser-pathname)]
      (rf/dispatch-sync [:initialize-db])
      ;; TODO this shoudl go away, handle in nav, but not quite working. See AARDVARK
      (rf/dispatch-sync [:set-path path])
      (when inits (inits))
      (init/mount-root)
      )))

(defn ^:export init
  []
  #_ (init/init app-ui nil)
  (way-init app-ui nil))





