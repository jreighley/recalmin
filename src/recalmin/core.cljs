(ns recalmin.core
  (:require
    [reagent.core :as reagent :refer [atom]]
    [recalcitrant.core :refer [error-boundary]]))

(enable-console-print!)

(println "This text is printed from src/recalmin/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn foo []
  ;;(throw "BAD")
  [:h1 (:text @app-state)])

(defn bar []
  ;;(throw "BAD")
  [:h1 "sup"])

(defn root []
  [:div
   [bar]
   [error-boundary [foo]]])

(reagent/render-component (fn [] [error-boundary [root]])
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
