(ns pimento.core
  (:gen-class)
  (:require [hyperphor.way.server :as server]
            [hyperphor.way.config :as config]
            [pimento.handler :as handler]
            [taoensso.timbre :as log]
            [environ.core :as env]
            [hyperphor.multitool.cljcore :as ju]
            [pimento.logging.dynamo :as l]
            ))

(defn main1
  [port]
  (log/info "Starting server on port" port)
  (server/start (Integer. port) (handler/app))
  ;; Smart enough to be a no-op on server
  (ju/open-url (format "http://localhost:%s" port))
  )

(defn -main
  [& args]
  (let [port (or (first args) (env/env :port))
        config (or (second args) "config.edn")]
    (config/read-config config)
    (main1  (or port (env/env :port) ))))

;;; Startup bypassing OAuth
(defn dev-mode
  [port]
  (config/set-config-map!
   (merge (config/read-config "config.edn")
          {:dev-mode? true
           :oauth nil}))
  (main1 port))
  
(defn report
  []
  (l/report))
