(ns book-library.database
  (:require [monger.core :as mg]))

(def default-uri "mongodb://root:secret@localhost:27017/admin")

(defn open-db-connection
  ([] (open-db-connection default-uri))
  ([db-uri] (:conn (mg/connect-via-uri (or db-uri default-uri)))))

(defn close-db-connection [conn]
  (mg/disconnect conn))
