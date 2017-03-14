(ns plants-at-home.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <!]]
            [plants-at-home.data :as data]
            [plants-at-home.ui]))

(enable-console-print!)

(plants-at-home.ui.show)

(let [data-channel (chan)] (data/loadData data-channel)
  (go (let [db (<! data-channel)] (swap! plants-at-home.ui.app-state assoc
    :plants (data/plants db)))))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
