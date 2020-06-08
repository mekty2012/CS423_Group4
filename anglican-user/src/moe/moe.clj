;; gorilla-repl.fileformat = 1

;; **
;;; #MoE
;;; This is MoE
;; **

;; @@
(ns moe.moe
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]
            [moe.gaussian :refer :all]
            [moe.batchreader :refer :all]
            [moe.preprocess :refer :all]
            ))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(require 'byte-streams)
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
(defn pixel2gray [p]
  (let [rgb (mikera.image.colours/components-rgb p)]
    (/ (reduce + rgb) 3))
  )

(defn rgb2uniform [p]
  (- (/ p 127.5) 1)
  )
;; @@

;; @@
; TODO

(defn kernel-compute [kernel vect]
  "Returns value applying kernel to vect. In fact, it is dot product."
  )

(defn moe-feed-best [model vect]
  "Performs moe-feed, where gating model leads to cluster with highest probability"
  )

(defn moe-feed-prob [model vect]
  "Performs moe-feed, where gating model leads to cluster probabilistically. It does not need to be sample argument."
  )

(defn moe-feed-weight [model vect]
  "Performs moe-feed, where gating model performs weighted sum over all children."
  )
;; @@

;; @@
(defquery SingleLearning [file-name iter-num hyperparams]
  (let [model (single-moe-sampler hyperparams)]
    (for-images file-name iter-num
       (fn [im]
         (let [dropped (dropoutted im 0.3)])
         (loop [x 0 y 0]
           (if (= y 32)
             model
             (if (= x 32)
               (recur 0 (inc y))
               (do
                 (let [box (nbox image 3 x y)
                       box-vector (im2vec box)
                       gray-vector (map pixel2gray box-vector)
                       uniform-vector (map rgb2uniform gray-vector)
                       ret (moe-feed model uniform-vector)]
                   (observe (normal (rgb2uniform (mikera.image.core/get-pixel im x y)) 1) ret))
                 (recur (inc x) y)
                 )
               )
             )
           
           )
         ))
    )
  )
;; @@

;; @@
(defquery HierarchicalLearning [file-name iter-num hyperparams]
  (let [model (moe.preprocess/hierarchical-moe-sampler hyperparams)]
    (for-images file-name iter-num
       (fn [im]
         (let [dropped (dropoutted im 0.3)])
         (loop [x 0 y 0]
           (if (= y 32)
             model
             (if (= x 32)
               (recur 0 (inc y))
               (do
                 (let [box (nbox image 3 x y)
                       box-vector (im2vec box)
                       gray-vector (map pixel2gray box-vector)
                       uniform-vector (map rgb2uniform gray-vector)
                       ret (moe-feed model uniform-vector)]
                   (observe (normal (rgb2uniform (mikera.image.core/get-pixel im x y)) 1) ret))
                 (recur (inc x) y)
                 )
               )
             )
           
           )
         ))
    )
  )
;; @@
