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
(ns batchreader
  (:require [gorilla-plot.core :as plot]))
(use 'nstools.ns)
(require 'mikera.image.core)
(require 'mikera.image.colours)
(require 'byte-streams)
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
; This implementation is wrong. We need to modify definition of firstarray so that it uses byte array.
; Current implementation is using char array, where 1 char may not be single byte.

(defn Example []
  (with-open [rdr (clojure.java.io/reader "worksheets/CS423_Group4/cifar-10-batches-bin/data_batch_1.bin")]
    (let [firstarray (take-nth 3072 (next (mapcat seq (line-seq rdr))))
          image (mikera.image.core/new-image 32 32)
          pixels (mikera.image.core/get-pixels image)]
      (loop [i 0 j 0]
        (if (= j 32)
          image
          (if (= i 32)
            (recur 0 (+ j 1))
            (mikera.image.core/set-pixel image i j (mikera.image.colours/rgb-from-components (byte (nth firstarray (+ i (* 32 j)))) (byte (nth firstarray (+ 1024 (+ i (* 32 j))))) (byte (nth firstarray (+ 2048 (+ i (* 32 j)))))))
            )
          )
        )
      )
    )
  )
;; @@

;; @@
(def image (mikera.image.core/new-image 32 32))
;; @@

;; @@
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
