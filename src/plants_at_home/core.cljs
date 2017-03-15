(ns plants-at-home.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <!]]
            [plants-at-home.data :as data]
            [plants-at-home.ui :as ui]
            [plants-at-home.map :as map]))

(enable-console-print!)

(let [data-channel (chan)
      ui-channel (chan)
      map-channel (chan)]
  (map/init-map map-channel)
  (ui/show ui-channel)
  (data/loadData data-channel)
  (go (let [db (<! data-channel)] (swap! plants-at-home.ui.app-state assoc
    :plants (data/plants db))
  (go (while true (let [plant (<! ui-channel)] (put! map-channel (data/countries db plant))))))))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
