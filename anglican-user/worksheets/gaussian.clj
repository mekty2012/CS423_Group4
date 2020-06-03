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
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;templete/eval-multi-variable-normal</span>","value":"#'templete/eval-multi-variable-normal"}
;; <=

;; @@
; Test required
;Implementation of multivariate normal, where given input is not covariance matrix, but multiplier matrix.
(defdist factor-mvn [n mean-vec factor-mat] ; Distribution is factor-mat * ((repeatedly n N(0, 1)) + mean-vec)
  [inv-factor-mat (clojure.core.matrix/inverse factor-mat)] ; Internal variable that precomputes inverse of factor-mat
  (sample* [this]
           (clojure.core.matrix/mmul
             factor-mat 
             (clojure.core.matrix/add 
               (repeatedly n (fn [] (sample* (normal 0 1)))) mean-vec)
             ))
  (observe* [this value] 
            (reduce + (map (fn [t] (observe* (normal 0 1) t)) 
                           (clojure.core.matrix/mmul 
                             inv-factor-mat (clojure.core.matrix/sub value mean-vec))
                           )))
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>#multifn[print-method 0x5f90c73c]</span>","value":"#multifn[print-method 0x5f90c73c]"}
;; <=

;; @@
(defn identity-matrix [n]
  (clojure.core.matrix/identity-matrix n)
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;templete/identity-matrix</span>","value":"#'templete/identity-matrix"}
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
                  log-prob-vec (map + gaussian-log-prob-vec pi)
                  sum-prob (reduce + (map (fn [l] (Math/exp l)) log-prob-vec))]
              (- (nth log-prob-vec label) (Math/log sum-prob))
              )))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>#multifn[print-method 0x5f90c73c]</span>","value":"#multifn[print-method 0x5f90c73c]"}
;; <=

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
    {:x x}
    ))
)

;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;templete/test-factor-mvn-sample</span>","value":"#'templete/test-factor-mvn-sample"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/test-factor-mvn-observe</span>","value":"#'templete/test-factor-mvn-observe"}],"value":"[#'templete/test-factor-mvn-sample,#'templete/test-factor-mvn-observe]"}
;; <=

;; @@
(def samples-1 (doquery :lmh test-factor-mvn-sample nil))

(def results-1 (map get-result (take 10 (drop 10 samples-1))))

(def samples-2 (doquery :lmh test-factor-mvn-observe nil))

(def results-2  (take 10 (drop 10 samples-2)))

(println results-2)
;; @@
;; ->
;;; ({:log-weight 0.0, :result {:x 4.0440147815357586}, :predicts []} {:log-weight 0.0, :result {:x 4.0440147815357586}, :predicts []} {:log-weight 0.0, :result {:x 4.0440147815357586}, :predicts []} {:log-weight 0.0, :result {:x 4.0440147815357586}, :predicts []} {:log-weight 0.0, :result {:x 4.0440147815357586}, :predicts []} {:log-weight 0.0, :result {:x 3.003805593714239}, :predicts []} {:log-weight 0.0, :result {:x 3.003805593714239}, :predicts []} {:log-weight 0.0, :result {:x 3.003805593714239}, :predicts []} {:log-weight 0.0, :result {:x 2.5386209911852}, :predicts []} {:log-weight 0.0, :result {:x 2.5386209911852}, :predicts []})
;;; 
;; <-
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;templete/samples-1</span>","value":"#'templete/samples-1"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/results-1</span>","value":"#'templete/results-1"}],"value":"[#'templete/samples-1,#'templete/results-1]"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/samples-2</span>","value":"#'templete/samples-2"}],"value":"[[#'templete/samples-1,#'templete/results-1],#'templete/samples-2]"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/results-2</span>","value":"#'templete/results-2"}],"value":"[[[#'templete/samples-1,#'templete/results-1],#'templete/samples-2],#'templete/results-2]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[[#'templete/samples-1,#'templete/results-1],#'templete/samples-2],#'templete/results-2],nil]"}
;; <=

