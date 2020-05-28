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
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[nil,nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[nil,nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[nil,nil],nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[[nil,nil],nil],nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[[[nil,nil],nil],nil],nil],nil]"}
;; <=

;; @@
; This implementation is wrong. We need to modify definition of firstarray so that it uses byte array.
; Current implementation is using char array, where 1 char may not be single byte.
(defn sb2ub [b]
  (if (< b 0)
    (+ 256 b)
    b
    )
  )
(defn Example [num]
  (let [f (java.io.File. "data/cifar-10-batches-bin/data_batch_1.bin")
		  st (byte-streams/to-byte-array f)]
    (loop [chunk (partition 3073 st) n num]
      (let [ch (first chunk)
          firstarray (next ch)
          image (mikera.image.core/new-image 32 32)
          pixels (mikera.image.core/get-pixels image)]
        	(print "hi")
            (loop [i 0 j 0]
              (if (= j 32)
                (do
                  (mikera.image.core/show image)
                  image
                  )
                (if (= i 32)
                  (recur 0 (+ j 1))
                  (do
                    (mikera.image.core/set-pixel image i j (mikera.image.colours/rgb-from-components 
                                                             (sb2ub (nth firstarray (+ i (* 32 j)))) 
                                                             (sb2ub (nth firstarray (+ 1024 (+ i (* 32 j))))) 
                                                             (sb2ub (nth firstarray (+ 2048 (+ i (* 32 j)))))))
                    (recur (+ i 1) j)
                    )
                  )
                )
              ) 
              (when (> n 1) (recur (next chunk) (- n 1)))
        )
    )
  )
)
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/sb2ub</span>","value":"#'template/sb2ub"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/Example</span>","value":"#'template/Example"}],"value":"[#'template/sb2ub,#'template/Example]"}
;; <=

;; @@
(Example [2])
;; @@
;; ->
;;; hi
;; <-

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
