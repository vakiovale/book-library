(ns book-library.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [book-library.core :refer :all]
            [cheshire.core :refer [generate-string]]))

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
                          (mock/json-body (generate-string {:name "My best book"}))))]
      (prn response)
      (is (= (:name (:body response)) "My best book"))
      (is (= (:status response) 201)))))
