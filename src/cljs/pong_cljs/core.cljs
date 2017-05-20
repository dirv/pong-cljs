(ns pong-cljs.core
  )

(enable-console-print!)

(def paddle-width 5)
(def paddle-height 25)
(def screen-width 640)
(def screen-height 480)

(def state (atom {:ball {:position [200 200]
                         :velocity [1 1]}
                  :paddle [10 30]}))

(defn- render-ball [ctx]
 (.beginPath ctx)
 (let [[x y] (:position (:ball @state))]
   (.arc ctx x y 10 0 (* 2 js/Math.PI)))
 (.fill ctx))

(defn- render-paddle [ctx]
  (let [[x y] (:paddle @state)]
    (.fillRect ctx x y paddle-width paddle-height)))

(defn- render [ctx]
  (.clearRect ctx 0 0 screen-width screen-height)
  (set! (.-fillStyle ctx) "black")
  (do (render-ball ctx)
      (render-paddle ctx)))

(defn- add-vec [& vecs]
  (apply mapv + vecs))

(defn- update-ball [{[x y :as position] :position [vx vy :as velocity] :velocity :as ball}]
  (-> ball
      (assoc :velocity (if (> y screen-height) [vx (* -1 vy)] velocity))
      (#(assoc % :position (add-vec position (:velocity %))))))

(defn- update-state [{ball :ball paddle :paddle}]
  {:ball (update-ball ball)
   :paddle paddle})

(defn- tick []
  (swap! state update-state)
  )

(defn- setup []
  (let [ctx (.getContext (.getElementById js/document "scene") "2d")
        request-render #(.requestAnimationFrame js/window (partial render ctx))]
    (js/setInterval #(do (tick) (request-render)) (/ 1000 60))
    ))

(.addEventListener js/window "load" setup)

