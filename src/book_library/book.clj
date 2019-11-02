(ns book-library.book)
  (defprotocol Book
    "Book should have an ID and a name"
    (get-id [this])
    (get-name [this]))
