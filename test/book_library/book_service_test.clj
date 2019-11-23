(ns book-library.book-service-test
  (:require [clojure.test :refer :all]
            [book-library.book :as Book]
            [book-library.book-service :as service])
  (:import (java.util UUID)))

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

(deftest removing-books
  (testing "should remove book if ID exists"
    (let [book (service/create-book {:name "Remove me!"})]
      (is (= (count (service/get-books)) 1))
      (service/remove-book (:id book))
      (is (= (count (service/get-books))))))
  (testing "should not remove anything if ID does not exist"
    (service/create-book {:name "Don't remove me"})
    (service/create-book {:name "It would be nice not to remove me"})
    (service/remove-book (UUID/randomUUID))
    (let [book-names (map Book/get-name (service/get-books))]
      (is (= (count book-names) 2))
      (is (.contains book-names "Don't remove me"))
      (is (.contains book-names "It would be nice not to remove me")))))

(deftest get-book-by-id
  (testing "should get book by id"
    (let [id (Book/get-id (service/create-book {:name "Find me, please!"}))]
      (is (= (Book/get-id (service/get-book id)) id))))
  (testing "should get nil with non existing ID"
    (is (nil? (service/get-book (UUID/randomUUID))))))

