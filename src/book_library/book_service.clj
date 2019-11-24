(ns book-library.book-service
  (:require [book-library.book :refer :all]
            [book-library.book-store :as store]))

(defn create-book
  "Creates a book"
  [book]
  (store/add-book book))

(defn get-books
  "Get list of books"
  []
  (store/get-books))

(defn remove-book
  "Removes a book"
  [id]
  (store/remove-book id))

(defn get-book
  "Get book"
  [id]
  (store/get-book id))
