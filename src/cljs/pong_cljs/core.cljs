(ns pong-cljs.core)

(enable-console-print!)

(def paddle-width 1)
(def paddle-height 50)
(def screen-width 640)
(def screen-height 480)
(def paddle-left-up \D)
(def paddle-left-down \S)
(def paddle-right-up \K)
(def paddle-right-down \J)
(def paddle-move-distance 10)

(def state (atom {:position [320 240]
                  :velocity [1 1]
                  :paddle-left [30 30]
                  :paddle-right [610 (- 450 paddle-height)]}))

(defn- draw-circle [ctx x y r]
  (.beginPath ctx)
  (.arc ctx x y r 0 (* 2 js/Math.PI))
  (.fill ctx))

(defn- render-ball [ctx]
 (let [[x y] (:position @state)]
   (draw-circle ctx x y 10)))

(defn- render-paddle [ctx paddle]
  (let [[x y] (get @state paddle)]
    (.fillRect ctx x y paddle-width paddle-height)))

(defn- render [ctx]
  (.clearRect ctx 0 0 screen-width screen-height)
  (set! (.-fillStyle ctx) "black")
  (render-ball ctx)
  (render-paddle ctx :paddle-left)
  (render-paddle ctx :paddle-right))

; TODO
(defn- move-ball [{position :position :as state}]
  state)

; TODO
(defn- move-paddles [{paddle-left :paddle-left paddle-right :paddle-right} c]
  state)

(defn- tick []
  (swap! state move-ball))

(defn- handle-keypress [e]
  (swap! state move-paddles (char (.-keyCode e))))

(defn- setup []
  (let [ctx (.getContext (.getElementById js/document "scene") "2d")
        request-render #(.requestAnimationFrame js/window (partial render ctx))]
    (js/setInterval #(do (tick) (request-render)) (/ 1000 60))
    (.addEventListener js/window "keydown" handle-keypress)))

(.addEventListener js/window "load" setup)
