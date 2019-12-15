(ns book-library.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [book-library.core :refer :all]
            [book-library.security.jwt :refer :all]
            [cheshire.core :refer :all]
            [book-library.book-store :as store]
            [book-library.book-service :as service]
            [book-library.book :as Book]))

(defn setup [test-fun]
  (store/clear)
  (test-fun))

(use-fixtures :each setup)

(def test-user-token
  (str "Bearer " (create-token {:sub "user1@example.com"})))

(def bad-user-token
  (str "Bearer " (create-token {:sub "user1@example.com"} "bad-password-123")))

(defn read-books [response]
  (map Book/create (cheshire.core/parse-string (:body response) true)))

(defn read-book [response]
  (Book/create (cheshire.core/parse-string (:body response) true)))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello World!"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

(deftest enable-test-login
  (with-redefs [environ.core/env (fn [_] "true")]
    (testing "when env variable is true, should return status 200"
      (is (= (:status (app (mock/request :get "/test-login"))) 200)))))

(deftest form-test-login
  (with-redefs [environ.core/env (fn [_] "(= 1 1)")]
    (testing "when env variable is truthy form (= 1 1), should return status 404"
      (is (= (:status (app (mock/request :get "/test-login"))) 404)))))

(deftest disable-test-login
  (with-redefs [environ.core/env (fn [_] "false")]
    (testing "when env variable is false, should return status 404"
      (is (= (:status (app (mock/request :get "/test-login"))) 404)))))

(deftest nil-test-login
  (with-redefs [environ.core/env (fn [_] nil)]
    (testing "when env variable is nil, should return status 404"
      (is (= (:status (app (mock/request :get "/test-login"))) 404)))))

(deftest nonsense-test-login
  (with-redefs [environ.core/env (fn [_] "ThisIsBadEnvVar")]
    (testing "when env variable is not true or false, should return status 404"
      (is (= (:status (app (mock/request :get "/test-login"))) 404)))))

(deftest create-book
  (testing "should not create book without authentication"
    (let [response (app (->
                          (mock/request :post "/books")
                          (mock/json-body {:name "Bad book!"})))]
      (is (= (:status response) 401))))
  (testing "should create a book"
    (let [response (app (->
                          (mock/request :post "/books")
                          (mock/header :authorization test-user-token)
                          (mock/json-body {:name "My best book"})))]
      (is (= (:name (read-book response)) "My best book"))
      (is (= (:user (read-book response)) "user1@example.com"))
      (is (= (:status response) 201)))))

(deftest get-books
  (testing "should not get books without authentication"
    (is (= (:status (app (mock/request :get "/books"))) 401)))
  (testing "should not get books with bad token"
    (is (= (:status (app (->
                           (mock/request :get "/books")
                           (mock/header :authorization bad-user-token)))) 401)))
  (testing "should get list of books"
    (let [response (app (->
                          (mock/request :get "/books")
                          (mock/header :authorization test-user-token)))]
      (is (sequential? (read-books response)))))
  (testing "should get only own books"
    (service/create-book {:name "Your book1" :user "user2@example.com"})
    (service/create-book {:name "Your book2" :user "user2@example.com"})
    (service/create-book {:name "My book1" :user "user1@example.com"})
    (service/create-book {:name "My book2" :user "user1@example.com"})
    (service/create-book {:name "No-ones book"})
    (service/create-book {:name "Your book3" :user "user2@example.com"})
    (let [response (app (->
                          (mock/request :get "/books")
                          (mock/header :authorization test-user-token)))]
      (is (= (count (map :user (read-books response))) 2))
      (is (every? #(= % "user1@example.com") (map :user (read-books response)))))))
