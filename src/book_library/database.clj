(ns book-library.database
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

(defn open-db-connection [& db-uri]
  (:conn
    (mg/connect-via-uri
      (or
        db-uri
        "mongodb://root:secret@localhost:27017/admin"))))

(defn close-db-connection [conn]
  (mg/disconnect conn))
