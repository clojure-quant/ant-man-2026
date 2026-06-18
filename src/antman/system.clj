(ns antman.system
  (:require
   [antman.auth :as auth]
   [antman.config :as config]
   [antman.routes :as routes]
   [antman.sim.state :as sim]
   [antman.sse.heartbeat :as heartbeat]
   [hyper.core :as h]))

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
   [:script {:type "module" :src "/js/golden-layout.js?v=4"}]
   [:script {:type "module" :src "/js/highcharts-random.js?v=4"}]])

(defn create-handler
  []
  (h/create-handler
   #'routes/all-routes
   :static-resources "public"
   :head #'head-tags
   :middleware [auth/wrap-hydrate-identity]
   :watches [#'sim/positions* #'sim/trades* #'sim/notifications* #'heartbeat/server-ts*]))

(defonce server* (atom nil))
(defonce config* (atom nil))

(defn start!
  ([]
   (start! (config/load-config!)))
  ([cfg]
   (let [cfg (merge (config/load-config!) cfg)]
     (reset! config* cfg)
     (auth/start-token! cfg)
     (routes/rebuild! @auth/token*)
     (sim/start!)
     (heartbeat/start!)
     (when-not @server*
       (reset! server* (h/start! (create-handler) {:port (or (:port cfg) 3000)})))
     @server*)))

(defn stop!
  []
  (sim/stop!)
  (heartbeat/stop!)
  (when-let [app @server*]
    (h/stop! app)
    (reset! server* nil)))
