(ns plants-at-home.data
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljsjs.papaparse]
    [ajax.core :refer [GET]]
    [cljs.core.async :refer [put! chan <!]]))

(defn parseCsv [csv] (js/Papa.parse csv (js-obj "header" true)))


(defn initDb [csvObj] (filter (fn [row] (> row.Value 0)) csvObj.data))

(defn handleError [e] ())

(defn loadData [out]
  (GET "FAOSTAT_data_3-16-2017.csv" {
      :handler (fn [r] (put! out (initDb (parseCsv r))))
      :error-handler (fn [e] (put! out e)) }))

(defn plants [db] (filter some? (apply sorted-set (map (fn [row] row.Item) db))))

(defn countries [db plant] (set (map (fn [row] row.Area)
  (filter (fn [row] (= row.Item plant)) db))))
