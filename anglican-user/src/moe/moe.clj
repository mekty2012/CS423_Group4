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
;; ->
;;; batchreader import Success
;;; 
;; <-
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
    (loop [maximum (nth v 0)
           max-index 0
           i 1]
      (if (< i length)
        (let [value (nth v i)]
          (if (> value maximum)
            (recur value i (inc i))
            (recur maximum max-index (inc i))))
        max-index))))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/max-index</span>","value":"#'template/max-index"}
;; <=

;; @@
(defn normalize [vector]
  (let [sum (reduce + 0.0 vector)]
    (map (fn [x] (/ x sum)) vector))
  )

(defn shape [mat]
   (clojure.core.matrix/shape mat))

 (defn add [a b]
   (clojure.core.matrix/add a b))

 (defn zero-array [shape]
   (clojure.core.matrix/zero-array shape))
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/normalize</span>","value":"#'template/normalize"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/shape</span>","value":"#'template/shape"}],"value":"[#'template/normalize,#'template/shape]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/add</span>","value":"#'template/add"}],"value":"[[#'template/normalize,#'template/shape],#'template/add]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/zero-array</span>","value":"#'template/zero-array"}],"value":"[[[#'template/normalize,#'template/shape],#'template/add],#'template/zero-array]"}
;; <=

;; @@
; TODO : There's no zero-array function

(defn kernel-compute [kernel vect]
  "Returns value applying kernel to vect. In fact, it is dot product."
  (clojure.core.matrix/mmul kernel vect)
  )

;The below 3 methods needs testing
(with-primitive-procedures [max-index factor-gmm kernel-compute]
(defm moe-feed-best-single [n model vect]
  "Performs moe-feed, where gating model leads to cluster with highest probability"
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        kernel_vec (:kernel_vec model)
        observe-gmm ()
        prob_cluster (map (fn [index] 
                             (observe* (factor-gmm n pi mu_vec factor_vec) [index vect])) (range 0 num_cluster))
        index (max-index prob_cluster)]
    (kernel-compute (nth kernel_vec index) vect)
    )
  )
)    	
  
(with-primitive-procedures [factor-gmm kernel-compute]
(defm moe-feed-prob-single [n model vect]
  "Performs moe-feed, where gating model leads to cluster probabilistically. It does not need to be sample argument."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        kernel_vec (:kernel_vec model)
        prob_cluster (normalize 
                        (map 
                          (fn [index] (exp (observe* (factor-gmm n pi mu_vec factor_vec) [index vect]))) 
                          (range 0 num_cluster)))
        index (sample* (discrete prob_cluster))]
    (kernel-compute (nth kernel_vec index) vect)
  )
 )
)

(with-primitive-procedures [factor-gmm kernel-compute shape add zero-array]
(defm moe-feed-weight-single [n model vect]
  "Performs moe-feed, where gating model performs weighted sum over all children."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        shape (shape (first factor_vec))
        kernel_vec (:kernel_vec model)
        prob_cluster (normalize 
                        (map 
                          (fn [index] (exp (observe* (factor-gmm n pi mu_vec factor_vec) [index vect]))) 
                          (range 0 num_cluster)))
        kernel-collection (map (fn [x] (kernel-compute x vect)) kernel_vec)]
    (reduce add (zero-array shape) 
            (map (fn [vec p] 
                   (map (fn [x] (* p x)) vec)
                   ) kernel-collection prob_cluster))
  )
 )
)

;For the hierarchical case probably the same but with recusion at certain steps.

(with-primitive-procedures [max-index factor-gmm kernel-compute]
(defm moe-feed-best-hierarchical [n model vect]
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        ischild_vec (:ischild_vec model)
        child_vec (:child_vec model)
        prob_cluster (map (fn [index] 
                             (observe* (factor-gmm n pi mu_vec factor_vec) [index vect])) (range 0 num_cluster))
        index (max-index prob_cluster)]
    (if (= (nth ischild_vec index) 1)
      (moe-feed-best-hierarchical (nth child_vec index) vect)
      (kernel-compute (nth child_vec index) vect)
      )
    )
  )
)

