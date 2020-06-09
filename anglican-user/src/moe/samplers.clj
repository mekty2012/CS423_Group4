;; gorilla-repl.fileformat = 1

;; **
;;; # Samplers
;;; 
;;; This is the clojure file that contains functions needed to sample the hyperparameters
;; **

;; @@
(ns moe.samplers
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]))
(use 'nstools.ns)
(ns+ template
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
; Since kernel here, is similar to CNN's single layer, I suggest random initialization method
; of CNN layer as probabilistic model. Note that this is not that reasonable. 
; The derivation of CNN layer initialization is based on signal preservation, where probabilistic programming
; does not contain any kind of signal propagation.
(defn xavier [n] (sqrt (/ 6 (+ (* n n) 1))))

(with-primitive-procedures [xavier]
(defm kernel-sampler [n]
  (repeatedly n (fn [] (sample (uniform-continuous (- (xavier n)) (xavier n)))))
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

;; @@
