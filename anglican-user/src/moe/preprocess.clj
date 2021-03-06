;; gorilla-repl.fileformat = 1

;; **
;;; # Preprocessing
;;; 
;;; This is the clojure file that contains functions needed to preprocess the CIFAR 10 images. Completed functions are dropout, grayscale, and nbox
;; **

;; @@
(ns moe.preprocess
  (:require [gorilla-plot.core :as plot]))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
(defn new-image [x y]
  (mikera.image.core/new-image 32 32))

(defn set-pixel [im x y rgb]
  (mikera.image.core/set-pixel im x y rgb)
  )

(defn get-pixel [im x y]
  (mikera.image.core/get-pixel im x y)
  )

(defn rgb-from-components [r g b]
  (mikera.image.colours/rgb-from-components r g b))
;; @@

;; @@
;Function to return image with each pixel dropped with probability p
(with-primitive-procedures [new-image set-pixel rgb-from-components get-pixel]
(defm dropoutted [image p]
  (let [ret-image (new-image 32 32)]
    (loop [x 0 y 0]
      (if (= y 32)
        ret-image ;Loop done. Return new image.
        (if (= x 32)
          (recur 0 (+ y 1))
          (do
            (if (= 1 (sample (bernoulli p)))
              (set-pixel ret-image x y (rgb-from-components 0 0 0)) ;Below threshold. Drop pixel
              (set-pixel ret-image x y (get-pixel image x y)) ;Else keep pixel
              )
            (recur (+ x 1) y)
            )
          )
        )
      )
    )
  )
  )
;; @@

;; @@
(defn dropoutted-normal [image p]
  (let [ret-image (new-image 32 32)]
    (loop [x 0 y 0]
      (if (= y 32)
        ret-image ;Loop done. Return new image.
        (if (= x 32)
          (recur 0 (+ y 1))
          (do
            (if ( < (rand) p)
              (set-pixel ret-image x y (rgb-from-components 0 0 0)) ;Below threshold. Drop pixel
              (set-pixel ret-image x y (get-pixel image x y)) ;Else keep pixel
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
;Return appropriate grayscale value of given rgb
(defn togray [rgb]
  (let [sum (+ (nth rgb 0) (+ (nth rgb 1) (nth rgb 2)))]
    (int (/ sum 3))
    )
  )

;Get grayscaled image of given image
(defn grayscaled [image]
  (let [ret-image (mikera.image.core/new-image 32 32)]
    (loop [x 0 y 0]
      (if (= y 32)
        ret-image ;Loop done. Return new image.
        (if (= x 32)
          (recur 0 (+ y 1))
          (do
            (let [pixelcolor (mikera.image.core/get-pixel image x y); Get rgb of current pixel
                  rgb (mikera.image.colours/components-rgb pixelcolor)]
              (mikera.image.core/set-pixel ret-image x y (mikera.image.colours/rgb-from-components (togray rgb) (togray rgb) (togray rgb))) ;Insert grayscale pixel in new image
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
;Function to get 2n+1 by 2n+1 image that is centered at ij pixel of original image.
(defn nbox [image n i j]
  (let [box (mikera.image.core/new-image (+ (* 2 n) 1) (+ (* 2 n) 1))]
    (loop [x (- 0 n) y (- 0 n)]
    	(if (= y (+ 1 n))
            box ;Loop done. Return new image
    		(if (= x (+ 1 n))
              (recur (- 0 n) (+ y 1)) ;One column done, go to next
              (do
              (if (or (< (+ i x) 0) (< (+ j y) 0) (> (+ i x) 31) (> (+ j y ) 31))
                    (mikera.image.core/set-pixel box (+ x n) (+ y n)(mikera.image.colours/rgb-from-components 0 0 0))
                (let [pixelcolor (mikera.image.core/get-pixel image (+ i x) (+ j y))]
                    (mikera.image.core/set-pixel box (+ x n) (+ y n) pixelcolor)
                  )
                )
          	(recur (+ x 1 ) y))

          )
    	)
    )
  ))
;; @@

;; @@
(defn im2vec [image n]
  (loop [x (- n 1) y (- n 1) l nil]
    (if (= y -1)
      l
      (if (= x -1)
        (recur (- n 1) (- y 1) l)
        (recur (- x 1) y (cons (mikera.image.core/get-pixel image x y) l))
        )
      )
    )
  )
;; @@

;; @@

;; @@
