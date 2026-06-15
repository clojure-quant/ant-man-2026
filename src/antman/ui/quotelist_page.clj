(ns antman.ui.quotelist-page
  (:require
   [hyper.core :as h]
   [antman.demo.quotelist :as quotelist]
   [antman.ui.components :as ui]))

(defn quotelist-page
  [_req]
  ;(println "QUOTELIST-PAGE")
  (h/watch! quotelist/quotelist)
  [:motion.div.quotelist-page
   (ui/nav)
   [:h1 "Quote list"]
   (h/reactive [quotelist/quotelist]
     (ui/quotelist-table @quotelist/quotelist))])
