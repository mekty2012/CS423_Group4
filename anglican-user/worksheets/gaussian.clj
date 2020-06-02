;; gorilla-repl.fileformat = 1

;; **
;;; # GMM
;;; 
;;; This is GMM model. The multi variable normal function is made brand new, and the GMM query is taken from the anglican homepage
;; **

;; @@
(ns gaussian-mixture
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
(defn eval-multi-variable-normal [x mu sigma]
  (let [d (clojure.core.matrix/row-count mu)
        factor (Math/pow (* (Math/pow (* 2 Math/PI) d) (clojure.core.matrix/det sigma)) -0.5)
        x-minus-mu (clojure.core.matrix/sub x mu)
        inverse (clojure.core.matrix/inverse sigma)
        exponent (clojure.core.matrix/mmul (clojure.core.matrix/transpose x-minus-mu) inverse x-minus-mu)]
    	(* factor (Math/exp (* -0.5 exponent)))
    )
  )
;; @@

;; @@
; Test required
(defdist factor-mvn
  "Implementation of multivariate normal, where given input is not covariance matrix, but multiplier matrix. "
  [n mean-vec factor-mat] ; Distribution is factor-mat * ((repeatedly n N(0, 1)) + mean-vec)
  [inv-factor-mat (clojure.core.matrix/inverse factor-mat)] ; Internal variable that precomputes inverse of factor-mat
  ; I think this pre-computation is trade off between computation time and numerical accuracy. Maybe linalg/solve uses
  ; uses smarter algorithm solving Ax=b. I wish anyone of us has taken MAS364...
  (sample* [this] (clojure.core.matrix/mmul factor-mat (clojure.core.matrix/add (repeatedly n (sample* (normal 0 1))) mean-vec)))
  (observe* [this value] (reduce + (map (fn [t] (observe* (normal 0 1) t)) (clojure.core.matrix/mmul inv-factor-mat (clojure.core.matrix/sub value mean-vec)))))
  )
;; @@

;; @@
(defn eval-gaussian-mixture [x pi mu-vec sigma-vec]
  "This function returns a vector that contains P(z_i = 1|mu, sigma)."
  (map 
    (fn [i] (* (nth pi i) (eval-multi-variable-normal x (nth mu-vec i) (nth sigma-vec i)))) 
    (range 0 (count pi)))
  )
;; @@

;; @@
(def evalmvn-test (eval-multi-variable-normal [1 1 1 -1 3 1 2 3 1 1] [0 0 0 0 0 0 0 0 0 0] (clojure.core.matrix/identity-matrix 10)))

(print evalmvn-test)
;; @@

;; @@
(defn row-mean [data] (clojure.core.matrix.operators// (reduce clojure.core.matrix.operators/+ data) (clojure.core.matrix/row-count data)))
(defn invert
  ([W] (clojure.core.matrix.linear/solve W))
  ([kappa W] (clojure.core.matrix.linear/solve (clojure.core.matrix.operators/* kappa W))))

;; @@

;; @@
(with-primitive-procedures [row-mean invert clojure.core.matrix/shape clojure.core.matrix/identity-matrix clojure.core.matrix/get-row]
  (defm gmm [data & [hyperparams]]
    (println "provided hyperparameters:" hyperparams)
    (let [[N D] (shape data)

          ;; there are many hyperparameters; we provide defaults
          K         (:K         hyperparams 10)
          alpha     (:alpha     hyperparams 1.0)
          mu-0      (:mu-0      hyperparams (row-mean data))
          lambda-0  (:lambda-0  hyperparams (identity-matrix D))
          nu        (:nu        hyperparams (inc D))
          kappa     (:kappa     hyperparams 1.0)

          ;; sample the latent variables.
          ;;
          ;; mu and sigma are per-cluster; ideally we would 
          ;; sample them lazily
          
          pi (sample (dirichlet (repeat K alpha)))
          lambda (into [] (map (fn [x] (sample x))
                               (repeat K (wishart nu lambda-0))))
          mu (into [] (map 
                        (fn [k] 
                          (sample (multi-variable-normal mu-0 
                                       (invert kappa 
                                               (get lambda k)))))
                        (range K)))
          sigma (into [] (map invert lambda))]
      ;; for each data point, sample z[n] and `observe`
      (loop [n 0
             z []]
        (if (= n N)
          z
          (let [row (get-row data n)
                k (sample (discrete pi))]
            (observe (multi-variable-normal (get mu k) (get sigma k)) row)
            (recur (inc n) (conj z k))))))))
;; @@
