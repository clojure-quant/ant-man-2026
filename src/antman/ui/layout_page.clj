(ns antman.ui.layout-page
  (:require
   [hyper.core :as h]
   [antman.sim.state :as sim]
   [antman.ui.components :as ui]))

(defn layout-page
  [_req]
  [:motion.div.layout-page
   (ui/nav)
   (ui/sse-connection-status)
   [:motion.div#golden-layout-host.golden-layout-host
    {:data-on-load "antmanInitLayout()"}
    [:motion.div#gl-mount]]
   (h/reactive [sim/positions*]
     [:motion.div#panel-positions.panel-root
      (ui/positions-table @sim/positions*)])
   (h/reactive [sim/trades*]
     [:motion.div#panel-trades.panel-root
      (ui/trades-table @sim/trades*)])])
