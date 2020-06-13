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
   :feeder "prob"
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

(def autotune-hierarchical-hyperparameter-prob
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
   :feeder "prob"
   }
  )

(def autotune-hierarchical-hyperparameter-box-2
  {:n 25
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

(def autotune-hierarchical-hyperparameter-box-1
  {:n 9
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
(defn save-result [result file-name]
  (let [length (count result)]
   (with-open [w (clojure.java.io/writer file-name :append true)]
     (loop [index 0]
       (if (= index length)
         (.write w (str "\n"))
         (do
       		(.write w (str (nth result index)))
       		(.write w (str ","))
         	(recur (+ index 1))
         )
       )
       )
     )
    )
  )
;; @@

;; **
;;; Testing for time spent
;; **

;; @@
(println "Control")
(println "Algorithm: lmh")
(println "iter num: 3")
(println "Box size: 3")
(println "Feed type: 'best'")


(print "Testing time for control. Current time is: ")
(println (now))

(def test-control (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 3 autotune-hierarchical-hyperparameter]))
;; @@

;; @@
(print "Testing time to take one element of control. Current time is: ")
(println (now))

(def results-control (take 1 test-control))

(save-result results-control "test-control.txt")

(print "Saving done. Current time is: ")
(println (now))
;; @@

;; @@
(println "Feed type change to 'prob'")

(print "Testing time for 'prob' change. Current time is: ")
(println (now))

(def test-prob (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 3 autotune-hierarchical-hyperparameter-prob]))
;; @@

;; @@
(print "Testing time to take one element of 'prob'. Current time is: ")
(println (now))

(def results-prob (take 1 test-prob))

(save-result results-prob "test-prob.txt")


(print "Saving done. Current time is: ")
(println (now))
;; @@

;; @@
(println "Box size change to 2")

(print "Testing time for box size 2. Current time is: ")
(println (now))

(def test-box-2 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-box-2]))
;; @@

;; @@
(print "Testing time to take one element of box size 2. Current time is: ")
(println (now))

(def results-box-2 (take 1 test-box-2))

(save-result results-box-2 "test-box-2.txt")


(print "Saving done. Current time is: ")
(println (now))
;; @@

;; @@
(println "Box size change to 1")

(print "Testing time for box size to 1 change. Current time is: ")
(println (now))

(def test-box-1 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 1 autotune-hierarchical-hyperparameter-box-1]))
;; @@

;; @@
(print "Testing time to take one element of box size 1. Current time is: ")
(println (now))

(def results-box-1 (take 1 test-box-1))

(save-result results-box-1 "test-box-1.txt")


(print "Saving done. Current time is: ")
(println (now))
;; @@

;; @@
(println "Iter num change to 10")

(print "Testing time for iter num 10. Current time is: ")
(println (now))

(def test-iter-num-10 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 10 0.2 3 autotune-hierarchical-hyperparameter]))
;; @@

;; @@
(print "Testing time to take one element of iter num 10. Current time is: ")
(println (now))

(def results-iter-num-10 (take 1 test-iter-num-10))

(save-result results-iter-num-10 "test-iter-num-10.txt")


(print "Saving done. Current time is: ")
(println (now))
;; @@

;; @@
(println "Algorithm change to is")

(print "Testing time for is. Current time is: ")
(println (now))

(def test-is (doquery :importance train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 3 autotune-hierarchical-hyperparameter]))
;; @@

;; @@
(print "Testing time to take one element of is. Current time is: ")
(println (now))

(def results-is (take 1 test-is))

(save-result results-is "test-is.txt")


(print "Saving done. Current time is: ")
(println (now))
;; @@

;; @@
(println "Algorithm change to bbvb")

(print "Testing time for bbvb. Current time is: ")
(println (now))

(def test-bbvb (doquery :bbvb train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 3 autotune-hierarchical-hyperparameter]))
;; @@

;; @@
(print "Testing time to take one element of bbvb. Current time is: ")
(println (now))

(def results-bbvb (take 1 test-bbvb))

(save-result results-bbvb "test-bbvb.txt")


(print "Saving done. Current time is: ")
(println (now))
;; @@

;; @@
(println "Box size change to 2 again")

(print "Testing time for box size 2. Current time is: ")
(println (now))

(def test-box-2-1 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-box-2]))
;; @@

;; @@
(print "Testing time to take one element of box size 2. Current time is: ")
(println (now))

(def results-box-2-1 (take 1 test-box-2-1))

(save-result results-box-2-1 "test-box-2-1.txt")


(print "Saving done. Current time is: ")
(println (now))
;; @@

;; @@

;; @@