(with-primitive-procedures [factor-gmm kernel-compute]
(defm moe-feed-prob-hierarchical [n model vect]
  "Performs moe-feed, where gating model leads to cluster probabilistically. It does not need to be sample argument."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        ischild_vec (:ischild_vec model)
        child_vec (:child_vec model)
        prob_cluster  (normalize (map 
                                    (fn [index] (exp (observe* (factor-gmm n pi mu_vec factor_vec) [index vect]))) 
                                    (range 0 num_cluster)))
        index (sample* (discrete prob_cluster))]
    (if (= (nth ischild_vec index) 1)
      (moe-feed-prob-hierarchical (nth child_vec index) vect)
      (kernel-compute (nth child_vec index) vect)
      )
  )
 )
)

(with-primitive-procedures [factor-gmm kernel-compute shape add zero-array]
(defm moe-feed-weight-hierarchical [n model vect]
  "Performs moe-feed, where gating model performs weighted sum over all children."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        shape (shape (first factor_vec))
        ischild_vec (:ischild_vec model)
        child_vec (:child_vec model)
        prob_cluster (normalize 
                        (map 
                          (fn [index] (exp (observe* (factor-gmm n pi mu_vec factor_vec) [index vect]))) 
                          (range 0 num_cluster)))
         index (sample* (discrete prob_cluster))
        kernel-collection (map 
                            (fn [x] 
                              (if (= (nth ischild_vec x) 1) 
                                (moe-feed-weight-hierarchical (nth child_vec x) vect) 
                                (kernel-compute x vect))) child_vec)]
    (reduce add (zero-array shape) 
            (map (fn [vec p] 
                   (map (fn [x] (* p x)) vec)
                   ) kernel-collection prob_cluster))
  )
 )
 )
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/kernel-compute</span>","value":"#'template/kernel-compute"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/moe-feed-best-single</span>","value":"#'template/moe-feed-best-single"}],"value":"[#'template/kernel-compute,#'template/moe-feed-best-single]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/moe-feed-prob-single</span>","value":"#'template/moe-feed-prob-single"}],"value":"[[#'template/kernel-compute,#'template/moe-feed-best-single],#'template/moe-feed-prob-single]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/moe-feed-weight-single</span>","value":"#'template/moe-feed-weight-single"}],"value":"[[[#'template/kernel-compute,#'template/moe-feed-best-single],#'template/moe-feed-prob-single],#'template/moe-feed-weight-single]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/moe-feed-best-hierarchical</span>","value":"#'template/moe-feed-best-hierarchical"}],"value":"[[[[#'template/kernel-compute,#'template/moe-feed-best-single],#'template/moe-feed-prob-single],#'template/moe-feed-weight-single],#'template/moe-feed-best-hierarchical]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/moe-feed-prob-hierarchical</span>","value":"#'template/moe-feed-prob-hierarchical"}],"value":"[[[[[#'template/kernel-compute,#'template/moe-feed-best-single],#'template/moe-feed-prob-single],#'template/moe-feed-weight-single],#'template/moe-feed-best-hierarchical],#'template/moe-feed-prob-hierarchical]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/moe-feed-weight-hierarchical</span>","value":"#'template/moe-feed-weight-hierarchical"}],"value":"[[[[[[#'template/kernel-compute,#'template/moe-feed-best-single],#'template/moe-feed-prob-single],#'template/moe-feed-weight-single],#'template/moe-feed-best-hierarchical],#'template/moe-feed-prob-hierarchical],#'template/moe-feed-weight-hierarchical]"}
;; <=

;; @@
(defn get-file [file-name]
  (java.io.File. file-name))

(defn new-image [a b]
  (mikera.image.core/new-image a b)
  )

(defn to-byte-array [f]
  (byte-streams/to-byte-array f))

(defn get-pixels [im]
  (mikera.image.core/get-pixels im))

(defn set-pixel [im x y rgbcomponent]
  (mikera.image.core/set-pixel im x y rgbcomponent))

(defn rgb-from-components [x y z]
  (mikera.image.colours/rgb-from-components x y z))

