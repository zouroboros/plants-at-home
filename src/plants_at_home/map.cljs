(ns plants-at-home.map
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [ajax.core :refer [GET]]
    [cljs.core.async :refer [put! <!]]
    [ol.Map]
    [ol.View]
    [ol.layer.Tile]
    [ol.source.OSM]
    [ol.proj]
    [ol.layer.Vector]
    [ol.source.Vector]
    [ol.style.Style]
    [ol.style.Stroke]
    [ol.style.Fill]
    [ol.format.GeoJSON]
    [ol.source.ImageVector]
    [ol.layer.Image]))

(defn error [] (js/console.log "Error loading countries"))

(defn omap [] (ol.Map. (js-obj "target" "map-container"
  "layers" (array (ol.layer.Tile. (js-obj "source" (ol.source.OSM.))))
  "view" (ol.View. (js-obj "center" (ol.proj.fromLonLat (array 0 0)) "zoom" 2)))))

(defonce polygon-style (ol.style.Style.
  (js-obj "stroke" (ol.style.Stroke. (js-obj "color" "white"))
   "fill" (ol.style.Fill. (js-obj "color" "rgba(0, 120, 255, 0.3)")))))

(defn source-vector [geo-json]
  (let [features (.readFeatures (ol.format.GeoJSON.) (clj->js geo-json) (js-obj "featureProjection" "EPSG:3857"))
        sourceVector (ol.source.Vector. (js-obj "features" features))]
  (ol.layer.Vector. (js-obj "source" sourceVector "style" polygon-style))))

(defn prepare-geojson [countries geojson]
  (let [countrySet (set countries)]
    (update-in geojson ["features"] (fn [old] (filter (fn [feature] (contains? countrySet (get-in feature ["properties" "name"]))) old)))))


(defn mark-countries [map geo-json countries]
  (if (.getLength (.getLayers map) > 1)
  (.removeLayer map (.item (.getLayers map) 1)))
  (.addLayer map (source-vector (prepare-geojson countries geo-json))))

(defn init-map [channel] (GET "countries.json" {
    :handler (fn [result] (let [map (omap)] (go (while true
        (let [countries (<! channel)]
         (mark-countries map result countries))))))
    :error-handler (fn [event] (error))}))
