(ns book-library.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]]
            [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes app
  (GET "/" [] "<h1>Hello World!</h1>")
  (route/not-found "<h1>Not Found</h1>"))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (run-jetty app {:port port})))
