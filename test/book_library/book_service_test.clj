(ns book-library.book-service-test
  (:require [clojure.test :refer :all]
            [book-library.book-service :refer :all]))

(deftest test-book-service
  (testing "Book service"
    (testing "should create a book"
      (let [book (create-book {:id "id-1" :name "The Greatest Book"})]
        (is (= (:name book)
               "The Greatest Book"))))))
