(ns antman.ui.components
  (:require
   [hyper.context :as ctx]
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

(defn sse-connection-status
  "Banner when SSE timestamps stop updating for >2s.
   Stale detection + auto-reconnect live in /js/sse-reconnect.js."
  []
  (let [tab-id (:hyper/tab-id ctx/*request*)]
    [:motion.div#sse-connection-status.sse-connection-status
     (when tab-id {:data-tab-id tab-id})
     (h/reactive [heartbeat/server-ts*]
       [:motion.span#sse-server-ts
        {:data-server-ts @heartbeat/server-ts*
         :hidden true}])
     [:motion.div.sse-interrupted "server connection interrupted"]]))

(defn nav
  []
  [:nav.app-nav
   [:a (h/navigate :trading) "Trading"]
   " · "
   [:a (h/navigate :layout) "Layout"]
   " · "
   [:a (h/navigate :highcharts-random) "Highcharts random"]])
