;; gorilla-repl.fileformat = 1

;; **
;;; #MoE-test
;;; This is for testing functions of moe.moe
;; **

;; @@
(ns moe-test
  (:require [gorilla-plot.core :as plot]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount add]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]
            [moe.gaussian :refer :all]
            [moe.batchreader :refer :all]
            [moe.preprocess :refer :all]
            [moe.samplers :refer :all]
            [moe.preprocess :refer :all]))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(ns+ template
  (:like anglican-user.worksheet))
;; @@
