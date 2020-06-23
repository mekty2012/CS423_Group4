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
 
 (defn mul [a vec]
   (clojure.core.matrix/mul a vec))
;; @@

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

;; @@
; TODO : There's no zero-array function

(defn kernel-compute [kernel vect]
  "Returns value applying kernel to vect. In fact, it is dot product."
  (clojure.core.matrix/mmul kernel (conj vect 1))
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
        prob_cluster (map (fn [index] 
                             (observe* (factor-gmm n pi mu_vec factor_vec) (list index vect))) (range 0 num_cluster))
        index (max-index prob_cluster)]
    (kernel-compute (nth kernel_vec index) vect)
    )
  )
)

  
(with-primitive-procedures [factor-gmm kernel-compute normalize]
(defm moe-feed-prob-single [n model vect]
  "Performs moe-feed, where gating model leads to cluster probabilistically. It does not need to be sample argument."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        kernel_vec (:kernel_vec model)
        prob_cluster (normalize 
                        (map 
                          (fn [index] (exp (observe* (factor-gmm n pi mu_vec factor_vec) (list index vect))))
                          (range 0 num_cluster)))
        index (sample (discrete prob_cluster))]
    (kernel-compute (nth kernel_vec index) vect)
  )
 )
)

(with-primitive-procedures [factor-gmm kernel-compute normalize shape add zero-array mul]
(defm moe-feed-weight-single [n model vect]
  "Performs moe-feed, where gating model performs weighted sum over all children."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        shape (shape (first mu_vec))
        kernel_vec (:kernel_vec model)
        prob_cluster (normalize 
                        (map 
                          (fn [index] (exp (observe* (factor-gmm n pi mu_vec factor_vec) (list index vect)))) 
                          (range 0 num_cluster)))
        kernel-collection (map (fn [x] (kernel-compute x vect)) kernel_vec)]
    (reduce add 0 (map mul kernel-collection prob_cluster))
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
        probs (map (fn [index] 
                             (observe* (factor-gmm n pi mu_vec factor_vec) (list index vect))) 
                          (range 0 num_cluster))
        index (max-index probs)]
    (if (= (nth ischild_vec index) 1)
      (moe-feed-best-hierarchical n (nth child_vec index) vect)
      (kernel-compute (nth child_vec index) vect)
      )
    )
  )
)

(with-primitive-procedures [factor-gmm kernel-compute normalize]
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
        index (sample (discrete prob_cluster))]
    (if (= (nth ischild_vec index) 1)
      (moe-feed-prob-hierarchical n (nth child_vec index) vect)
      (kernel-compute (nth child_vec index) vect)
      )
  )
 )
)

(with-primitive-procedures [factor-gmm kernel-compute normalize shape add zero-array mul]
(defm moe-feed-weight-hierarchical [n model vect]
  "Performs moe-feed, where gating model performs weighted sum over all children."
  (let [num_cluster (:num_cluster model)
        pi (:pi model)
        mu_vec (:mu_vec model)
        factor_vec (:factor_vec model)
        shape (shape (first mu_vec))
        ischild_vec (:ischild_vec model)
        child_vec (:child_vec model)
        prob_cluster (normalize 
                        (map 
                          (fn [index] (exp (observe* (factor-gmm n pi mu_vec factor_vec) [index vect]))) 
                          (range 0 num_cluster)))
        ;index (sample (discrete prob_cluster))
        kernel-collection (map 
                            (fn [x] 
                              (if (= (nth ischild_vec x) 1) 
                                (moe-feed-weight-hierarchical n (nth child_vec x) vect) 
                                (kernel-compute (nth child_vec x) vect))) (range (count ischild_vec)))]
    (reduce add 0 (map mul kernel-collection prob_cluster));(moe-feed-weight-hierarchical n (nth child_vec index) vect)
  )
 )
)
;; @@

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

(defn now []
  (.toString (java.util.Date.)))
;; @@

;; @@
;no deer classifier
(with-primitive-procedures [get-file new-image to-byte-array get-pixels set-pixel rgb-from-components sb2ub]
(defm for-images-m-old
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

;; @@
;has deer classifier
(with-primitive-procedures [get-file new-image to-byte-array get-pixels set-pixel rgb-from-components sb2ub now]
(defm for-images-m
  [file-name iter-num do-fun]
  "Read file-name, to 32*32 images, and for each image, apply do-fun. It will perform do-fun for n images."
  (let [f (get-file file-name)
		  st (to-byte-array f)]
    (loop [chunk (partition 3073 st) n iter-num]
      (let [ch (first chunk)
            deerclassifier (first ch)
            firstarray (next ch)
            image (new-image 32 32)
            pixels (get-pixels image)]
          (if (not= deerclassifier 4) (recur (next chunk) n)
            (do
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
  )
)
;; @@

;; @@
(with-primitive-procedures [nbox im2vec pixel2gray rgb2uniform get-pixel shape now]
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
                   "weight" (if (:is-single hyperparams) moe-feed-weight-single moe-feed-weight-hierarchical)
                   (println "Wrong feed")
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
                          ret (feeder (+ 1 (* 2 box-size)) model uniform-vector)]
                      (observe (normal (rgb2uniform (get-pixel im x y)) 1) ret))
                    (recur (inc x) y)
                    )
                  )
                )
              )
            )
          )
        )
      (print "Query made. Current time is: ")
      (println (now))
      model
      )
    )
  )
;; @@

;; @@
(with-primitive-procedures [nbox im2vec pixel2gray rgb2uniform get-pixel shape now]
  (defquery train-fast [file-name iter-num drop-prob box-size hyperparams]
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
                   "weight" (if (:is-single hyperparams) moe-feed-weight-single moe-feed-weight-hierarchical)
                   (println "Wrong feed")
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
                          uniform-vector (map rgb2uniform gray-vector)]
                      (when (= (nth uniform-vector (inc box-size)) -1)
                        (let [ret (feeder (+ 1 (* 2 box-size)) model uniform-vector)]
                          (observe (normal (rgb2uniform (get-pixel im x y)) 1) ret)
                          )
                        )
                      )
                    (recur (inc x) y)
                    )
                  )
                )
              )
            )
          )
        )
      (print "Query made. Current time is: ")
      (println (now))
      model
      )
    )
  )
;; @@
