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
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[nil,nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[nil,nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[nil,nil],nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[[nil,nil],nil],nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[[[nil,nil],nil],nil],nil],nil]"}
;; <=

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
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/single-hyperparameter</span>","value":"#'template/single-hyperparameter"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/single-hyperparameter-1</span>","value":"#'template/single-hyperparameter-1"}],"value":"[#'template/single-hyperparameter,#'template/single-hyperparameter-1]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/single-hyperparameter-2</span>","value":"#'template/single-hyperparameter-2"}],"value":"[[#'template/single-hyperparameter,#'template/single-hyperparameter-1],#'template/single-hyperparameter-2]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/hierarchical-hyperparameter</span>","value":"#'template/hierarchical-hyperparameter"}],"value":"[[[#'template/single-hyperparameter,#'template/single-hyperparameter-1],#'template/single-hyperparameter-2],#'template/hierarchical-hyperparameter]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/hierarchical-hyperparameter-1</span>","value":"#'template/hierarchical-hyperparameter-1"}],"value":"[[[[#'template/single-hyperparameter,#'template/single-hyperparameter-1],#'template/single-hyperparameter-2],#'template/hierarchical-hyperparameter],#'template/hierarchical-hyperparameter-1]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/hierarchical-hyperparameter-2</span>","value":"#'template/hierarchical-hyperparameter-2"}],"value":"[[[[[#'template/single-hyperparameter,#'template/single-hyperparameter-1],#'template/single-hyperparameter-2],#'template/hierarchical-hyperparameter],#'template/hierarchical-hyperparameter-1],#'template/hierarchical-hyperparameter-2]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/autotune-single-hyperparameter</span>","value":"#'template/autotune-single-hyperparameter"}],"value":"[[[[[[#'template/single-hyperparameter,#'template/single-hyperparameter-1],#'template/single-hyperparameter-2],#'template/hierarchical-hyperparameter],#'template/hierarchical-hyperparameter-1],#'template/hierarchical-hyperparameter-2],#'template/autotune-single-hyperparameter]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/autotune-single-hyperparameter-1</span>","value":"#'template/autotune-single-hyperparameter-1"}],"value":"[[[[[[[#'template/single-hyperparameter,#'template/single-hyperparameter-1],#'template/single-hyperparameter-2],#'template/hierarchical-hyperparameter],#'template/hierarchical-hyperparameter-1],#'template/hierarchical-hyperparameter-2],#'template/autotune-single-hyperparameter],#'template/autotune-single-hyperparameter-1]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/autotune-single-hyperparameter-2</span>","value":"#'template/autotune-single-hyperparameter-2"}],"value":"[[[[[[[[#'template/single-hyperparameter,#'template/single-hyperparameter-1],#'template/single-hyperparameter-2],#'template/hierarchical-hyperparameter],#'template/hierarchical-hyperparameter-1],#'template/hierarchical-hyperparameter-2],#'template/autotune-single-hyperparameter],#'template/autotune-single-hyperparameter-1],#'template/autotune-single-hyperparameter-2]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/autotune-hierarchical-hyperparameter</span>","value":"#'template/autotune-hierarchical-hyperparameter"}],"value":"[[[[[[[[[#'template/single-hyperparameter,#'template/single-hyperparameter-1],#'template/single-hyperparameter-2],#'template/hierarchical-hyperparameter],#'template/hierarchical-hyperparameter-1],#'template/hierarchical-hyperparameter-2],#'template/autotune-single-hyperparameter],#'template/autotune-single-hyperparameter-1],#'template/autotune-single-hyperparameter-2],#'template/autotune-hierarchical-hyperparameter]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/autotune-hierarchical-hyperparameter-1</span>","value":"#'template/autotune-hierarchical-hyperparameter-1"}],"value":"[[[[[[[[[[#'template/single-hyperparameter,#'template/single-hyperparameter-1],#'template/single-hyperparameter-2],#'template/hierarchical-hyperparameter],#'template/hierarchical-hyperparameter-1],#'template/hierarchical-hyperparameter-2],#'template/autotune-single-hyperparameter],#'template/autotune-single-hyperparameter-1],#'template/autotune-single-hyperparameter-2],#'template/autotune-hierarchical-hyperparameter],#'template/autotune-hierarchical-hyperparameter-1]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/autotune-hierarchical-hyperparameter-2</span>","value":"#'template/autotune-hierarchical-hyperparameter-2"}],"value":"[[[[[[[[[[[#'template/single-hyperparameter,#'template/single-hyperparameter-1],#'template/single-hyperparameter-2],#'template/hierarchical-hyperparameter],#'template/hierarchical-hyperparameter-1],#'template/hierarchical-hyperparameter-2],#'template/autotune-single-hyperparameter],#'template/autotune-single-hyperparameter-1],#'template/autotune-single-hyperparameter-2],#'template/autotune-hierarchical-hyperparameter],#'template/autotune-hierarchical-hyperparameter-1],#'template/autotune-hierarchical-hyperparameter-2]"}
;; <=

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
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/save-result</span>","value":"#'template/save-result"}
;; <=

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
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/save-string</span>","value":"#'template/save-string"}
;; <=

