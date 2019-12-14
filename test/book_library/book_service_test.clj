(ns book-library.book-service-test
  (:require [clojure.test :refer :all]
            [book-library.book :as Book]
            [book-library.book-store :as store]
            [book-library.book-service :as service])
  (:import (java.util UUID)))

(defn setup [test-fun]
  (store/clear)
  (test-fun))

(use-fixtures :each setup)

(deftest create-a-book
  (let [book (service/create-book {:name "The Greatest Book" :user "user1@example.com"})]
    (testing "book should satisfy Book protocol"
      (is (satisfies? Book/Book book)))
    (testing "book should have a name"
      (is (= (Book/get-name book) "The Greatest Book")))
    (testing "book should have a user"
      (is (= (Book/get-user book) "user1@example.com")))
    (testing "book's ID should be an UUID"
      (is (uuid? (Book/get-id book))))))

(deftest get-books
  (testing "should get empty list of books if no books exist"
    (is (empty? (service/get-books "user1@example.com"))))
  (testing "should get list of books"
    (service/create-book {:name "Nice Book" :user "user1@example.com"})
    (service/create-book {:name "Bad Book" :user "user1@example.com"})
    (let [books (service/get-books "user1@example.com")]
      (is (= (count books) 2)))))

(deftest removing-books
  (testing "should remove book if ID exists"
    (let [book (service/create-book {:name "Remove me!" :user "user1@example.com"})]
      (is (= (count (service/get-books "user1@example.com")) 1))
      (service/remove-book (:id book))
      (is (= (count (service/get-books "user1@example.com")) 0))))
  (testing "should not remove anything if ID does not exist"
    (service/create-book {:name "Don't remove me" :user "user1@example.com"})
    (service/create-book {:name "It would be nice not to remove me" :user "user1@example.com"})
    (service/remove-book (UUID/randomUUID))
    (let [book-names (map Book/get-name (service/get-books "user1@example.com"))]
      (is (= (count book-names) 2))
      (is (.contains book-names "Don't remove me"))
      (is (.contains book-names "It would be nice not to remove me")))))

(deftest get-book-by-id
  (testing "should get book by id"
    (let [id (Book/get-id (service/create-book {:name "Find me, please!" :user "user1@example.com"}))]
      (is (= (Book/get-id (service/get-book id)) id))))
  (testing "should get nil with non existing ID"
    (is (nil? (service/get-book (UUID/randomUUID))))))

(deftest own-books
  (testing "should get only books for specific user"
    (service/create-book {:name "My book 1" :user "user1@example.com"})
    (service/create-book {:name "Your book 1" :user "user2@example.com"})
    (service/create-book {:name "My book 2" :user "user1@example.com"})
    (is (= (count (service/get-books "user1@example.com")) 2))
    (is (every? #(= % "user1@example.com") (map Book/get-user (service/get-books "user1@example.com"))))))
