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
;Made to avoid the hassle of dealing with defm, and simplify calculation
(defn moe-feed-best-single-n [model vect]
  "Performs moe-feed, where gating model leads to cluster with highest probability"
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        kernel_vec (:kernel_vec model)
        prob_cluster (map (fn [index]
                            (eval-gmm pi mu_vec factor_vec index vect)) (range 0 num_cluster))
        index (max-index prob_cluster)]
    (kernel-compute (nth kernel_vec index) vect)
    )
  )


  
(defn moe-feed-prob-single-n [model vect]
  "Performs moe-feed, where gating model leads to cluster probabilistically. It does not need to be sample argument."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        kernel_vec (:kernel_vec model)
        prob_cluster (normalize 
                        (map (fn [index]
                            (eval-gmm pi mu_vec factor_vec index vect)) (range 0 num_cluster)(range 0 num_cluster)))
                          
        index (sample* (discrete prob_cluster))]
    (kernel-compute (nth kernel_vec index) vect)
  )
 )


(defn moe-feed-weight-single-n [model vect]
  "Performs moe-feed, where gating model performs weighted sum over all children."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        shape (shape (first factor_vec))
        kernel_vec (:kernel_vec model)
        prob_cluster (normalize 
                        (map (fn [index]
                            (eval-gmm pi mu_vec factor_vec index vect)) (range 0 num_cluster)))
        kernel-collection (map (fn [x] (kernel-compute x vect)) kernel_vec)]
    (reduce add 0 (map mul kernel-collection prob_cluster))
  )
 )


;For the hierarchical case probably the same but with recusion at certain steps.

(defn moe-feed-best-hierarchical-n [model vect]
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        ischild_vec (:ischild_vec model)
        child_vec (:child_vec model)
        probs (map (fn [index]
                            (eval-gmm pi mu_vec factor_vec index vect)) (range 0 num_cluster))
        index (max-index probs)]
    (if (= (nth ischild_vec index) 1)
      (moe-feed-best-hierarchical-n (nth child_vec index) vect)
      (kernel-compute (nth child_vec index) vect)
      )
    )
  )


(defn moe-feed-prob-hierarchical-n [model vect]
  "Performs moe-feed, where gating model leads to cluster probabilistically. It does not need to be sample argument."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        ischild_vec (:ischild_vec model)
        child_vec (:child_vec model)
        prob_cluster  (normalize (map (fn [index]
                            (eval-gmm pi mu_vec factor_vec index vect)) (range 0 num_cluster)))
        index (sample* (discrete prob_cluster))]
    (if (= (nth ischild_vec index) 1)
      (moe-feed-prob-hierarchical-n (nth child_vec index) vect)
      (kernel-compute (nth child_vec index) vect)
      )
  )
 )


(defn moe-feed-weight-hierarchical-n [model vect]
  "Performs moe-feed, where gating model performs weighted sum over all children."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        shape (shape (first factor_vec))
        ischild_vec (:ischild_vec model)
        child_vec (:child_vec model)
        prob_cluster (normalize 
                        (map (fn [index]
                            (eval-gmm pi mu_vec factor_vec index vect)) (range 0 num_cluster)))
        kernel-collection (map 
                            (fn [x] 
                              (if (= (nth ischild_vec x) 1) 
                                (moe-feed-weight-hierarchical-n (nth child_vec x) vect) 
                                (kernel-compute (nth child_vec x) vect))) (range (count ischild_vec)))]
    (reduce add 0 (map mul kernel-collection prob_cluster))
  )
 )
 
;; @@

;; @@
(defn restore [image model box-size hyperparams]
  (let [ret (mikera.image.core/new-image 32 32)
        feeder (case (:feeder hyperparams)
                   "best" (if (:is-single hyperparams) moe-feed-best-single-n moe-feed-best-hierarchical-n)
                   "prob" (if (:is-single hyperparams) moe-feed-prob-single-n moe-feed-prob-hierarchical-n)
                   "weight" (if (:is-single hyperparams) moe-feed-weight-single-n moe-feed-weight-hierarchical-n)
                 	(println "Wrong feed")
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
                    res (feeder model uniform-vector)]
                (mikera.image.core/set-pixel ret x y 
                                             (mikera.image.colours/rgb-from-components 
                                               (uniform2pixel res) 
                                               (uniform2pixel res) 
                                               (uniform2pixel res)))
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
        (let [gray (int (* (rand) 255))]
        (mikera.image.core/set-pixel original x y (mikera.image.colours/rgb-from-components gray gray gray)))
        (recur (inc x) y))
        )
      )
    )
;; @@

;; @@
(mikera.image.core/show original)
(def dropped (dropoutted-normal original 0.1))
(mikera.image.core/show dropped)
;; @@

;; @@
(peak-signal-to-noise-ratio original dropped)
;; @@

;; @@
(def result-model )
;; @@

;; @@
(defn inverse-factor-vec [model]
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        ischild_vec (:ischild_vec model)
        child_vec (:child_vec model)
        inversed-factor-vec (map clojure.core.matrix/inverse factor_vec)
        inversed-child-vec (map (fn [index] 
                                    (if (= 1 (nth ischild_vec index)) 
                                      (inverse-factor-vec (nth child_vec index))
                                      (nth child_vec index))) (range 0 num_cluster))]
    {:num_cluster num_cluster, :pi pi, :mu_vec mu_vec, :factor_vec inversed-factor-vec, :ischild_vec ischild_vec, :child_vec inversed-child-vec}
    	)
  )
;; @@

;; @@
(def inverse-model (inverse-factor-vec result-model))

(def result-test )
;; @@

;; @@
(defn get-restore-result [image]
  (let [dropped (dropoutted-normal image 0.2)]
  (print "Original peak is: ")
  (println (peak-signal-to-noise-ratio image dropped))
  (print "New peak is: ")
  (println (peak-signal-to-noise-ratio image (restore dropped inverse-model 2 {:feeder "prob", :is-single false})))
    )
  )
;; @@

;; @@
(for-images-deer "data/cifar-10-batches-bin/data_batch_1.bin" 10 get-restore-result)
;; @@

;; @@
(defn for-images-deer [file-name iter-num do-fun]
  "Read file-name, to 32*32 images, and for each image, apply do-fun. It will perform do-fun for n images."
  (let [f (java.io.File. file-name)
		  st (byte-streams/to-byte-array f)]
    (loop [chunk (partition 3073 st) n iter-num]
      (let [ch (first chunk)
            deerclassifier (first ch)
            firstarray (next ch)
            image (mikera.image.core/new-image 32 32)
            pixels (mikera.image.core/get-pixels image)]
        (if (not= deerclassifier 4) (recur (next chunk) n)
          (do 
            (loop [i 0 j 0]
              (if (= j 32)
                (do-fun image)
                (if (= i 32)
                  (recur 0 (+ j 1))
                  (do
                    (mikera.image.core/set-pixel image i j (mikera.image.colours/rgb-from-components
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
    )
;; @@
