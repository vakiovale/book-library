(ns book-library.book-service
  (:require [book-library.book :refer :all])
  (:import (java.util UUID)))

(def book-store
  (atom {:books []}))

(defrecord ABook [id name]
  Book
  (get-id [this] id)
  (get-name [this] name))

(defn create-book
  "Creates a book"
  [book]
  (let [book (ABook. (UUID/randomUUID) (:name book))]
    book))

(defn get-books
  "Get list of books"
  []
  (:books book-store))

