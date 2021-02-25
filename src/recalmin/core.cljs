(ns recalmin.core
  (:require
    [reagent.core :as reagent :refer [atom]]
    [recalcitrant.core :refer [error-boundary]]
    [clojure.string :as string]))

(enable-console-print!)

(defonce app-state (reagent/atom []))

(defn xy [e]
  (let [rect (.getBoundingClientRect (.-target e))]
    [(- (.-clientX e) (.-left rect))
     (- (.-clientY e) (.-top rect))]))

(defn svg [attrs boxes img]
  [:svg
   (merge-with merge attrs
               {:width 812 :height 609
                :stroke "black"
                :fill "none"
                :style {:border "1px solid"
                        :cursor "crosshair"}})
   (when @img
     [:image {:xlink-href @img
              :style {:pointer-events "none"}
              :width "100%"
              :height "100%"
              :opacity 0.3}])
   (for [[[x1 y1] [x2 y2]] @boxes]
     [:path {:d (str "M" x1 " " y1 " L " (string/join " " [x1 y2 x2 y2 x2 y1 x1 y1]))}])])

(defn mouse-handlers [boxes]
  (let [pen-down? (reagent/atom false)
        start-xy (reagent/atom nil)
        start-box
        (fn [e]
          (when (not= (.-buttons e) 0)
            (reset! pen-down? true)
            (let [coords (xy e)]
              (reset! start-xy coords))))
        end-box
        (fn [e]
          (when @pen-down?
            (swap! boxes conj [@start-xy (xy e) (random-uuid)])
            (reset! pen-down? false)))]
  {:on-mouse-down start-box
   :on-mouse-over start-box
   :on-mouse-up end-box
   :on-mouse-out end-box}))

(defn table [boxes]
  [:table
   [:thead
    [:tr
     [:td "BoxName"]
     [:td "x1"]
     [:td "y1"]
     [:td "x2"]
     [:td "y2"]]]
   [:tbody
    (for [[[x1 y1] [x2 y2] boxname] @boxes]
      [:tr
       [:td boxname]
       [:td x1]
       [:td y1]
       [:td x2]
       [:td y2]])]])

(defonce img (reagent/atom nil))

(defn root [boxes]
  [:div
   [:div
    [:label
     [:input
      {:type "file"
       :accept "image/*"
       :style {:display "none"}
       :on-change
       (fn image-selected [e]
         (let [r (js/FileReader.)]
           (set! (.-onload r)
                 (fn [e]
                   (reset! img (.. e -target -result))))
           (.readAsDataURL r (aget (.. e -target -files) 0))))}]
     [:span.mdl-button.mdl-button--icon
      {:title "Background"}
      [:i.material-icons "\uE3F4"]
      "Add backgound"]]]
   [svg (mouse-handlers boxes) boxes img]
   [table boxes]])

(reagent/render-component [(fn [] [error-boundary [root app-state]])]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
