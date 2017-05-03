(ns plants-at-home.ui
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]))

(def app-state (atom {:plants nil :current-plant nil}))

(defn plant-list-item [current-plant plant]
  (let [class (if (= current-plant plant) "current" nil)]
    (om/component (dom/li nil
      (dom/a #js {:href (str "#" plant) :className class} plant)))))

(defn plant-search [data owner]
  (reify om/IRenderState
    (render-state [this {:keys [search]}]
      (dom/div #js {:className "search-box"}
        (dom/input #js {:type "text" :onChange (fn [e] (put! search (.. e -target -value)))})))))

(defn plant-list [data owner]
  (reify
    om/IInitState
      (init-state [_]
        {:search-c (chan) :current-plants (:plants data)})
    om/IWillMount
      (will-mount [_]
        (let [search-c (om/get-state owner :search-c)]
        (go (loop []
             (let [search (<! search-c)]
               (om/set-state! owner :current-plants (filter (fn [plant] (string/includes? plant search)) (:plants data)))
               (recur))))))
     om/IRenderState
     (render-state [this {:keys [search-c current-plants]}]
       (dom/div nil
       (om/build plant-search () {:init-state {:search search-c}})
       (apply dom/ul #js {:className "plant-list"}
         (om/build-all (partial plant-list-item (:current-plant data)) current-plants))))))


(defn wait-view []
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "Loading data"))))

(defn read-url-hash [channel]
  (let [plant (js/decodeURIComponent (subs js/document.location.hash 1))]
    (put! channel plant)))

(defn init-ui [ui-channel]
  (om/root
    (fn [data owner]
      (reify om/IRender
        (render [_] (if (nil? (:plants data)) (om/build wait-view nil)
          (om/build
            plant-list data)))))
    app-state
    {:target (. js/document (getElementById "app"))})
  (. js/window (addEventListener "hashchange"
    (fn [e] (read-url-hash ui-channel))))
  (if (nil? js.document.location.hash) () (read-url-hash ui-channel)))
