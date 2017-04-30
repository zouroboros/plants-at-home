(ns plants-at-home.ui
  (:require [cljs.core.async :refer [put! chan <!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def app-state (atom {:plants nil}))

(defn plant-list-item [channel plant]
  (om/component (dom/li nil (dom/a #js {:href (str "#" plant)} plant))))

(defn plant-list [channel plants]
      (om/component (dom/div nil (dom/h1 nil "Plants")
        (apply dom/ul nil (om/build-all (partial plant-list-item channel) plants)))))

(defn wait-view []
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "Loading data"))))

(defn read-url-hash [channel] (let [plant (js/decodeURIComponent (subs js/document.location.hash 1))] (put! channel plant)))

(defn init-ui [channel]
  (om/root
    (fn [data owner]
      (reify om/IRender
        (render [_] (if (nil? (:plants data)) (om/build wait-view nil) (om/build (partial plant-list channel) (:plants data))))))
    app-state
    {:target (. js/document (getElementById "app"))})
  (. js/window (addEventListener "hashchange" (fn [e] (read-url-hash channel))))
  (if (nil? js.document.location.hash) () (read-url-hash channel)))