;; **
;;; Testing for time spent. Box method. WARNING: takes long time.
;; **

;; @@
(def file-name "data/result-time.txt")

(def file-temp "temp.txt")
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/file-name</span>","value":"#'template/file-name"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/file-temp</span>","value":"#'template/file-temp"}],"value":"[#'template/file-name,#'template/file-temp]"}
;; <=

;; @@
(def sample (doquery :lmh train-fast  ["data/cifar-10-batches-bin/data_batch_1.bin" 5 0.2 2 autotune-single-hyperparameter-2]))

(def result (take 10 (drop 50 sample)))

(save-result "data/result_non_single_best.txt" result)
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/result</span>","value":"#'template/result"}
;; <=

;; @@
;single-best
(save-string file-name "Times for single-best. Start time is:")
(save-string file-name (now))
(save-string file-name "Query times:")

(def single-best-1 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter]))
(save-string file-name (now))

(def single-best-2 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter]))
(save-string file-name (now))

(def single-best-3 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter]))
(save-string file-name (now))

(def single-best-4 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter]))
(save-string file-name (now))

(def single-best-5 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter]))
(save-string file-name (now))

(def single-best-6 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter]))
(save-string file-name (now))

(def single-best-7 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter]))
(save-string file-name (now))

(def single-best-8 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter]))
(save-string file-name (now))

(def single-best-9 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter]))
(save-string file-name (now))

(def single-best-10 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter]))
(save-string file-name (now))

(save-string file-name "Take times:")
(save-result file-temp (take 1 single-best-1))
(save-string file-name (now))
(save-result file-temp (take 1 single-best-2))
(save-string file-name (now))
(save-result file-temp (take 1 single-best-3))
(save-string file-name (now))
(save-result file-temp (take 1 single-best-4))
(save-string file-name (now))
(save-result file-temp (take 1 single-best-5))
(save-string file-name (now))
(save-result file-temp (take 1 single-best-6))
(save-string file-name (now))
(save-result file-temp (take 1 single-best-7))
(save-string file-name (now))
(save-result file-temp (take 1 single-best-8))
(save-string file-name (now))
(save-result file-temp (take 1 single-best-9))
(save-string file-name (now))
(save-result file-temp (take 1 single-best-10))
(save-string file-name (now))
;; @@

;; @@
;single-prob
(save-string file-name "Times for single-prob. Start time is:")
(save-string file-name (now))
(save-string file-name "Query times:")

(def single-prob-1 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter-1]))
(save-string file-name (now))

(def single-prob-2 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter-1]))
(save-string file-name (now))

(def single-prob-3 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter-1]))
(save-string file-name (now))

(def single-prob-4 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter-1]))
(save-string file-name (now))

(def single-prob-5 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter-1]))

(save-string file-name (now))
(def single-prob-6 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter-1]))

(save-string file-name (now))
(def single-prob-7 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter-1]))
(save-string file-name (now))

(def single-prob-8 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter-1]))
(save-string file-name (now))

(def single-prob-9 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter-1]))
(save-string file-name (now))

(def single-prob-10 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 single-hyperparameter-1]))
(save-string file-name (now))

