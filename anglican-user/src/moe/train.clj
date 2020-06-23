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
  {:n 25
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

(def single-hyperparameter-1 
  {:n 25
   :lambda 5
   :alpha 1
   :mu-mu 0
   :mu-sigma 1
   :factor-mu 0
   :factor-sigma 1
   :is-single true
   :auto-tune false
   :feeder "prob" ; best, prob, weight
    })

(def single-hyperparameter-2
  {:n 25
   :lambda 5
   :alpha 1
   :mu-mu 0
   :mu-sigma 1
   :factor-mu 0
   :factor-sigma 1
   :is-single true
   :auto-tune false
   :feeder "weight" ; best, prob, weight
    })

; p should be lesser then 1/lambda. 
(def hierarchical-hyperparameter
  {:n 25
   :lambda 5
   :p 0.01
   :alpha 1
   :mu-mu 0
   :mu-sigma 1
   :factor-mu 0
   :factor-sigma 1
   :is-single false
   :auto-tune false
   :feeder "best"})

(def hierarchical-hyperparameter-1
  {:n 25
   :lambda 5
   :p 0.01
   :alpha 1
   :mu-mu 0
   :mu-sigma 1
   :factor-mu 0
   :factor-sigma 1
   :is-single false
   :auto-tune false
   :feeder "prob"})

(def hierarchical-hyperparameter-2
  {:n 25
   :lambda 5
   :p 0.01
   :alpha 1
   :mu-mu 0
   :mu-sigma 1
   :factor-mu 0
   :factor-sigma 1
   :is-single false
   :auto-tune false
   :feeder "weight"})

(def autotune-single-hyperparameter
  {:n 25
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
   :feeder "best"
   })

(def autotune-single-hyperparameter-1
  {:n 25
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

(def autotune-single-hyperparameter-2
  {:n 25
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
   :feeder "weight"
   })

(def autotune-hierarchical-hyperparameter
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

(def autotune-hierarchical-hyperparameter-1
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
   :feeder "prob"
   }
  )

(def autotune-hierarchical-hyperparameter-2
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
   :feeder "weight"
   }
  )
;; @@

;; @@
(defn save-result [file-name result]
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

;; @@
(defn save-string [file-name string]
  (with-open [w (clojure.java.io/writer file-name :append true)]
    (do
    (.write w (str string))
    (.write w (str "\n"))
      )
    )
  )
;; @@

;; @@
(def file-name "data/result-time.txt")

(def file-temp "temp.txt")
;; @@

;; @@
(println "Algorithm: smc")
(println "iter num: 100")
(println "Box size: 2")
(println "Feed type: 'auto-single-best'")

(def sample (doquery :smc train-fast  ["data/cifar-10-batches-bin/data_batch_1.bin" 100 0.2 2 autotune-single-hyperparameter-2] :number-of-particles 100))

(def result (map last (take 10 (partition 100 sample))))
                 
(save-result "data/models/result_auto_single_weight_smc.txt" result)
;; @@

;; @@

;; @@
