(ns pimento.core
  (:gen-class)
  (:require [com.hyperphor.way.server :as server]
            [com.hyperphor.way.config :as config]
            [pimento.handler :as handler]
            [taoensso.timbre :as log]
            [environ.core :as env]
            [hyperphor.multitool.cljcore :as ju]
            [pimento.logging.dynamo :as l]
            ))

(defn -main
  [& args]
  (let [port (or (first args) (env/env :port))
        config (or (second args) "config.edn")]
    (config/read-config config)
    (let [port (or port (env/env :port) )]
      (log/info "Starting server on port" port)
      #_ (heroku-deploy-hack)
      (server/start (Integer. port) (handler/app))
      ;; Smart enough to be a no-op on server
      (ju/open-url (format "http://localhost:%s" port))
      )))
