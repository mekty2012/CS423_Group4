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
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
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
(Example)
;; @@

;; @@

;; @@
