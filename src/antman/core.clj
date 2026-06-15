(ns antman.core
  (:require
   [antman.system :as system])
  (:gen-class))

(defn -main
  [& _args]
  (println "Starting ant-man on http://localhost:3000")
  (println "  /trading — positions and trades tables")
  (println "  /layout  — Golden Layout workspace")
  (println "  /quotelist — live quote dictionary")
  (system/start! {:port 3000}))
