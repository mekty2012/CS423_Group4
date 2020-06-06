;; gorilla-repl.fileformat = 1

;; **
;;; #MoE
;;; This is MoE
;; **

;; @@
(ns moe.moe
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]
            [moe.gaussian :refer :all]
            [moe.BatchReader :refer :all] ; filename과 ns name이 달라서?
            [moe.preprocess :refer :all] ; 얘는 뭐여
            ))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(require 'byte-streams)
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
; import not working?
(defn for-nbox [image n nbox-fun]
  (let [dropped (moe.preprocess/dropoutted image 0.3)]
    (loop [x 0 y 0]
      (if (= y 32)
        nil
        (if (= x 32)
          (recur 0 (inc y))
          (do
            (let [box (moe.preprocess/nbox image n x y)
                  box-vector (moe.preprocess/im2vec box)]
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
