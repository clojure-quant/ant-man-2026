(ns antman.config
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [token.identity.local :as local-identity]))

(defn- read-edn-file [path]
  (let [f (io/file path)]
    (when (.exists f)
      (edn/read-string (slurp f)))))

(defn- deep-merge [a b]
  (if (and (map? a) (map? b))
    (merge-with deep-merge a b)
    (or b a)))

(defn prepare-users
  "Hash plain-text passwords for token local login."
  [users]
  (into {}
        (map (fn [[id user]]
               [id (update user :password local-identity/pwd-hash)])
             users)))

(defn- read-edn-resource [path]
  (when-let [res (io/resource path)]
    (edn/read-string (slurp res))))

(defn- read-secrets []
  (or (read-edn-resource "secrets.edn")
      (read-edn-file "secrets.edn")
      (read-edn-resource "secrets-empty.edn")
      (read-edn-file "secrets-empty.edn")
      {}))

(defn load-config!
  "Load config.edn from resources, then merge secrets.edn.

  secrets.edn is looked up on the classpath (resources/) first, then in the
  project root. Falls back to secrets-empty.edn in the same locations."
  []
  (let [base (or (read-edn-resource "config.edn")
                 (read-edn-file "config.edn")
                 {})
        secrets (read-secrets)
        cfg (deep-merge base secrets)]
    (when-not (map? (:users cfg))
      (throw (ex-info "Config :users must be a map — add resources/secrets.edn or secrets.edn"
                      {:users (:users cfg)})))
    (update cfg :users prepare-users)))
