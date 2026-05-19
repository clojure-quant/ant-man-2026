(ns antman.system
  (:require
   [hyper.core :as h]
   [antman.routes :refer [routes]]
   [antman.sim.state :as sim]
   [antman.sse.heartbeat :as heartbeat]))

(defn head-tags
  [_req]
  ;; Scripts/CSS for layout and highcharts-random live in the base head so
  ;; Hyper client-side navigation between pages still has them loaded.
  [[:link {:rel "stylesheet" :href "/css/app.css"}]
   [:script {:src "/js/sse-reconnect.js" :defer true}]
   [:link {:rel "stylesheet"
           :href "https://cdn.jsdelivr.net/npm/golden-layout@2.6.0/dist/css/goldenlayout-base.css"}]
   [:link {:rel "stylesheet"
           :href "https://cdn.jsdelivr.net/npm/golden-layout@2.6.0/dist/css/themes/goldenlayout-dark-theme.css"}]
   [:script {:type "module" :src "/js/golden-layout.js"}]
   [:script {:type "module" :src "/js/highcharts-random.js?v=2"}]])

(defn create-handler
  []
  (h/create-handler
    #'routes
    :static-resources "public"
    :head #'head-tags
    :watches [#'sim/positions* #'sim/trades* #'heartbeat/server-ts*]))

(defonce server* (atom nil))

(defn start!
  [{:keys [port] :or {port 3000}}]
  (sim/start!)
  (heartbeat/start!)
  (when-not @server*
    (reset! server* (h/start! (create-handler) {:port port})))
  @server*)

(defn stop!
  []
  (sim/stop!)
  (heartbeat/stop!)
  (when-let [app @server*]
    (h/stop! app)
    (reset! server* nil)))
