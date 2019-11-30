(ns book-library.security.jwt
  (:require [buddy.sign.jwt :as jwt]
            [environ.core :refer [env]]))

(def env-secret (env :jwt-secret))

(defn create-token
  ([data secret] (jwt/sign data secret))
  ([data] (create-token data env-secret)))

(defn read-token
  ([data secret] (jwt/unsign data secret))
  ([data] (read-token data env-secret)))
