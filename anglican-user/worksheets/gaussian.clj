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

;; @@
(defn identity-matrix [n]
  (clojure.core.matrix/identity-matrix n)
  )
;; @@

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
                  indexed-vec (range 0 n)
                  gaussian-log-prob-vec (map (fn [i]
                                           (let [mu (nth mu-vec i)
                                                 inv-factor (nth inverse-factor-vec i)]
                                             (reduce +
                                                     (map
                                                       (fn [t]  (observe* (normal 0 1) (first (first t))))
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

;; @@
(with-primitive-procedures [factor-mvn]
(defquery test-factor-mvn-sample []
  (let [x (sample (factor-mvn 5 [1 1 1 1 1] [[1 2 3 4 5] [1 1 1 1 1] [3 4 5 6 7] [8 3 4 5 1] [3 4 5 6 1]]))]
      {:x x}
    ))
)
(with-primitive-procedures [factor-mvn identity-matrix]
(defquery test-factor-mvn-observe []
  (let [x (sample (normal 5 1))]
    (observe (factor-mvn 5 [x x x x x] (identity-matrix 5)) [3 3 3 3 3])
    (observe (factor-mvn 5 [x (+ x 1) x x x] (identity-matrix 5)) [3 5 3 3 3])
    (observe (factor-mvn 5 [x x x x x] (identity-matrix 5)) [3 3 3 3 3])
    (observe (factor-mvn 5 [x x x (+ x 2) x] (identity-matrix 5)) [3 3 3 6 3])
    {:x x}
    ))
)

;; @@

;; @@
(def samples-1 (doquery :lmh test-factor-mvn-sample nil))

(def results-1 (map get-result (take 10 (drop 10 samples-1))))

(def samples-2 (doquery :lmh test-factor-mvn-observe nil))

(def results-2  (take 10 (drop 10 samples-2)))

(println results-2)
;; @@

;; @@
(with-primitive-procedures [factor-gmm identity-matrix]
(defquery test-factor-gmm-sample []
  (let [x (sample
            (factor-gmm
              5 (list 0.3 0.7)
              (list [1 1 1 1 1] [7 7 7 7 7])
              (list [[1 2 3 4 5] [1 1 1 1 1] [3 4 5 6 7] [8 3 4 5 1] [3 4 5 6.2 1]]
                    (identity-matrix 5))
              )
            )]
      {:x x}
    )
))
(with-primitive-procedures [factor-gmm identity-matrix]
(defquery test-factor-gmm-observe []
  (let [x (sample (normal 3 2))
        y (sample (normal 7 2))]
      (observe (factor-gmm
                 5 (list 0.3 0.7)
                 (list [x y x x y]
                       [y x y x y])
                 (list [[1 2 3 4 5] [1 1 1 1 1] [3 4 5 6 7] [8 3 4 5 1] [3 4 5 6.2 1]]
                       (identity-matrix 5))
                 )
               (list 3 [3 3 3 3 3]))
    {:x x :y y}
    ))
)
;; @@

;; @@
(def samples-2 (doquery :lmh test-factor-gmm-observe nil))

(def results-2  (take 100 (drop 10000 samples-2)))

(println results-2)
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

(defn shape [object]
  (clojure.core.matrix/shape object)
  )

(defn get-row [matrix]
  (clojure.core.matrix/get-row matrix)
  )

;; @@

;; @@
(with-primitive-procedures [row-mean invert shape identity-matrix get-row]
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
                          (sample (eval-multi-variable-normal mu-0
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
            (observe (eval-multi-variable-normal (get mu k) (get sigma k)) row)
            (recur (inc n) (conj z k))))))))
;; @@

;; @@

;; @@
