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
            [book-library.book :as Book]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authorization wrap-authentication]]
            [clojure.walk :refer :all]
            [cheshire.core :refer [parse-string]]))

(defn test-login-enabled []
  (try
    (true? (read-string (env :enable-test-login)))
    (catch Exception e false)))

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

(defn book-not-found [] (not-found "Book not found"))

(defn get-book-handler [id]
  (fn [req]
    (let [book (service/get-book id)]
      (cond
        (nil? book) (book-not-found)
        (= (Book/get-user book) (:sub (:identity req))) (response book)
        :else (book-not-found)))))

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
  "<html>
    <body>
      <h1>Hello World!</h1>
    </body>
    <a href=\"/test-login?user=test@user\">TEST LOGIN</a>
  </html>")

(defroutes app
           (GET "/" [] hello-world)
           (GET "/test-login" []
             (if (test-login-enabled)
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
           (GET "/books/:id" [id]
             (->
               (get-book-handler id)
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
