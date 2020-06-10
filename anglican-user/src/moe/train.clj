;; gorilla-repl.fileformat = 1

;; **
;;; # Training file for MoE
;;; 
;;; These are the clojure segments that is used to train and see results. Hyperparameters are also provided.
;; **

;; @@
(ns moe.train
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount add]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]
            [moe.gaussian :refer :all]
            [moe.batchreader :refer :all]
            [moe.preprocess :refer :all]
            [moe.samplers :refer :all]
            [moe.moe :refer :all]
            ))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(require 'byte-streams)
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
(def single-hyperparameter 
  {:n 49
   :lambda 5
   :alpha 1
   :mu-mu 0
   :mu-sigma 1
   :factor-mu 0
   :factor-sigma 1
   :is-single true
   :auto-tune false
   :feeder "best" ; best, prob, weight
    })

; p should be lesser then 1/lambda. 
(def hierarchical-hyperparameter
  {:n 49
   :lambda 5
   :p 0.01
   :alpha 1
   :mu-mu 0
   :mu-sigma 1
   :factor-mu 0
   :factor-sigma 1
   :is-single false
   :auto-tune false
   :feeder ""})

(def autotune-single-hyperparameter
  {:n 49
   :lambda-tune 0.2
   :alpha-tune 1
   :mu-mu-tune 1
   :mu-sigma-tune-a 5
   :mu-sigma-tune-b 1
   :factor-mu-tune 1
   :factor-sigma-tune-a 5
   :factor-sigma-tune-b 1
   :is-single true
   :auto-tune true
   :feeder ""
   })

(def autotune-hierarchical-hyperparameter
  {:n 49
   :lambda-tune 0.2
   :tune-p-a 2
   :tune-p-b 2
   :alpha-tune 1
   :mu-mu-tune 1
   :mu-sigma-tune-a 5
   :mu-sigma-tune-b 1
   :factor-mu-tune 1
   :factor-sigma-tune-a 5
   :factor-sigma-tune-b 1
   :is-single false
   :auto-tune true
   :feeder "best"
   }
  )
;; @@

;; @@
(def test-auto-hier-best (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 3 autotune-hierarchical-hyperparameter]))
;; @@

;; @@
(def results (take 1 test-single-best))
;; @@

;; @@
results
;; @@
