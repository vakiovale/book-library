(ns book-library.database-test
  (:require [clojure.test :refer :all]
            [monger.core :as mg]
            [monger.collection :as mc]
            [book-library.database :refer :all]))

(deftest database-connection
  (testing "should fail with bad uri"
    (is (thrown? Exception (open-db-connection "bad-uri"))))

  (testing "should connect with default uri, insert test value and drop collection"
    (let [{:keys [conn db]} (open-db-connection)]
      (is (not (nil? conn)))
      (is (not (nil? db)))
      (let [test-db (mg/get-db conn "test-drop")]
        (mc/insert test-db "test-coll" {:value "you just inserted me"})
        (is (not (empty? (mc/find-maps test-db "test-coll"))))
        (is (= (->
                 (mc/find-maps test-db "test-coll")
                 first
                 :value)
               "you just inserted me"))
        (mc/drop test-db "test-coll")
        (is (empty? (mc/find-maps test-db "test-coll")))
        (mg/drop-db conn "test-drop")
        (mg/disconnect conn)))))
