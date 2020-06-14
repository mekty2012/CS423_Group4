;; gorilla-repl.fileformat = 1

;; **
;;; # GMM
;;; 
;;; This is GMM model. The multi variable normal function is made brand new, and the GMM query is taken from the anglican homepage
;; **

;; @@
(ns moe.gaussian
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]))
(use 'nstools.ns)
(ns+ template
  (:like anglican-user.worksheet))
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[nil,nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[nil,nil],nil]"}
;; <=

;; @@
(defn eval-multi-variable-normal [x mu sigma]
  (let [d (clojure.core.matrix/row-count mu)
        factor (Math/pow
                 (* (Math/pow
                      (* 2 Math/PI) d) (clojure.core.matrix/det sigma)) -0.5)
        x-minus-mu (clojure.core.matrix/sub x mu)
        inverse (clojure.core.matrix/inverse sigma)
        exponent (clojure.core.matrix/mmul
                   (clojure.core.matrix/transpose x-minus-mu) inverse x-minus-mu)]
    	(+ (Math/log factor) (* -0.5 exponent))
    )
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/eval-multi-variable-normal</span>","value":"#'template/eval-multi-variable-normal"}
;; <=

;; @@
; Test required
;Implementation of multivariate normal, where given input is not covariance matrix, but multiplier matrix.
(defdist factor-mvn [n mean-vec factor-mat] ; Distribution is factor-mat * ((repeatedly n N(0, 1)) + mean-vec)
  [inv-factor-mat (clojure.core.matrix/inverse factor-mat)] ; Internal variable that precomputes inverse of factor-mat
  (sample* [this]
           (clojure.core.matrix/add
             mean-vec
             (clojure.core.matrix/mmul
               factor-mat
               (repeatedly n (fn [] (sample* (normal 0 1)))))
             ))
  (observe* [this value]
            (reduce + (map (fn [t] (observe* (normal 0 1) t))
                           (clojure.core.matrix/sub
                             (clojure.core.matrix/mmul inv-factor-mat value)
                             mean-vec)
                           )))
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>#multifn[print-method 0x6323e1dd]</span>","value":"#multifn[print-method 0x6323e1dd]"}
;; <=

;; @@
(defn identity-matrix [n]
  (clojure.core.matrix/identity-matrix n)
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/identity-matrix</span>","value":"#'template/identity-matrix"}
;; <=

;; @@
; Test required
(defdist factor-gmm
  [n pi mu-vec factor-vec]
  [inverse-factor-vec (map clojure.core.matrix/inverse factor-vec)]
  (sample* [this]
           (let [chosen (sample* (discrete pi))
                 mu (nth mu-vec chosen)
                 factor (nth factor-vec chosen)]
             (clojure.core.matrix/mmul
               factor
               (clojure.core.matrix/add
                 mu
                 (repeatedly n (fn [] (sample* (normal 0 1)))))))
           )
  (observe* [this label-value]
            (let [label (first label-value)
                  value (rest label-value)
                  indexed-vec (range 0 (count pi))
                  gaussian-log-prob-vec (map (fn [i]
                                           (let [mu (nth mu-vec i)
                                                 inv-factor (nth inverse-factor-vec i)]
                                             (reduce + 0.0
                                                     (map
                                                       (fn [t]
                                                         (observe* (normal 0 1) (first t)))
                                                       (clojure.core.matrix/mmul
                                                         inv-factor
                                                         (clojure.core.matrix/transpose
                                                           (clojure.core.matrix/sub value mu)))
                                                       ))
                                             )) indexed-vec)
                  log-prob-vec (map + gaussian-log-prob-vec (map (fn [x] (Math/log x)) pi))
                  sum-prob (reduce + (map (fn [l] (Math/exp l)) log-prob-vec))]
              (- (nth log-prob-vec label) (Math/log sum-prob))
              )))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>#multifn[print-method 0x6323e1dd]</span>","value":"#multifn[print-method 0x6323e1dd]"}
;; <=

;; @@
(defn eval-gaussian-mixture [x pi mu-vec sigma-vec]
  "This function returns a vector that contains P(z_i = 1|mu, sigma)."
  (map
    (fn [i] (+ (Math/log (nth pi i)) (eval-multi-variable-normal x (nth mu-vec i) (nth sigma-vec i))))
    (range 0 (count pi)))
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/eval-gaussian-mixture</span>","value":"#'template/eval-gaussian-mixture"}
;; <=

;; @@
(defn row-mean [data] (clojure.core.matrix.operators// (reduce clojure.core.matrix.operators/+ data)
                                                       (clojure.core.matrix/row-count data)))

(defn invert
  ([W] (clojure.core.matrix.linear/solve W))
  ([kappa W] (clojure.core.matrix.linear/solve (clojure.core.matrix.operators/* kappa W))))

(defn shape [object]
  (clojure.core.matrix/shape object)
  )

(defn get-row [matrix]
  (clojure.core.matrix/get-row matrix)
  )
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/row-mean</span>","value":"#'template/row-mean"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/invert</span>","value":"#'template/invert"}],"value":"[#'template/row-mean,#'template/invert]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/shape</span>","value":"#'template/shape"}],"value":"[[#'template/row-mean,#'template/invert],#'template/shape]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/get-row</span>","value":"#'template/get-row"}],"value":"[[[#'template/row-mean,#'template/invert],#'template/shape],#'template/get-row]"}
;; <=

;; @@

;; @@
