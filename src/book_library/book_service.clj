(ns book-library.book-service
  (:require [book-library.book :refer :all])
  (:import (java.util UUID)))

(defrecord ABook [id name]
  Book
  (get-id [this] id)
  (get-name [this] name))

(defn create-book
  "Creates a book"
  [book]
  (ABook. (UUID/randomUUID) (:name book)))

(defn get-books
  "Get list of books"
  []
  (vector))