(save-string file-name "Take times:")
(save-result file-temp (take 1 single-prob-1))
(save-string file-name (now))
(save-result file-temp (take 1 single-prob-2))
(save-string file-name (now))
(save-result file-temp (take 1 single-prob-3))
(save-string file-name (now))
(save-result file-temp (take 1 single-prob-4))
(save-string file-name (now))
(save-result file-temp (take 1 single-prob-5))
(save-string file-name (now))
(save-result file-temp (take 1 single-prob-6))
(save-string file-name (now))
(save-result file-temp (take 1 single-prob-7))
(save-string file-name (now))
(save-result file-temp (take 1 single-prob-8))
(save-string file-name (now))
(save-result file-temp (take 1 single-prob-9))
(save-string file-name (now))
(save-result file-temp (take 1 single-prob-10))
(save-string file-name (now))
;; @@

;; @@
;hierarchical-best
(save-string file-name "Times for hierarchical-best. Start time is:")
(save-string file-name (now))
(save-string file-name "Query times:")

(def hierarchical-best-1 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter]))
(save-string file-name (now))

(def hierarchical-best-2 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter]))
(save-string file-name (now))

(def hierarchical-best-3 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter]))
(save-string file-name (now))

(def hierarchical-best-4 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter]))
(save-string file-name (now))

(def hierarchical-best-5 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter]))
(save-string file-name (now))

(def hierarchical-best-6 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter]))
(save-string file-name (now))

(def hierarchical-best-7 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter]))
(save-string file-name (now))

(def hierarchical-best-8 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter]))
(save-string file-name (now))

(def hierarchical-best-9 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter]))
(save-string file-name (now))

(def hierarchical-best-10 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter]))
(save-string file-name (now))

(save-string file-name "Take times:")
(save-result file-temp (take 1 hierarchical-best-1))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-best-2))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-best-3))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-best-4))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-best-5))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-best-6))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-best-7))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-best-8))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-best-9))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-best-10))
(save-string file-name (now))
;; @@

;; @@
;hierarchical-prob
(save-string file-name "Times for hierarchical-prob. Start time is:")
(save-string file-name (now))
(save-string file-name "Query times:")
(def hierarchical-best-1 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def hierarchical-prob-2 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def hierarchical-prob-3 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def hierarchical-prob-4 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def hierarchical-prob-5 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def hierarchical-prob-6 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def hierarchical-prob-7 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def hierarchical-prob-8 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def hierarchical-prob-9 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def hierarchical-prob-10 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 hierarchical-hyperparameter-1]))
(save-string file-name (now))

(save-string file-name "Take times:")
(save-result file-temp (take 1 hierarchical-prob-1))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-prob-2))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-prob-3))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-prob-4))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-prob-5))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-prob-6))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-prob-7))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-prob-8))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-prob-9))
(save-string file-name (now))
(save-result file-temp (take 1 hierarchical-prob-10))
(save-string file-name (now))
;; @@

;; @@
;autotuned-single-best
(save-string file-name "Times for autotuned-single-bes. Start time is:")
(save-string file-name (now))
(save-string file-name "Query times:")

(def a-single-best-1 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter]))
(save-string file-name (now))

(def a-single-best-2 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter]))
(save-string file-name (now))

(def a-single-best-3 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter]))
(save-string file-name (now))

(def a-single-best-4 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter]))
(save-string file-name (now))

(def a-single-best-5 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter]))
(save-string file-name (now))

(def a-single-best-6 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter]))
(save-string file-name (now))

(def a-single-best-7 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter]))
(save-string file-name (now))

(def a-single-best-8 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter]))
(save-string file-name (now))

(def a-single-best-9 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter]))
(save-string file-name (now))

(def a-single-best-10 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter]))
(save-string file-name (now))

(save-string file-name "Take times:")
(save-result file-temp (take 1 a-single-best-1))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-best-2))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-best-3))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-best-4))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-best-5))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-best-6))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-best-7))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-best-8))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-best-9))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-best-10))
(save-string file-name (now))
;; @@

;; @@
;autotuned-single-prob
(save-string file-name "Times for autotuned-single-prob. Start time is:")
(save-string file-name (now))
(save-string file-name "Query times:")

(def a-single-prob-1 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter-1]))
(save-string file-name (now))

(def a-single-prob-2 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter-1]))
(save-string file-name (now))

(def a-single-prob-3 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter-1]))
(save-string file-name (now))

(def a-single-prob-4 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter-1-1]))
(save-string file-name (now))

(def a-single-prob-5 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter-1]))
(save-string file-name (now))

