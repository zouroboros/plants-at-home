(ns plants-at-home.ui
  (:require [cljs.core.async :refer [put! chan <!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def app-state (atom {:plants nil}))

(defn plant-list-item [channel plant]
  (om/component (dom/li #js {:onClick (fn [e] (put! channel plant))} plant)))

(defn plant-list [channel plants]
      (om/component (dom/div nil (dom/h1 nil "Plant list")
        (apply dom/ul nil (om/build-all (partial plant-list-item channel) plants)))))

(defn wait-view []
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "Loading data"))))

(defn show [channel]
  (om/root
    (fn [data owner]
      (reify om/IRender
        (render [_] (if (nil? (:plants data)) (om/build wait-view nil) (om/build (partial plant-list channel) (:plants data))))))
    app-state
    {:target (. js/document (getElementById "app"))}))
