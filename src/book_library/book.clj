(ns book-library.book)

(defprotocol Book
  "Book should have an ID and a name"
  (get-id [this])
  (get-user [this])
  (get-name [this]))

(defrecord ABook [id user name]
  Book
  (get-id [this] id)
  (get-user [this] user)
  (get-name [this] name))

(defn create [book]
  (ABook. (:id book) (:user book) (:name book)))
