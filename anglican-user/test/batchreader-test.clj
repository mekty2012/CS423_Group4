;; gorilla-repl.fileformat = 1

;; **
;;; # Batchreader Test
;;; 
;;; Testing for BatchReader.clj file.
;; **

;; @@
(ns batchreader-test
  (:require [gorilla-plot.core]
            [clojure.core.matrix
             :refer [matrix diagonal-matrix to-nested-vectors div ecount]]
            [clojure.core.matrix.operators]
            [clojure.core.matrix.linear]
            [moe.batchreader :refer :all] 
            ))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(require 'byte-streams)
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
(for-images "data/cifar-10-batches-bin/data_batch_1.bin" 2 (fn [im] (mikera.image.core/show im)))
;; @@

;; @@
(def image (mikera.image.core/new-image 32 32))

(loop [i 0 j 0]
  (if (= j 32)
      image
      (if (= i 32)
        (recur 0 (+ j 1))
        (do 
          (mikera.image.core/set-pixel image i j (mikera.image.colours/rgb-from-components 255 (+ 7 (* 8 i)) (+ 7 (* 8 j))))
          (recur (+ i 1) j)
          )
        )
    )
  )

(mikera.image.core/show image)
;; @@

;; @@
(def f (java.io.File. "data/cifar-10-batches-bin/data_batch_1.bin"))

(def st (byte-streams/to-input-stream f))

(def ch (take 1 (byte-streams/convert st (byte-streams/seq-of java.nio.ByteBuffer) {:chunk-size 1024})))

(byte-streams/print-bytes ch)
;; @@

;; @@

;; @@
