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
(ns moe.samplers
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]))
(use 'nstools.ns)
(ns+ templete
  (:like anglican-user.worksheet))
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[nil,nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[nil,nil],nil]"}
;; <=

;; @@
(defm cluster-num-sampler [lambda]
  (sample (poisson lambda))
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;templete/cluster-num-sampler</span>","value":"#'templete/cluster-num-sampler"}
;; <=

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
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;templete/pi-sampler</span>","value":"#'templete/pi-sampler"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/mu-sampler</span>","value":"#'templete/mu-sampler"}],"value":"[#'templete/pi-sampler,#'templete/mu-sampler]"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/factor-sampler</span>","value":"#'templete/factor-sampler"}],"value":"[[#'templete/pi-sampler,#'templete/mu-sampler],#'templete/factor-sampler]"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/gmm-sampler</span>","value":"#'templete/gmm-sampler"}],"value":"[[[#'templete/pi-sampler,#'templete/mu-sampler],#'templete/factor-sampler],#'templete/gmm-sampler]"}
;; <=

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
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;templete/xavier</span>","value":"#'templete/xavier"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/kernel-sampler</span>","value":"#'templete/kernel-sampler"}],"value":"[#'templete/xavier,#'templete/kernel-sampler]"}
;; <=

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
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;templete/single-moe-sampler</span>","value":"#'templete/single-moe-sampler"}
;; <=

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
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;templete/hierarchical-moe-sampler</span>","value":"#'templete/hierarchical-moe-sampler"}
;; <=

;; @@

;; @@
