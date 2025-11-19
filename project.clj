(defproject pimento "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [com.hyperphor/multitool "0.2.1"]
                 [com.hyperphor/way "0.1.21"]
                 ;; frontend
                 ;; See packge.json for the real ones
                 
                 ;; This should get inherited from way, but is necessary for some reason
                 [thheller/shadow-cljs "3.1.8"] ;TODO maybe only in dev profile
                 ]
  :main ^:skip-aot pimento.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
