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

(def state (atom {:position [200 200]
                  :velocity [1 1]
                  :paddle-left [30 30]
                  :paddle-right [610 450]}))

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

(defn- add-vec [& vecs]
  (apply mapv + vecs))

(defn- reverse-x [[vx vy]] [(* -1 vx) vy])
(defn- reverse-y [[vx vy]] [vx (* -1 vy)])

(defn- hit-wall? [[x y]]
  (or (< y 0) (> y screen-height)))

(defn- hit-paddle? [[px py] [x y]]
  (and (= px x) (>= y py) (<= y (+ py paddle-height))))

(defn- apply-collisions [{position :position
                          velocity :velocity
                          paddle-left :paddle-left
                          paddle-right :paddle-right}]
  (cond
    (hit-wall? position) (reverse-y velocity)
    (hit-paddle? paddle-left position) (reverse-x velocity)
    (hit-paddle? paddle-right position) (reverse-x velocity)
    :else
    velocity))

(defn- update-state [{position :position :as state}]
  (let [new-velocity (apply-collisions state)]
    (assoc state
           :velocity new-velocity
           :position (add-vec position new-velocity))))

(defn- tick []
  (swap! state update-state))

(defn- move-paddle [paddle-up paddle-down [px py :as paddle] c]
  (cond
    (= c paddle-up) [px (- py paddle-move-distance)]
    (= c paddle-down) [px (+ py paddle-move-distance)]
    :else paddle))

(def move-paddle-left (partial move-paddle paddle-left-up paddle-left-down))
(def move-paddle-right (partial move-paddle paddle-right-up paddle-right-down))

(defn- move-paddles [state c]
  (-> state
    (update :paddle-left move-paddle-left c)
    (update :paddle-right move-paddle-right c)))

(defn- handle-keypress [e]
  (swap! state move-paddles (char (.-keyCode e))))

(defn- setup []
  (let [ctx (.getContext (.getElementById js/document "scene") "2d")
        request-render #(.requestAnimationFrame js/window (partial render ctx))]
    (js/setInterval #(do (tick) (request-render)) (/ 1000 60))
    (.addEventListener js/window "keydown" handle-keypress)))

(.addEventListener js/window "load" setup)
