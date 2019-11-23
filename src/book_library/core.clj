(ns book-library.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]]
            [compojure.core :refer :all]
            [ring.middleware.json :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer :all]
            [book-library.book-service :as service]
            [cheshire.core :refer [parse-string]]))

(defn book-creation-handler [req]
  (created "/books" (service/create-book (:body req))))

(defroutes app
           (GET "/" [] "Hello World!")
           (POST "/books" []
             (->
               book-creation-handler
               (wrap-json-body {:keywords? true})
               wrap-json-response))
           (route/not-found "Not Found"))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (run-jetty app {:port port})))
