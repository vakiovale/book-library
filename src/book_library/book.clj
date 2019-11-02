(ns book-library.book)
  (defprotocol Book
    "Abstract book"
    (get-name [this]))
