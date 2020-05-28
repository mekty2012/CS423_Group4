;; gorilla-repl.fileformat = 1

;; **
;;; # Gorilla REPL
;;; 
;;; Welcome to gorilla :-)
;;; 
;;; Shift + enter evaluates code. Hit alt+g twice in quick succession or click the menu icon (upper-right corner) for more commands ...
;;; 
;;; It's a good habit to run each worksheet in its own namespace: feel free to use the declaration we've provided below if you'd like.
;; **

;; @@
(ns image-preprocess
  (:require [gorilla-plot.core :as plot]))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(require 'byte-streams)
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
(defn dropoutted [image p]
  (let [ret-image (mikera.image.core/new-image 32 32)]
    (loop [x 0 y 0]
      (if (= y 32)
        ret-image
        (if (= x 32)
          (recur 0 (+ y 1))
          (do
            (if (< (rand) p)
              (mikera.image.core/set-pixel ret-image x y (mikera.image.colours/rgb-from-components 0 0 0))
              (mikera.image.core/set-pixel ret-image x y (mikera.image.core/get-pixel image x y))
              )
            (recur (+ x 1) y)
            )
          )
        )
      )
    )
  )
;; @@

;; @@
(def image (mikera.image.core/new-image 32 32))

(loop [i 0 j 0]
  (if (= j 32)
      image
      (if (= i 32)
        (recur 0 (+ j 1))
        (do 
          (mikera.image.core/set-pixel image i j (mikera.image.colours/rgb-from-components 255 (+ 7 (* 8 i)) (+ 7 (* 8 j))))
          (recur (+ i 1) j)
          )
        )
    )
  )
(def dropped (dropoutted image 0.5))
(mikera.image.core/show dropped)
(mikera.image.core/show image)
;; @@

;; @@
(defn togray [rgb]
  (let [sum (+ (nth rgb 0) (+ (nth rgb 1) (nth rgb 2)))]    
    (int (/ sum 3))
    )
  )

(defn grayscaled [image]
  (let [ret-image (mikera.image.core/new-image 32 32)]
    (loop [x 0 y 0]
      (if (= y 32)
        ret-image
        (if (= x 32)
          (recur 0 (+ y 1))
          (do
            (let [pixelcolor (mikera.image.core/get-pixel image x y)
                  rgb (mikera.image.colours/components-rgb pixelcolor)]
              (mikera.image.core/set-pixel ret-image x y (mikera.image.colours/rgb-from-components (togray rgb) (togray rgb) (togray rgb)))
            )
            (recur (+ x 1) y)
            )
          )
        )
      )
    )
  )
;; @@

;; @@
(def grayed (grayscaled image))
(mikera.image.core/show grayed)
;; @@

;; @@

;; @@
