;; gorilla-repl.fileformat = 1

;; **
;;; #MoE
;;; This is MoE
;; **

;; @@
(ns mixture_of_experts
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]
            [gmm-generator :refer :all]
            [batch-reader :refer :all] ; filename과 ns name이 달라서?
            [pre-process :refer :all] ; 얘는 뭐여
            ))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(require 'byte-streams)
(ns+ templete
  (:like anglican-user.worksheet))
;; @@

;; @@
; import not working?
(defn for-nbox [image n nbox-fun]
  (let [dropped (pre-process/dropoutted image 0.3)]
    (loop [x 0 y 0]
      (if (= y 32)
        nil
        (if (= x 32)
          (recur 0 (inc y))
          (do
            (let [box (pre-process/nbox image n x y)
                  box-vector (pre-process/im2vec box)]
              (nbox-fun box-vector))
            (recur (inc x) y)
            )
          )
        )
    ))
  )
;; @@

;; @@

;; @@