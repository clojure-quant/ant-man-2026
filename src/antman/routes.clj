(ns antman.routes
  (:require
   [antman.ui.trading :refer [trading-page]]
   [antman.ui.layout-page :refer [layout-page]]
   [antman.ui.highcharts-random-page :refer [highcharts-random-page]]
   [antman.ui.panels :refer [positions-panel trades-panel]]))

(def routes
  [["/" {:name :home
         :title "Ant Man"
         :get (fn [_] {:status 302 :headers {"Location" "/trading"} :body ""})}]
   ["/trading"
    {:name :trading
     :title "Trading"
     :get #'trading-page}]
   ["/layout"
    {:name :layout
     :title "Layout"
     :get #'layout-page}]
   ["/highcharts-random"
    {:name :highcharts-random
     :title "Highcharts random"
     :get #'highcharts-random-page}]
   ["/panels/positions"
    {:name :panel-positions
     :title "Positions"
     :get #'positions-panel}]
   ["/panels/trades"
    {:name :panel-trades
     :title "Trades"
     :get #'trades-panel}]])
