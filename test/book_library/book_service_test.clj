(ns book-library.book-service-test
  (:require [clojure.test :refer :all]
            [book-library.book :as Book]
            [book-library.book-service :as service]))

(defn setup [test]
  (reset! service/book-store {:books []})
  (test))

(use-fixtures :each setup)

(deftest create-a-book
  (let [book (service/create-book {:name "The Greatest Book"})]
    (testing "book should satisfy Book protocol"
      (is (satisfies? Book/Book book)))
    (testing "book should have a name"
      (is (= (Book/get-name book) "The Greatest Book")))
    (testing "book's ID should be an UUID"
      (is (uuid? (Book/get-id book))))))

(deftest get-books
  (testing "should get empty list of books if no books exist"
    (is (= (empty? (service/get-books)))))
  (testing "should get list of books"
    (service/create-book {:name "Nice Book"})
    (service/create-book {:name "Bad Book"})
    (let [books (service/get-books)]
      (is (= (count books) 2)))))
