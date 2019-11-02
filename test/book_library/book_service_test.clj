(ns book-library.book-service-test
  (:require [clojure.test :refer :all]
            [book-library.book :as Book]
            [book-library.book-service :as service]))

(deftest test-book-service
  (testing "Book service"
    (testing "should create a book"
      (let [book (service/create-book {:name "The Greatest Book"})]
        (is (= (Book/get-name book) "The Greatest Book"))
        (is (satisfies? Book/Book book))))))
