;; gorilla-repl.fileformat = 1

;; **
;;; # Preprocessing
;;; 
;;; This is the clojure file that contains functions needed to preprocess the CIFAR 10 images. Completed functions are dropout, grayscale, and nbox
;; **

;; @@
(ns moe.preprocess; shouldn't it have a different name?
  (:require [gorilla-plot.core :as plot]
            [moe.preprocess :refer :all]
            [moe.batchreader :refer :all]))
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
;(def dropped (dropoutted image 0.5))
;(mikera.image.core/show dropped)
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
(for-images-deer "data/cifar-10-batches-bin/data_batch_1.bin" 1 (fn [im] (mikera.image.core/save (grayscaled im) "/home/gary/monochrasaome.png" :quality 1.0 :progressive false))) ; OpenJDK does not support .jpg
;; @@

;; @@
;Function to return image with each pixel dropped with probability p
(with-primitive-procedures [new-image set-pixel rgb-from-components get-pixel]
(defn d [image p]
  (let [ret-image (mikera.image.core/new-image 32 32)]
    (loop [x 0 y 0]
      (if (= y 32)
        ret-image
        (if (= x 32)
          (recur 0 (+ y 1))
          (do
            (if (> p (rand))
              (mikera.image.core/set-pixel ret-image x y (mikera.image.colours/rgb-from-components 0 0 0)) ;Below threshold. Drop pixel
              (mikera.image.core/set-pixel ret-image x y (mikera.image.core/get-pixel image x y)) ;Else keep pixel
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
(for-images-deer "data/cifar-10-batches-bin/data_batch_1.bin" 1 (fn [im] (mikera.image.core/save (d (grayscaled im) 0.2) "/home/gary/dropout.png" :quality 1.0 :progressive false)))
;; @@

;; @@
;(for-images-deer "data/cifar-10-batches-bin/data_batch_1.bin" 1 (fn [im] (println (im2vec (d (grayscaled im) 0.2) 32))))
;; @@

;; @@

;; @@
