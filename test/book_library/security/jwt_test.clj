(ns book-library.security.jwt-test
  (:require [clojure.test :refer :all]
            [book-library.security.jwt :refer :all]))

(def secret "secret")

(deftest creating-token
  (testing "should return signed JWT token"
    (let [token (create-token {:sub "user@example.com"} secret)]
      (prn token)
      (is (= token "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.Y6K9I0487rb6k2_Pnef-_tdoIdJVd7xPTJxvHFRcPZ8")))))

(deftest reading-token
  (testing "should return map with correct data"
    (is (=
          (read-token
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.Y6K9I0487rb6k2_Pnef-_tdoIdJVd7xPTJxvHFRcPZ8"
            secret)
          {:sub "user@example.com"})))
  (testing "with bad secret should raise exception"
    (is (thrown-with-msg? Exception #"Message seems corrupt or manipulated."
                          (read-token
                            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.Y6K9I0487rb6k2_Pnef-_tdoIdJVd7xPTJxvHFRcPZ8"
                            "bad-secret"))))
  (testing "with manipulated data should raise exception"
    (is (thrown-with-msg? Exception #"Message seems corrupt or manipulated."
                          (read-token
                            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiYWQtdXNlckBleGFtcGxlLmNvbSJ9.Y6K9I0487rb6k2_Pnef-_tdoIdJVd7xPTJxvHFRcPZ8"
                            secret)))))
