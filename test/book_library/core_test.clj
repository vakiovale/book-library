(ns book-library.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [book-library.core :refer :all]
            [cheshire.core :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello World!"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

(deftest create-book
  (testing "should create a book"
    (let [response (app (->
                          (mock/request :post "/books")
                          (mock/json-body {:name "My best book"})))]
      (is (= (:name (cheshire.core/parse-string (:body response) true)) "My best book"))
      (is (= (:status response) 201)))))

(deftest get-books
  (testing "should get list of books"
    (let [response (app (mock/request :get "/books"))]
      (is (= (vector? (cheshire.core/parse-string (:body response))))))))
