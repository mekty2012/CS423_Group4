;; gorilla-repl.fileformat = 1

;; **
;;; # Gorilla REPL
;;; 
;;; Welcome to gorilla :-)
;;; 
;;; Shift + enter evaluates code. Hit alt+g twice in quick succession or click the menu icon (upper-right corner) for more commands ...
;;; 
;;; It's a good habit to run each worksheet in its own namespace: feel free to use the declaration we've provided below if you'd like.
;; **

;; @@
(ns gmm-generator
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]))
(use 'nstools.ns)
(ns+ templete
  (:like anglican-user.worksheet))
;; @@

;; @@
(defm cluster-num-sampler [lambda]
  (sample (poisson lambda))
  )

(defm tuned-cluster-num-sample [a b]
  (let [lambda (sample (gamma a b))]
    {:lambda lambda :n (sample (poisson lambda))})
  )
;; @@

;; @@
(defm pi-sampler [n alpha]
  (sample (dirichlet (repeat n alpha)))
  )

(defm mu-sampler [n mu-mu mu-sigma]
  (repeatedly n (fn [] (sample (normal mu-mu mu-sigma)))
  ))

(defm factor-sampler [n factor-mu factor-sigma]
  (repeatedly n (fn [] (repeatedly n (fn [] (sample (normal factor-mu factor-sigma)))))))

(defm gmm-sampler [n lambda alpha mu-mu mu-sigma factor-mu factor-sigma]
  (let [num_cluster (cluster-num-sampler lambda)
        pi (pi-sampler num_cluster alpha)
        mu_vec (repeatedly num_cluster (fn [] (mu-sampler n mu-mu mu-sigma)))
        factor_vec (repeatedly num_cluster (fn [] (factor-sampler n factor-mu factor-sigma)))]
    {:num_cluster num_cluster
     :pi pi
     :mu_vec mu_vec
     :factor_vec factor_vec}
    )
  )
;; @@

;; @@
; Since kernel here, is similar to CNN's single layer, I suggest random initialization method
; of CNN layer as probabilistic model. Note that this is not that reasonable. 
; The derivation of CNN layer initialization is based on signal preservation, where probabilistic programming
; does not contain any kind of signal propagation.
(defn xavier [n] (sqrt (/ 6 (+ (* n n) 1))))

(defm kernel-sampler [n]
  (repeatedly n (fn [] (repeatedly n (fn [] (sample (uniform-continuous (- (xavier n)) (xavier n)))))))
  )
;; @@

;; @@
(defm single-moe-sampler [n lambda alpha mu-mu mu-sigma factor-mu factor-sigma]
  (let [num_cluster (cluster-num-sampler lambda)
        pi (pi-sampler num_cluster alpha)
        mu_vec (repeatedly num_cluster (fn [] (mu-sampler n mu-mu mu-sigma)))
        factor_vec (repeatedly num_cluster (fn [] (factor-sampler n factor-mu factor-sigma)))
        kernel_vec (repeatedly num_cluster (fn [] (kernel-sampler n)))]
    {:num_cluster num_cluster
     :pi pi
     :mu_vec mu_vec
     :factor_vec factor_vec
     :kernel_vec kernel_vec}
    )
  )
;; @@

;; @@
;n p  lambda alpha mu-mu mu-sigma factor-mu factor-sigma

(defm hierarchical-moe-sampler [hyperparameters]
  (let [n (:n hyperparameters)
        p (:p hyperparameters)
        lambda (:lambda hyperparameters)
        alpha (:alpha hyperparameters)
        mu-mu (:mu-mu hyperparameters)
        mu-sigma (:mu-sigma hyperparameters)
        factor-mu (:factor-mu hyperparameters)
        factor-sigma (:factor-sigma hyperparameters)
        num_cluster (cluster-num-sampler lambda)
        pi (pi-sampler num_cluster alpha)
        mu_vec (repeatedly num_cluster (fn [] (mu-sampler n mu-mu mu-sigma)))
        factor_vec (repeatedly num_cluster (fn [] (factor-sampler n factor-mu factor-sigma)))
        ischild_vec (repeatedly num_cluster (fn [] (sample (bernoulli p))))
        child_vec (map (fn [i] 
                         (if (= i 1) 
                           (hierarchical_moe_sampler hyperparameters)
                           (kernel-sampler n)
                           )) ischild_vec)]
      {:num_cluster num_cluster
       :pi pi
       :mu_vec mu_vec
       :factor_vec factor_vec
       :ischild_vec ischild_vec
       :child_vec child_vec}
    )
  )
;; @@

;; @@
(defquery test-hierarchical-moe-sampler [n p lambda alpha mu-mu mu-sigma factor-mu factor-sigma]
  (let [x (hierarchical-moe-sampler 
            {:n n
             :p p
             :lambda lambda
             :alpha alpha
             :mu-mu mu-mu
             :mu-sigma mu-sigma
             :factor-mu factor-mu
             :factor-sigma factor-sigma})]
    {:x x}
    )
  )
;; @@

;; @@
(def sample (doquery :lmh test-hierarchical-moe-sampler 5 0.8 3 1.5 0 1 1 1))
;; @@