(defn get-pixel [im x y]
  (mikera.image.core/get-pixel im x y))
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/get-file</span>","value":"#'template/get-file"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/new-image</span>","value":"#'template/new-image"}],"value":"[#'template/get-file,#'template/new-image]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/to-byte-array</span>","value":"#'template/to-byte-array"}],"value":"[[#'template/get-file,#'template/new-image],#'template/to-byte-array]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/get-pixels</span>","value":"#'template/get-pixels"}],"value":"[[[#'template/get-file,#'template/new-image],#'template/to-byte-array],#'template/get-pixels]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/set-pixel</span>","value":"#'template/set-pixel"}],"value":"[[[[#'template/get-file,#'template/new-image],#'template/to-byte-array],#'template/get-pixels],#'template/set-pixel]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/rgb-from-components</span>","value":"#'template/rgb-from-components"}],"value":"[[[[[#'template/get-file,#'template/new-image],#'template/to-byte-array],#'template/get-pixels],#'template/set-pixel],#'template/rgb-from-components]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/get-pixel</span>","value":"#'template/get-pixel"}],"value":"[[[[[[#'template/get-file,#'template/new-image],#'template/to-byte-array],#'template/get-pixels],#'template/set-pixel],#'template/rgb-from-components],#'template/get-pixel]"}
;; <=

;; @@
(with-primitive-procedures [get-file new-image to-byte-array get-pixels set-pixel rgb-from-components sb2ub]
(defm for-images-m
  [file-name iter-num do-fun]
  "Read file-name, to 32*32 images, and for each image, apply do-fun. It will perform do-fun for n images."
  (let [f (get-file file-name)
		  st (to-byte-array f)]
    (loop [chunk (partition 3073 st) n iter-num]
      (let [ch (first chunk)
            firstarray (next ch)
            image (new-image 32 32)
            pixels (get-pixels image)]
        	(loop [i 0 j 0]
              (if (= j 32)
                (do-fun image)
                (if (= i 32)
                  (recur 0 (+ j 1))
                  (do
                    (set-pixel image i j (rgb-from-components
                                                             (sb2ub (nth firstarray (+ i (* 32 j))))
                                                             (sb2ub (nth firstarray (+ 1024 (+ i (* 32 j)))))
                                                             (sb2ub (nth firstarray (+ 2048 (+ i (* 32 j)))))))
                    (recur (+ i 1) j)
                    )
                  )
                )
              ) 
              (when (> n 1) (recur (next chunk) (- n 1)))
        )
      )
    )
  )
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/for-images-m</span>","value":"#'template/for-images-m"}
;; <=

;; @@
(with-primitive-procedures [dropoutted nbox im2vec pixel2gray rgb2uniform get-pixel shape
                            moe-feed-best-single moe-feed-prob-single moe-feed-weight-single
                            moe-feed-best-hierarchical moe-feed-prob-hierarchical moe-feed-weight-hierarchical]
  (defquery train [file-name iter-num drop-prob box-size hyperparams]
    (let [model (if (:is-single hyperparams)
                  (if (:auto-tune hyperparams)
                    (autotuned-single-moe-sampler hyperparams)
                    (single-moe-sampler hyperparams)
                    )
                  (if (:auto-tune hyperparams)
                    (autotuned-hierarchical-moe-sampler hyperparams)
                    (hierarchical-moe-sampler hyperparams)
                    )
                  )
          feeder (case (:feeder hyperparams)
                   "best" (if (:is-single hyperparams) moe-feed-best-single moe-feed-best-hierarchical)
                   "prob" (if (:is-single hyperparams) moe-feed-prob-single moe-feed-prob-hierarchical)
                   (if (:is-single hyperparams) moe-feed-weight-single moe-feed-weight-hierarchical)
                   )
          ]
      (for-images-m file-name iter-num
        (fn [im]
          (let [dropped (dropoutted im drop-prob)]
            (loop [x box-size y box-size]
              (if (= y (- 32 box-size))
                nil
                (if (= x (- 32 box-size))
                  (recur box-size (inc y))
                  (do
                    (let [box (nbox im box-size x y)
                          box-vector (im2vec box (+ 1 (* 2 box-size)))
                          gray-vector (map pixel2gray box-vector)
                          uniform-vector (map rgb2uniform gray-vector)
                          ret (feeder model uniform-vector)]
                      (observe (normal (rgb2uniform (get-pixel im x y)) 1) ret))
                    (recur (inc x) y)
                    )
                  )
                )
              )
            )
          )
        )
      model
      )
    )
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/train</span>","value":"#'template/train"}
;; <=

;; @@

;; @@
