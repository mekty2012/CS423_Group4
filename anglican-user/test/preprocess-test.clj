;; gorilla-repl.fileformat = 1

;; **
;;; # Preprocessing
;;; 
;;; This is the clojure file that contains functions needed to preprocess the CIFAR 10 images. Completed functions are dropout, grayscale, and nbox
;; **

;; @@
(ns moe.preprocess; shouldn't it have a different name?
  (:require [gorilla-plot.core :as plot]
            [moe.preprocess :refer :all]))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
;Testing dropout
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
;Testing grayscale

(def grayed (grayscaled image))
(mikera.image.core/show grayed)
;; @@

;; @@
;Testing nbox


(def sevenbox1616 (nbox image 3 16 16))
	
(mikera.image.core/show sevenbox1616)

(def sevenbox00 (nbox image 3 0 0))

(mikera.image.core/show sevenbox00)
;; @@

;; @@
(map mikera.image.colours/components-rgb (im2vec sevenbox00 7))
;; @@

;; @@

;; @@
