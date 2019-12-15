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

(defn unauth-handler [request metadata]
  (status metadata 401))

(def backend (backends/jws {:secret               env-secret
                            :token-name           "Bearer"
                            :unauthorized-handler unauth-handler}))

(defn authenticated-user [handler]
  (fn [request]
    (when (not (authenticated? request))
      (throw-unauthorized {:message "Not authorized"}))
    (handler request)))

(defn book-creation-handler [req]
  (created "/books" (service/create-book
                      (merge
                        (:body req)
                        {:user (:sub (:identity req))}))))

(defn get-books-handler [req]
  (response (service/get-books (:sub (:identity req)))))

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
             (if (env :enable-test-login)
               (->
                 test-login-handler
                 wrap-params)
               (route/not-found "Not found")))
           (GET "/books" []
             (->
               get-books-handler
               authenticated-user
               wrap-json-response
               (wrap-authentication backend)
               (wrap-authorization backend)))
           (POST "/books" []
             (->
               book-creation-handler
               authenticated-user
               (wrap-json-body {:keywords? true})
               wrap-json-response
               (wrap-authentication backend)
               (wrap-authorization backend)))
           (route/not-found "Not Found"))

(defn resolve-port [arg]
  (if-let [port (or arg (env :port))]
    (Integer/parseInt port)
    3000))

(defn -main [& [arg]]
  (let [port (resolve-port arg)]
    (-> app
        (run-jetty {:port port}))
    (store/close)))
