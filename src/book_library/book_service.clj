(ns book-library.book-service
  (:require [book-library.book :refer :all]))

(defrecord ABook [name]
  Book
  (get-name [this] name))

(defn create-book
  "Creates a book"
  [book]
  (ABook. (:name book)))
