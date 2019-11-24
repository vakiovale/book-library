(ns book-library.database
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

(defn open-db-connection [& db-uri]
  (let [uri (or db-uri "mongodb://root:secret@localhost:27017/admin")
        {:keys [conn db]} (mg/connect-via-uri uri)]
    {:conn conn :db db}))
