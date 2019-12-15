(ns book-library.book-service-test
  (:require [clojure.test :refer :all]
            [book-library.book :as Book]
            [book-library.book-store :as store]
            [book-library.book-service :as service])
  (:import (java.util UUID)))

(def user-1 {:user "user1@example.com"})
(def user-2 {:user "user2@example.com"})

(defn setup [test-fun]
  (store/clear)
  (test-fun))

(use-fixtures :each setup)

(deftest create-a-book
  (let [book (service/create-book (merge {:name "The Greatest Book"} user-1))]
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
    (service/create-book (merge {:name "Nice Book"} user-1))
    (service/create-book (merge {:name "Bad Book"} user-1))
    (let [books (service/get-books (:user user-1))]
      (is (= (count books) 2)))))

(deftest removing-books
  (testing "should remove book if ID exists"
    (let [book (service/create-book (merge {:name "Remove me!"} user-1))]
      (is (= (count (service/get-books (:user user-1))) 1))
      (service/remove-book (:id book))
      (is (= (count (service/get-books (:user user-1))) 0))))
  (testing "should not remove anything if ID does not exist"
    (service/create-book (merge {:name "Don't remove me"} user-1))
    (service/create-book (merge {:name "It would be nice not to remove me"} user-1))
    (service/remove-book (UUID/randomUUID))
    (let [book-names (map Book/get-name (service/get-books (:user user-1)))]
      (is (= (count book-names) 2))
      (is (.contains book-names "Don't remove me"))
      (is (.contains book-names "It would be nice not to remove me")))))

(deftest get-book-by-id
  (testing "should get book by id"
    (let [id (Book/get-id (service/create-book (merge {:name "Find me, please!"} user-1)))]
      (is (= (Book/get-id (service/get-book id)) id))))
  (testing "should get nil with non existing ID"
    (is (nil? (service/get-book (UUID/randomUUID))))))

(deftest own-books
  (testing "should get only books for specific user"
    (service/create-book (merge {:name "My book 1"} user-1))
    (service/create-book (merge {:name "Your book 1"} user-2))
    (service/create-book (merge {:name "My book 2"} user-1))
    (is (= (count (service/get-books "user1@example.com")) 2))
    (is (every? #(= % (:user user-1)) (map Book/get-user (service/get-books (:user user-1)))))))
