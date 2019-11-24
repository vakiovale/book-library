(ns book-library.book)

(defprotocol Book
  "Book should have an ID and a name"
  (get-id [this])
  (get-name [this]))

(defrecord ABook [id name]
  Book
  (get-id [this] id)
  (get-name [this] name))

(defn create [book]
  (ABook. (:id book) (:name book)))
