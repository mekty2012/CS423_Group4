;; gorilla-repl.fileformat = 1

;; **
;;; #Testing for gaussian
;;; 
;;; This is the clojure file that tests the gaussian.clj functions
;; **

;; @@
(ns gaussian-test
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]
            [moe.gaussian :refer :all]
            ))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(require 'byte-streams)
(ns+ templete
  (:like anglican-user.worksheet))
;; @@

;; @@
(with-primitive-procedures [factor-mvn]
(defquery test-factor-mvn-sample []
  (let [x (sample (factor-mvn 5 [1 1 1 1 1] [[1 2 3 4 5] [1 1 1 1 1] [3 4 5 6 7] [8 3 4 5 1] [3 4 5 6 1]]))]
      {:x x}
    ))
)

;; @@

;; @@
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
               (list 0 [3 3 3 3 3]))
    {:x x :y y}
    ))
)
;; @@

;; @@
(def samples-2 (doquery :lmh test-factor-gmm-observe nil))

(def results-2  (map :result (take 100 (drop 10000 samples-2))))

(println results-2)
;; @@

;; @@
(def evalmvn-test (eval-multi-variable-normal [1 1 1 -1 3 1 2 3 1 1] [0 0 0 0 0 0 0 0 0 0] (clojure.core.matrix/identity-matrix 10)))

(print evalmvn-test)
;; @@
