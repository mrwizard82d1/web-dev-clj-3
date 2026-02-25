;---
; Excerpted from "Web Development with Clojure, Third Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj3 for more book information.
;---
(defproject guestbook "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[ch.qos.logback/logback-classic "1.5.32"]
                 [cheshire "6.1.0"]
                 [clojure.java-time "1.4.3"]
                 [com.h2database/h2 "2.4.240"]
                 [conman "0.9.6"]
                 [cprop "0.1.21"]
                 [expound "0.9.0"]
                 [funcool/struct "1.4.0"]
                 [luminus-http-kit "0.2.0"]
                 [luminus-migrations "0.7.5"]
                 [luminus-transit "0.1.6"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [markdown-clj "1.12.6"]
                 [metosin/muuntaja "0.6.11"]
                 [metosin/reitit "0.10.0"]
                 [metosin/ring-http-response "0.9.5"]
                 [mount "0.1.23"]
                 [nrepl "1.5.2"]
                 [org.clojure/clojure "1.12.4"]
                 [org.clojure/tools.cli "1.3.250"]
                 [org.clojure/tools.logging "1.3.1"]
                 [org.webjars.npm/bulma "1.0.4"]
                 [org.webjars.npm/material-icons "1.13.2"]
                 [org.webjars/webjars-locator "0.52"]
                 [ring-webjars "0.3.1"]
                 [ring/ring-core "1.15.3"]
                 [ring/ring-defaults "0.7.0"]
                 [selmer "1.13.1"]]

  :min-lein-version "2.0.0"
  
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main ^:skip-aot guestbook.core

  :plugins [[lein-ancient "1.0.0-RC3"]]

  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "guestbook.jar"
             :source-paths ["env/prod/clj" ]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn" ]
                  :dependencies [[pjstadig/humane-test-output "0.11.0"]
                                 [prone "2021-04-23"]
                                 [ring/ring-devel "1.15.3"]
                                 [ring/ring-mock "0.6.2"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [jonase/eastwood "0.3.5"]] 
                  
                  :source-paths ["env/dev/clj" ]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :timeout 120000}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn" ]
                  :resource-paths ["env/test/resources"] }
   :profiles/dev {}
   :profiles/test {}})
