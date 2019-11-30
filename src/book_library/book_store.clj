(ns book-library.book-store
  (:require [book-library.database :as db-con]
            [book-library.book :refer [create]]
            [environ.core :refer [env]]
            [clojure.set :refer [rename-keys]]
            [monger.core :as mg]
            [monger.collection :as mc])
  (:import (java.util UUID)))

(def connection (db-con/open-db-connection (env :mongodb-uri)))
(def db (mg/get-db connection (or (env :book-library-db) "book-library")))
(def coll "books")

(defn close []
  (db-con/close-db-connection connection))

(defn clear []
  (mc/drop db coll))

(defn deserialize [to-deserialize]
  (if (sequential? to-deserialize)
    (map deserialize to-deserialize)
    (rename-keys to-deserialize {:_id :id})))

(defn serialize [to-serialize]
  (rename-keys to-serialize {:id :_id}))

(defn get-books []
  (map create (deserialize (mc/find-maps db coll))))

(defn add-book [book]
  (create (deserialize
            (mc/insert-and-return
              db
              coll
              (serialize
                (create
                  (merge book {:id (UUID/randomUUID)})))))))

(defn remove-book [id]
  (mc/remove-by-id db coll id))

(defn get-book [id]
  (let [object (mc/find-map-by-id db coll id)]
    (if (nil? object)
      nil
      (create (deserialize object)))))
