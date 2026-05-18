(ns antman.ui.components
  (:require [hyper.core :as h]))

(defn- fmt-num [n]
  (format "%.2f" (double n)))

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
    (for [{:keys [id broker asset entry price pl tp sl]} positions]
      [:tr {:key id}
       [:td broker]
       [:td asset]
       [:td (fmt-num entry)]
       [:td (fmt-num price)]
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

(defn nav
  []
  [:nav.app-nav
   [:a (h/navigate :trading) "Trading"]
   " · "
   [:a (h/navigate :layout) "Layout"]])
