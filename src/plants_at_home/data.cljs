(ns plants-at-home.data
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljsjs.papaparse]
    [ajax.core :refer [GET]]
    [cljs.core.async :refer [put! chan <!]]))

(defn parseCsv [csv] (js/Papa.parse csv (js-obj "header" true)))

(defn handleError [e] ())

(defn loadData [out]
  (GET "FAOSTAT_data_3-16-2017.csv" {
      :handler (fn [r] (put! out (parseCsv r)))
      :error-handler (fn [e] (put! out e)) }))

(defn plants [db] (filter some? (set (map (fn [row] row.Item) db.data))))

(defn countries [db plant] (set (map (fn [row] row.Area)
  (filter (fn [row] (= row.Item plant)) db.data))))
