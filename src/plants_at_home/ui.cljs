(ns plants-at-home.ui
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def app-state (atom {:plants nil :current-plant nil}))

(defn plant-list-item [current-plant plant]
  (let [class (if (= current-plant plant) "current" nil)]
    (om/component (dom/li nil (dom/a #js {:href (str "#" plant) :className class} plant)))))

(defn plant-list [current-plant plants]
      (om/component (dom/div nil (dom/h1 nil "Plants")
        (apply dom/ul #js {:className "plant-list"} (om/build-all (partial plant-list-item current-plant) plants)))))

(defn wait-view []
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "Loading data"))))

(defn read-url-hash [channel] (let [plant (js/decodeURIComponent (subs js/document.location.hash 1))] (put! channel plant)))

(defn init-ui [ui-channel]
  (om/root
    (fn [data owner]
      (reify om/IRender
        (render [_] (if (nil? (:plants data)) (om/build wait-view nil) (om/build (partial plant-list (:current-plant data)) (:plants data))))))
    app-state
    {:target (. js/document (getElementById "app"))})
  (. js/window (addEventListener "hashchange" (fn [e] (read-url-hash ui-channel))))
  (if (nil? js.document.location.hash) () (read-url-hash ui-channel)))
