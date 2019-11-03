(ns book-library.book-service
  (:require [book-library.book :refer :all])
  (:import (java.util UUID)))

(def book-store
  (atom {:books []}))

(defn add-book-to-store [book store]
  (swap! store update-in [:books] conj book)
  book)

(defn get-books-from-store [store]
  (:books @store))

(defn remove-book-from-store [id store]
  (swap! store update-in [:books] (fn [books] (remove #(= (:id %) id) books))))

(defn get-book-from-store [id store]
  (first (filter #(= (get-id %) id) (:books @store))))

(defrecord ABook [id name]
  Book
  (get-id [this] id)
  (get-name [this] name))

(defn create-book
  "Creates a book"
  [book]
  (let [book (ABook. (UUID/randomUUID) (:name book))]
    (add-book-to-store book book-store)))

(defn get-books
  "Get list of books"
  []
  (get-books-from-store book-store))

(defn remove-book
  "Removes a book"
  [id]
  (remove-book-from-store id book-store))

(defn get-book
  "Get book"
  [id]
  (get-book-from-store id book-store))
