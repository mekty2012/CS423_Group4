;; gorilla-repl.fileformat = 1

;; **
;;; # Gorilla REPL
;;; 
;;; Welcome to gorilla :-)
;;; 
;;; Shift + enter evaluates code. Hit alt+g twice in quick succession or click the menu icon (upper-right corner) for more commands ...
;;; 
;;; It's a good habit to run each worksheet in its own namespace: feel free to use the declaration we've provided below if you'd like.
;; **

;; @@
(ns moe.samplers-test
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]
            [moe.samplers :refer :all]))
(use 'nstools.ns)
(ns+ templete
  (:like anglican-user.worksheet))
;; @@

;; @@
(defquery test-pi-sampler [n alpha]
  (pi-sampler {:n n :alpha alpha})
  )
;; @@

;; @@
(def sample-test-pi)
;; @@

;; @@
(defquery test-single-moe-sampler [n lambda alpha mu-mu mu-sigma factor-mu factor-sigma]
	(single-moe-sampler {:n n :lambda lambda :alpha alpha :mu-mu mu-mu :mu-sigma mu-sigma :factor-mu factor-mu :factor-sigma factor-sigma})
  )
;; @@

;; @@
(def sample-sing (doquery :lmh test-single-moe-sampler [5 3 1.5 0 1 1 1]))

(def results-sing (take 10 sample-sing))


(print results-sing)
;; @@

;; @@
(defquery test-hierarchical-moe-sampler [n p lambda alpha mu-mu mu-sigma factor-mu factor-sigma]
  (hierarchical-moe-sampler {:n n :p p :lambda lambda :alpha alpha :mu-mu mu-mu :mu-sigma mu-sigma :factor-mu factor-mu :factor-sigma factor-sigma})
  )
;; @@

;; @@
(def sample-hier (doquery :lmh test-hierarchical-moe-sampler [5 0.5 3 1.5 0 1 1 1]))

(def results-hier (take 10 sample-hier))


(print results-hier)
;; @@

;; @@

;; @@
