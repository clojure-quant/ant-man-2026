(ns antman.core
  (:require
   [antman.system :as system])
  (:gen-class))

(defn -main
  [& _args]
  (println "Starting ant-man on http://localhost:3000")
  (println "  /login   — sign in (local or OAuth)")
  (println "  /trading — positions and trades tables")
  (println "  /layout  — Golden Layout workspace")
  (println "  /quotelist — live quote dictionary")
  (println "  /simulator — edit and submit trade signal")
  (system/start! {:port 3000}))  ; merges with config from resources/secrets.edn
