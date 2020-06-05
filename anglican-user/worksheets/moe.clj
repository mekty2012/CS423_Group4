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
(defquery Moe [image_name])
;; @@
