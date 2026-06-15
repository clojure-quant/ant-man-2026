(ns antman.demo.quotelist
  (:require
   [missionary.core :as m]
   [quanta.quote.account-manager :refer [create-account-manager add-edn-accounts quote-list-dict-flow]]
   [quanta.blotter.logger :refer [create-logger log start-log-flow-to-logger]]))




(def am
  (let [l (create-logger "log/quotes.txt" false)
        log-fn (partial log l)

        am (create-account-manager log-fn)
        _ (add-edn-accounts am "demo-quote-accounts.edn")] am))


(def quotelist (atom {}))

(defn quote-printer [f]
  (m/reduce
   (fn [s v]
     ;(println "QUOTELIST: " v)
     (reset! quotelist v)
     nil)
   nil
   f))


(def dispose!
  (let [ql (quote-list-dict-flow am (fn [_asset] 1)
                                 ["EURUSD" "USDJPY" "EURNOK"])
        qp (quote-printer ql)
        ]
    (qp #(println "1-quote-printer done " %) #(println "1-quote-printer CRASH " %))
    ))


(comment 
  @quotelist
  am
  (dispose!)
 ; 
  )