;; @@
(with-primitive-procedures [factor-gmm identity-matrix]
(defquery test-factor-gmm-sample []
  (let [x (sample 
            (factor-gmm 
              5 2
              (list [1 1 1 1 1] [7 7 7 7 7]) 
              (list [[1 2 3 4 5] [1 1 1 1 1] [3 4 5 6 7] [8 3 4 5 1] [3 4 5 6.2 1]] (identity-matrix 5))))]
      {:x x}
    )
))
(with-primitive-procedures [factor-gmm identity-matrix]
(defquery test-factor-gmm-observe []
  (let [x (sample (normal 5 1))
        y (sample (normal 7 2))]
      (observe (factor-gmm 
                 5 2 
                 (list [x (+ x 1) x x x] [y y y y y]) 
                 (list [[1 2 3 4 5] [1 1 1 1 1] [3 4 5 6 7] [8 3 4 5 1] [3 4 5 6.2 1]] identity-matrix 5)) 
               (list 3 [3 3 3 3 3]))
    {:x x :y y}
    ))
)
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;templete/test-factor-gmm-sample</span>","value":"#'templete/test-factor-gmm-sample"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/test-factor-gmm-observe</span>","value":"#'templete/test-factor-gmm-observe"}],"value":"[#'templete/test-factor-gmm-sample,#'templete/test-factor-gmm-observe]"}
;; <=

;; @@
(def samples-1 (doquery :lmh test-factor-gmm-sample nil))

(def results-1  (take 10 (drop 10 samples-1)))

(println results-1)
;; @@
;; ->
;;; ({:log-weight 0.0, :result {:x [21.24918699407977 6.908074614002376 35.065336222084525 34.411386702312605 25.519007896904423]}, :predicts []} {:log-weight 0.0, :result {:x [21.25213322765143 7.457005797143738 36.166144821938914 34.04902259892865 24.052645256605977]}, :predicts []} {:log-weight 0.0, :result {:x [19.13027901934386 7.886202076206212 34.90268317175628 38.39067849385132 35.83831505477605]}, :predicts []} {:log-weight 0.0, :result {:x [17.558247537108024 5.660082713261296 28.87841296363062 18.323946626847754 21.14419956079741]}, :predicts []} {:log-weight 0.0, :result {:x [6.400079022810495 2.953822125781674 12.307723274373842 18.297899188502214 8.272799055468372]}, :predicts []} {:log-weight 0.0, :result {:x [10.85722832841914 4.220259564017651 19.297747456454445 24.13255492690621 25.72918891214708]}, :predicts []} {:log-weight 0.0, :result {:x [20.601929371489447 6.2157682830139835 33.03346593751741 21.09955024109423 16.118107697965094]}, :predicts []} {:log-weight 0.0, :result {:x [13.356825732589447 3.558857454488184 20.474540641565817 11.340077931189029 16.918419419607876]}, :predicts []} {:log-weight 0.0, :result {:x [14.798997129753841 4.365251831130264 23.529500792014368 12.350158227350859 12.40113859060784]}, :predicts []} {:log-weight 0.0, :result {:x [6.9973393476306445 4.146412011510234 15.290163370651115 21.832511026711323 13.891877230357217]}, :predicts []})
;;; 
;; <-
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;templete/results-1</span>","value":"#'templete/results-1"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[#'templete/results-1,nil]"}
;; <=

;; @@
(defn eval-gaussian-mixture [x pi mu-vec sigma-vec]
  "This function returns a vector that contains P(z_i = 1|mu, sigma)."
  (map 
    (fn [i] (* (nth pi i) (eval-multi-variable-normal x (nth mu-vec i) (nth sigma-vec i)))) 
    (range 0 (count pi)))
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;templete/eval-gaussian-mixture</span>","value":"#'templete/eval-gaussian-mixture"}
;; <=

;; @@
(def evalmvn-test (eval-multi-variable-normal [1 1 1 -1 3 1 2 3 1 1] [0 0 0 0 0 0 0 0 0 0] (clojure.core.matrix/identity-matrix 10)))

(print evalmvn-test)
;; @@
;; ->
;;; -23.689385332046726
;; <-
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;templete/evalmvn-test</span>","value":"#'templete/evalmvn-test"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[#'templete/evalmvn-test,nil]"}
;; <=

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
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;templete/row-mean</span>","value":"#'templete/row-mean"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/invert</span>","value":"#'templete/invert"}],"value":"[#'templete/row-mean,#'templete/invert]"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/shape</span>","value":"#'templete/shape"}],"value":"[[#'templete/row-mean,#'templete/invert],#'templete/shape]"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/get-row</span>","value":"#'templete/get-row"}],"value":"[[[#'templete/row-mean,#'templete/invert],#'templete/shape],#'templete/get-row]"}
;; <=

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
