(ns antman.system
  (:require
   [hyper.core :as h]
   [antman.routes :refer [routes]]
   [antman.sim.state :as sim]))

(defn- layout-head
  []
  [[:link {:rel "stylesheet"
           :href "https://cdn.jsdelivr.net/npm/golden-layout@2.6.0/dist/css/goldenlayout-base.css"}]
   [:link {:rel "stylesheet"
           :href "https://cdn.jsdelivr.net/npm/golden-layout@2.6.0/dist/css/themes/goldenlayout-dark-theme.css"}]
   [:script {:type "module" :src "/js/golden-layout.js"}]])

(defn head-tags
  [req]
  (let [base [[:link {:rel "stylesheet" :href "/css/app.css"}]]]
    (if (= :layout (get-in req [:hyper/route :name]))
      (into base (layout-head))
      base)))

(defn create-handler
  []
  (h/create-handler
    #'routes
    :static-resources "public"
    :head #'head-tags
    :watches [#'sim/positions* #'sim/trades*]))

(defonce server* (atom nil))

(defn start!
  [{:keys [port] :or {port 3000}}]
  (sim/start!)
  (when-not @server*
    (reset! server* (h/start! (create-handler) {:port port})))
  @server*)

(defn stop!
  []
  (sim/stop!)
  (when-let [app @server*]
    (h/stop! app)
    (reset! server* nil)))
