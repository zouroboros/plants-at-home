(ns plants-at-home.ui
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def app-state (atom {:plants nil}))

(defn plant-list [plants]
      (om/component (dom/div nil (dom/h1 nil "Plant list")
        (apply dom/ul nil (map (fn [plant] (dom/li nil plant)) plants)))))


(defn wait-view []
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "Loading data"))))

(defn show []
  (om/root
    (fn [data owner]
      (reify om/IRender
        (render [_] (if (nil? (:plants data)) (om/build wait-view nil) (om/build plant-list (:plants data))))))
    app-state
    {:target (. js/document (getElementById "app"))}))
