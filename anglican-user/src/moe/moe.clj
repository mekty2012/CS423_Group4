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
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[nil,nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[nil,nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[nil,nil],nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[[nil,nil],nil],nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[[[nil,nil],nil],nil],nil],nil]"}
;; <=

;; @@
(defn pixel2gray [p]
  (let [rgb (mikera.image.colours/components-rgb p)]
    (/ (reduce + rgb) 3))
  )

(defn rgb2uniform [p]
  (- (/ p 127.5) 1)
  )
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/pixel2gray</span>","value":"#'template/pixel2gray"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/rgb2uniform</span>","value":"#'template/rgb2uniform"}],"value":"[#'template/pixel2gray,#'template/rgb2uniform]"}
;; <=

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
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/max-index</span>","value":"#'template/max-index"}
;; <=

;; @@
(def test (clojure.core.matrix/add [1 2 3]  [1 4 3] [7 8 9]))
test
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/test</span>","value":"#'template/test"},{"type":"list-like","open":"<span class='clj-vector'>[</span>","close":"<span class='clj-vector'>]</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>9</span>","value":"9"},{"type":"html","content":"<span class='clj-long'>14</span>","value":"14"},{"type":"html","content":"<span class='clj-long'>15</span>","value":"15"}],"value":"[9 14 15]"}],"value":"[#'template/test,[9 14 15]]"}
;; <=

;; @@
; TODO

(defn kernel-compute [kernel vect]
  "Returns value applying kernel to vect. In fact, it is dot product."
  )

(defn moe-feed-best [model vect]
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
    	
  

(defn moe-feed-prob [model vect]
  "Performs moe-feed, where gating model leads to cluster probabilistically. It does not need to be sample argument."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        kernel_vec (:kernel_vec model)
        prob_cluster (map (fn [x] (Math/exp x) (eval-gaussian-mixture vect pi mu_vec factor_vec)))
        index (categorical prob_cluster)]
    (kernel-compute (nth kernel_vec index) vect)
  )
 )

(defn moe-feed-weight [model vect]
  "Performs moe-feed, where gating model performs weighted sum over all children."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        kernel_vec (:kernel_vec model)
        prob-cluster (map (fn [x] (Math/exp x) (eval-gaussian-mixture vect pi mu_vec factor_vec)))
        kernel-collection (map (fn [x] (kernel-compute x vect)) kernel_vec)]
    (reduce clojure.core.matrix/add 
            (map (fn [vec p] 
                   (map (fn [x] (* p x)) vec)
                   ) kernel-collection prob-cluster))
  )
 )
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/kernel-compute</span>","value":"#'template/kernel-compute"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/moe-feed-best</span>","value":"#'template/moe-feed-best"}],"value":"[#'template/kernel-compute,#'template/moe-feed-best]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/moe-feed-prob</span>","value":"#'template/moe-feed-prob"}],"value":"[[#'template/kernel-compute,#'template/moe-feed-best],#'template/moe-feed-prob]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/moe-feed-weight</span>","value":"#'template/moe-feed-weight"}],"value":"[[[#'template/kernel-compute,#'template/moe-feed-best],#'template/moe-feed-prob],#'template/moe-feed-weight]"}
;; <=

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

;; @@

;; @@
