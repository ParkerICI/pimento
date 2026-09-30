(defproject pimento "0.1.2"
  :description "PICI Institutional Memory"
  :url "https://pimento-348b0b50f416.herokuapp.com/"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.4"]
                 [com.hyperphor/multitool "0.3.3"
                  :exclusions [org.clojure/clojurescript]]
                 [com.hyperphor/way "0.2.8"]
                 [com.hyperphor/ellum "0.1.6"]
                 [org.clojure/data.csv "1.1.0"]

                 [ring-oauth2 "0.3.0"]

                 ;; Logging
                 [com.cognitect.aws/api "0.8.774"
                  :exclusions [org.eclipse.jetty/jetty-http
                               org.eclipse.jetty/jetty-io
                               org.eclipse.jetty/jetty-util]]
                 [com.cognitect.aws/endpoints "871.2.41.6"]
                 [com.cognitect.aws/dynamodb "871.2.39.3"]

                 ;; frontend
                 ;; See packge.json for the real ones
                 
                 ;; This should get inherited from way, but is necessary for some reason
                 [thheller/shadow-cljs "3.3.2"] ;TODO maybe only in dev profile
                 ]
  :main ^:skip-aot pimento.core
  :source-paths ["src/cljc" "src/clj" "src/cljs"] 
  :target-path "target/%s"
  :uberjar-name "pimento-standalone.jar"
  :profiles {:uberjar {:aot :all
                       :resource-paths ["resources"]
                       :prep-tasks
                       ["compile" 
                        ["run" "-m" "shadow.cljs.devtools.cli" "release" "app"]]
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
