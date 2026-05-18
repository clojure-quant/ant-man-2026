(ns antman.ui.components
  (:require
   [hyper.core :as h]
   [antman.sse.heartbeat :as heartbeat]))

(defn- fmt-num [n]
  (format "%.2f" (double n)))

(defn- price-flash-class
  [flash]
  (case flash
    :up "price-flash-up"
    :down "price-flash-down"
    nil))

(defn positions-table
  [positions]
  [:table.positions-table
   [:thead
    [:tr
     [:th "Broker"]
     [:th "Asset"]
     [:th "Entry"]
     [:th "Price"]
     [:th "P/L"]
     [:th "TP"]
     [:th "SL"]]]
   [:tbody
    (for [{:keys [id broker asset entry price pl tp sl price-flash]} positions]
      [:tr {:key id}
       [:td broker]
       [:td asset]
       [:td (fmt-num entry)]
       [:td.price {:class (price-flash-class price-flash)} (fmt-num price)]
       [:td {:class (if (neg? pl) "loss" "profit")} (fmt-num pl)]
       [:td (fmt-num tp)]
       [:td (fmt-num sl)]])]])

(defn trades-table
  [trades]
  [:motion.div.trades-table-wrap
   [:table.trades-table
    [:thead
     [:tr
      [:th "Time"]
      [:th "Broker"]
      [:th "Asset"]
      [:th "Side"]
      [:th "Qty"]
      [:th "Price"]]]
    [:tbody
     (for [{:keys [id time broker asset side qty price]} (seq (rseq trades))]
       [:tr {:key id}
        [:td.time time]
        [:td broker]
        [:td asset]
        [:td.side (name side)]
        [:td qty]
        [:td (fmt-num price)]])]]])

(defn- stale-check-expr
  [stale?* ready?*]
  (str @ready?* " = Number(document.getElementById('sse-connection-status')"
       "?.getAttribute('data-server-ts') || 0) > 0; "
       @stale?* " = " @ready?* " && (Date.now() - Number(document.getElementById('sse-connection-status')"
       "?.getAttribute('data-server-ts') || 0)) > 2000"))

(defn sse-connection-status
  "Reactive banner: shows when SSE timestamps stop updating for >2s."
  []
  (let [stale?* (h/local-signal :sse-stale false)
        ready?* (h/local-signal :sse-ready false)]
    (h/reactive [heartbeat/server-ts*]
      [:motion.div#sse-connection-status.sse-connection-status
       {:data-server-ts @heartbeat/server-ts*
        :data-on-interval__duration.1s (stale-check-expr stale?* ready?*)}
       [:motion.div.sse-interrupted {:data-show @stale?*}
        "server connection interrupted"]])))

(defn nav
  []
  [:nav.app-nav
   [:a (h/navigate :trading) "Trading"]
   " · "
   [:a (h/navigate :layout) "Layout"]])
