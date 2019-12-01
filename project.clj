(defproject book-library "0.1.0-SNAPSHOT"
  :description "REST API for Book Library application"
  :license {:name "The MIT License"
            :url  "http://opensource.org/licenses/MIT"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [environ "1.1.0"]
                 [cheshire "5.9.0"]
                 [ring/ring-json "0.5.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-devel "1.6.3"]
                 [com.novemberain/monger "3.5.0"]
                 [buddy/buddy-auth "2.2.0"]]
  :plugins [[lein-ring "0.12.5"]
            [lein-environ "1.1.0"]]
  :ring {:handler book-library.core/app}
  :uberjar-name "book-library-standalone.jar"
  :profiles {:uberjar {:aot :all}
             :test    {:env {:book-library-db "book-library-test"
                             :jwt-secret      "jwtsecret"}}
             :dev     {:dependencies [[javax.servlet/servlet-api "2.5"]
                                      [ring/ring-mock "0.3.2"]]
                       :env          {:book-library-db "book-library-dev"
                                      :jwt-secret      "jwtsecret"}}})
