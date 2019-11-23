(ns book-library.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]]
            [compojure.core :refer :all]
            [ring.middleware.json :refer [wrap-json-body]]
            [compojure.route :as route]
            [cheshire.core :refer [parse-string]]))

(defn create-a-book [req]
  (let [body (parse-string (:body req) true)]
    {:body   {:name (:name body)}
     :status 201}))

(defroutes app
           (GET "/" [] "Hello World!")
           (POST "/books" req
             (wrap-json-body create-a-book {:keywords? true}))
           (route/not-found "Not Found"))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (run-jetty app {:port port})))
