;; gorilla-repl.fileformat = 1

;; **
;;; # Result
;;; 
;;; This file analyzes result data.
;; **

;; @@
(ns moe.result
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount add]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]
            [moe.gaussian :refer :all]
            [moe.batchreader :refer :all]
            [moe.preprocess :refer :all]
            [moe.samplers :refer :all]
            [moe.moe :refer :all]
            ))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(require 'byte-streams)
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
(defm silly [a b]
  (+ a b)
  )

((silly (fn [a b] a) nil 3 3))
;; @@

;; @@
(defn uniform2pixel [r]
  (if (< r -1)
    0
    (if (>= r 1)
      255
      (int (* (inc r) 128)))
    )
  )
;; @@

;; @@
(defn restore [image model box-size hyperparams]
  (let [ret (mikera.image.core/new-image 32 32)
        feeder (case (:feeder hyperparams)
                   "best" (if (:is-single hyperparams) moe-feed-best-single moe-feed-best-hierarchical)
                   "prob" (if (:is-single hyperparams) moe-feed-prob-single moe-feed-prob-hierarchical)
                   (if (:is-single hyperparams) moe-feed-weight-single moe-feed-weight-hierarchical)
                   )]
    (loop [x 0 y 0]
      (if (= y 32)
        ret
        (if (= x 32)
          (recur 0 (inc y))
          (do
            (if (or (< x box-size) (< y box-size) (> x (- 31 box-size)) (> y (- 31 box-size)))
              (mikera.image.core/set-pixel ret x y (mikera.image.core/get-pixel image x y))
              (let [box (nbox image x y box-size)
                    box-vector (im2vec box (+ 1 (* 2 box-size)))
                    gray-vector (map pixel2gray box-vector)
                    uniform-vector (map rgb2uniform gray-vector)
                    ret ((feeder (fn [a b] a) nil model uniform-vector))]
                (mikera.image.core/set-pixel ret x y 
                                             (mikera.image.colours/rgb-from-components 
                                               (uniform2pixel ret) 
                                               (uniform2pixel ret) 
                                               (uniform2pixel ret)))
                )
              )
            (recur (inc x) y)
            )
          )
        )
      )
    )
  )
;; @@

;; @@
(defn log10 [t]
  (/ (log t) (log 10))
  )

(defn peak-signal-to-noise-ratio [original noised]
  (loop [x 0 y 0 mse 0.0]
    (if (= y 32)
      (* 10 (log10 (/ (* 255 255) mse)))
      (if (= x 32)
        (recur 0 (inc y) mse)
        (let [original-pixel (pixel2gray (mikera.image.core/get-pixel original x y))
              noised-pixel (pixel2gray (mikera.image.core/get-pixel noised x y))
              diff (- original-pixel noised-pixel)
              diff2 (* diff diff)]
          (recur (inc x) y (+ mse (/ diff2 1024))))
        )
      )
    )
  )
;; @@

;; @@
(def original (mikera.image.core/new-image 32 32))
(loop [x 0 y 0]
  (if (= y 32)
  	nil
    (if (= x 32)
      (recur 0 (inc y))
      (do
        (mikera.image.core/set-pixel original x y (mikera.image.colours/rgb-from-components 255 255 255))
        (recur (inc x) y)
        )
      )
    )
  )
;; @@

;; @@
(mikera.image.core/show original)
(def dropped (dropoutted original 0.1))
(mikera.image.core/show dropped)
;; @@

;; @@
(peak-signal-to-noise-ratio original dropped)
;; @@

;; @@

;; @@
