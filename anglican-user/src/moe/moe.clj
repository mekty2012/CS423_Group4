;; gorilla-repl.fileformat = 1

;; **
;;; #MoE
;;; This is MoE
;; **

;; @@
(ns moe.moe
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount add]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]
            [moe.gaussian :refer :all]
            [moe.batchreader :refer :all]
            [moe.preprocess :refer :all]
            [moe.samplers :refer :all]
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
(defn max-index [v] 
  (let [length (count v)]
    (loop [maximum (v 0)
           max-index 0
           i 1]
      (if (< i length)
        (let [value (v i)]
          (if (> value maximum)
            (recur value i (inc i))
            (recur maximum max-index (inc i))))
        max-index))))
;; @@

;; @@
(defn normalize [vector]
  (let [sum (reduce + 0.0 vector)]
    (map (fn [x] (/ x sum)) vector))
  )
;; @@

;; @@
; TODO

(defn kernel-compute [kernel vect]
  "Returns value applying kernel to vect. In fact, it is dot product."
  )

;The below 3 methods needs testing
(defn moe-feed-best-single [model vect]
  "Performs moe-feed, where gating model leads to cluster with highest probability"
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        kernel_vec (:kernel_vec model)
        prob_cluster (eval-gaussian-mixture vect pi mu_vec factor_vec)
        index (max-index prob_cluster)]
    (kernel-compute (nth kernel_vec index) vect)
    )
  )
    	
  

(defm moe-feed-prob-single [model vect]
  "Performs moe-feed, where gating model leads to cluster probabilistically. It does not need to be sample argument."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        kernel_vec (:kernel_vec model)
        prob_cluster  (map (fn [x] (Math/exp x) (eval-gaussian-mixture vect pi mu_vec factor_vec)))
        index (sample* (discrete prob_cluster))]
    (kernel-compute (nth kernel_vec index) vect)
  )
 )

(defn moe-feed-weight-single [model vect]
  "Performs moe-feed, where gating model performs weighted sum over all children."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        shape (clojure.core.matrix/shape (first factor_vec))
        kernel_vec (:kernel_vec model)
        prob-cluster (normalize (map (fn [x] (Math/exp x) (eval-gaussian-mixture vect pi mu_vec factor_vec))))
        kernel-collection (map (fn [x] (kernel-compute x vect)) kernel_vec)]
    (reduce clojure.core.matrix/add (zero-array shape) 
            (map (fn [vec p] 
                   (map (fn [x] (* p x)) vec)
                   ) kernel-collection prob-cluster))
  )
 )

;For the hierarchical case probably the same but with recusion at certain steps.

(defn moe-feed-best-hierarchical [model vect]
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        ischild_vec (:ischild_vec model)
        child_vec (:child_vec model)
        prob_cluster (eval-gaussian-mixture vect pi mu_vec factor_vec)
        index (max-index prob_cluster)]
    (if (= (nth ischild_vec index) 1)
      (moe-feed-best-hierarchical (nth child_vec index) vect)
      (kernel-compute (nth child_vec index) vect)
      )
    )
  )

(defm moe-feed-prob-hierarchical [model vect]
  "Performs moe-feed, where gating model leads to cluster probabilistically. It does not need to be sample argument."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        ischild_vec (:ischild_vec model)
        child_vec (:child_vec model)
        prob_cluster  (map (fn [x] (Math/exp x) (eval-gaussian-mixture vect pi mu_vec factor_vec)))
        index (sample* (discrete prob_cluster))]
    (if (= (nth ischild_vec index) 1)
      (moe-feed-best-hierarchical (nth child_vec index) vect)
      (kernel-compute (nth child_vec index) vect)
      )
  )
 )

;not yet
(defn moe-feed-weight-hierarchical [model vect]
  "Performs moe-feed, where gating model performs weighted sum over all children."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        shape (clojure.core.matrix/shape (first factor_vec))
        ischild_vec (:ischild_vec model)
        child_vec (:child_vec model)
        prob-cluster (normalize (map (fn [x] (Math/exp x) (eval-gaussian-mixture vect pi mu_vec factor_vec))))
        kernel-collection (map (fn [x] (if (= (nth ischild_vec x) 1) () (kernel-compute x vect))) child_vec)]
    (reduce clojure.core.matrix/add (zero-array shape) 
            (map (fn [vec p] 
                   (map (fn [x] (* p x)) vec)
                   ) kernel-collection prob-cluster))
  )
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
  (let [model (hierarchical-moe-sampler hyperparams)]
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