(def a-single-prob-6 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter-1]))
(save-string file-name (now))

(def a-single-prob-7 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter-1]))
(save-string file-name (now))

(def a-single-prob-8 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter-1]))
(save-string file-name (now))

(def a-single-prob-9 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter-1]))
(save-string file-name (now))

(def a-single-prob-10 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-single-hyperparameter-1]))
(save-string file-name (now))

(save-string file-name "Take times:")
(save-result file-temp (take 1 a-single-prob-1))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-prob-2))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-prob-3))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-prob-4))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-prob-5))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-prob-6))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-prob-7))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-prob-8))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-prob-9))
(save-string file-name (now))
(save-result file-temp (take 1 a-single-prob-10))
(save-string file-name (now))
;; @@

;; @@
;autotune-hierarchical-best
(save-string file-name "Times for autotune-hierarchical-best. Start time is:")
(save-string file-name (now))
(save-string file-name "Query times:")

(def a-hierarchical-best-1 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter]))
(save-string file-name (now))

(def a-hierarchical-best-2 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter]))
(save-string file-name (now))

(def a-hierarchical-best-3 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter]))
(save-string file-name (now))

(def a-hierarchical-best-4 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter]))
(save-string file-name (now))

(def a-hierarchical-best-5 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter]))
(save-string file-name (now))

(def a-hierarchical-best-6 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter]))
(save-string file-name (now))

(def a-hierarchical-best-7 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter]))
(save-string file-name (now))

(def a-hierarchical-best-8 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter]))
(save-string file-name (now))

(def a-hierarchical-best-9 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter]))
(save-string file-name (now))

(def a-hierarchical-best-10 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter]))
(save-string file-name (now))

(save-string file-name "Take times:")
(save-result file-temp (take 1 a-hierarchical-best-1))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-best-2))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-best-3))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-best-4))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-best-5))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-best-6))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-best-7))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-best-8))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-best-9))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-best-10))
(save-string file-name (now))
;; @@

;; @@
;autotune-hierarchical-prob
(save-string file-name "Times for autotune-hierarchical-prob. Start time is:")
(save-string file-name (now))
(save-string file-name "Query times:")

(def a-hierarchical-prob-1 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def a-hierarchical-prob-2 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def a-hierarchical-prob-3 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def a-hierarchical-prob-4 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def a-hierarchical-prob-5 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def a-hierarchical-prob-6 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def a-hierarchical-prob-7 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def a-hierarchical-prob-8 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def a-hierarchical-prob-9 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-1]))
(save-string file-name (now))

(def a-hierarchical-prob-10 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 3 0.2 2 autotune-hierarchical-hyperparameter-1]))
(save-string file-name (now))

(save-string file-name "Take times:")
(save-result file-temp (take 1 a-hierarchical-prob-1))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-prob-2))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-prob-3))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-prob-4))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-prob-5))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-prob-6))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-prob-7))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-prob-8))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-prob-9))
(save-string file-name (now))
(save-result file-temp (take 1 a-hierarchical-prob-10))
(save-string file-name (now))
;; @@

;; **
;;; WARNING: TAKES LONG TIME
;; **

;; @@
1;Training
(print "Training. ")
(println "Algorithm: lmh")
(println "iter num: 50")
(println "Box size: 2")
(println "Feed type: 'prob'")


(def training (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 50 0.2 2 autotune-hierarchical-hyperparameter]))
;; @@

;; @@
(print "Saving data: ")
(println (now))

(def results (take 10 training))

(save-result results "data/results.txt")


(print "Saving done. Current time is: ")
(println (now))
;; @@

;; **
;;; Testing for time spent. Control method. WARNING: takes long time.
;; **

;; @@
(println "Control")
(println "Algorithm: lmh")
(println "iter num: 100")
(println "Box size: 3")
(println "Feed type: 'best'")


(print "Testing time for control. Current time is: ")
(println (now))

(def test-control (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 100 0.2 2 autotune-hierarchical-hyperparameter]))
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

(def test-box-2 (doquery :lmh train ["data/cifar-10-batches-bin/data_batch_1.bin" 300 0.2 2 autotune-hierarchical-hyperparameter-box-2]))
;; @@

;; @@
(print "Testing time to take one element of box size 2. Current time is: ")
(println (now))

(def results-box-2 (take 100 test-box-2))

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
