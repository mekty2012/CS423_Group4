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
;; @@

;; @@
(defm pi-sampler [hyperparameters]
  (let [n (:n hyperparameters)
        alpha (:alpha hyperparameters)]
  (sample (dirichlet (repeat n alpha)))
    )
  )

(defm mu-sampler [hyperparameters]
  (let [n (:n hyperparameters)
        mu-mu (:mu-mu hyperparameters)
        mu-sigma (:mu-sigma hyperparameters)]
  (repeatedly n (fn [] (sample (normal mu-mu mu-sigma)))
  ))
  )

(defm factor-sampler [hyperparameters]
  (let [n (:n hyperparameters)
        factor-mu (:factor-mu hyperparameters)
        factor-sigma (:factor-sigma hyperparameters)
        ]
    (repeatedly n (fn [] (repeatedly n (fn [] (sample (normal factor-mu factor-sigma))))))
    )
)
  

(defm gmm-sampler [hyperparameters]
  (let [n (:n hyperparameters)
        lambda (:lambda hyperparameters)
        alpha (:alpha hyperparameters)
        mu-mu (:mu-mu hyperparameters)
        mu-sigma (:mu-sigma hyperparameters)
        factor-mu (:factor-mu hyperparameters)
        factor-sigma (:factor-sigma hyperparameters)
        num_cluster (cluster-num-sampler lambda)
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
(defquery test-pi-sampler [n alpha]
  (pi-sampler {:n n :alpha alpha})
  )

(def sample-test-pi)
;; @@

;; @@
; Since kernel here, is similar to CNN's single layer, I suggest random initialization method
; of CNN layer as probabilistic model. Note that this is not that reasonable. 
; The derivation of CNN layer initialization is based on signal preservation, where probabilistic programming
; does not contain any kind of signal propagation.
(defn xavier [n] (sqrt (/ 6 (+ (* n n) 1))))

(with-primitive-procedures [xavier]
(defm kernel-sampler [n]
  (repeatedly n (fn [] (repeatedly n (fn [] (sample (uniform-continuous (- (xavier n)) (xavier n)))))))
  )
  )
;; @@

;; @@
(defm single-moe-sampler [hyperparameters]
  (let [n (:n hyperparameters)
        lambda (:lambda hyperparameters)
        alpha (:alpha hyperparameters)
        mu-mu (:mu-mu hyperparameters)
        mu-sigma (:mu-sigma hyperparameters)
        factor-mu (:factor-mu hyperparameters)
        factor-sigma (:factor-sigma hyperparameters)
        num_cluster (cluster-num-sampler lambda)
        pi (pi-sampler {:n num_cluster :alpha alpha})
        mu_vec (repeatedly num_cluster (fn [] (mu-sampler {:n n :mu-mu mu-mu :mu-sigma mu-sigma})))
        factor_vec (repeatedly num_cluster (fn [] (factor-sampler {:n n :factor-mu factor-mu :factor-sigma factor-sigma})))
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
        
        pi (pi-sampler {:n num_cluster :alpha alpha})
        
        mu_vec (repeatedly num_cluster 
                           (fn [] (mu-sampler {:n n :mu-mu mu-mu :mu-sigma mu-sigma})))
        
        factor_vec (repeatedly num_cluster 
                               (fn [] (factor-sampler {:n n :factor-mu factor-mu :factor-sigma factor-sigma})))
        
        ischild_vec (repeatedly num_cluster 
                                (fn [] (sample (bernoulli p))))
        
        child_vec (map (fn [i] 
                         (if (= i 1) 
                           (hierarchical-moe-sampler hyperparameters)
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
(defquery test-single-moe-sampler [n lambda alpha mu-mu mu-sigma factor-mu factor-sigma]
	(single-moe-sampler {:n n :lambda lambda :alpha alpha :mu-mu mu-mu :mu-sigma mu-sigma :factor-mu factor-mu :factor-sigma factor-sigma})
  )
;; @@

;; @@
(def sample-sing (doquery :lmh test-single-moe-sampler [5 3 1.5 0 1 1 1]))

(def results-sing (take 10 sample-sing))


(print results-sing)
;; @@

;; @@
(defquery test-hierarchical-moe-sampler [n p lambda alpha mu-mu mu-sigma factor-mu factor-sigma]
  (hierarchical-moe-sampler {:n n :p p :lambda lambda :alpha alpha :mu-mu mu-mu :mu-sigma mu-sigma :factor-mu factor-mu :factor-sigma factor-sigma})
  )
;; @@

;; @@
(def sample-hier (doquery :lmh test-hierarchical-moe-sampler [5 0.5 3 1.5 0 1 1 1]))

(def results-hier (take 10 sample-hier))


(print results-hier)
;; @@

;; @@

;; @@