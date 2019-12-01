(ns book-library.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]]
            [compojure.core :refer :all]
            [ring.middleware.params :refer :all]
            [ring.middleware.json :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer :all]
            [book-library.book-service :as service]
            [book-library.book-store :as store]
            [book-library.security.jwt :refer :all]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authorization wrap-authentication]]
            [clojure.walk :refer :all]
            [cheshire.core :refer [parse-string]]))

(def backend (backends/jws {:secret     env-secret
                            :token-name "Bearer"}))

(defn book-creation-handler [req]
  (created "/books" (service/create-book (:body req))))

(defn get-books-handler [req]
  (response (service/get-books)))

(defn test-login-handler [req]
  (response
    (create-token
      {:sub (->
              req
              :query-params
              keywordize-keys
              :user)})))

(defn hello-world [req]
  "Hello World!")

(defroutes app
           (GET "/" [] hello-world)
           (GET "/test-login" []
             (->
               test-login-handler
               wrap-params))
           (GET "/books" []
             (->
               get-books-handler
               wrap-json-response))
           (POST "/books" []
             (->
               book-creation-handler
               (wrap-json-body {:keywords? true})
               wrap-json-response))
           (route/not-found "Not Found"))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (-> app
        (run-jetty {:port port}))
    (store/close)))
