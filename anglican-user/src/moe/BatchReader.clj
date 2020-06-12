;; gorilla-repl.fileformat = 1

;; **
;;; # Batch Reader
;;; 
;;; This is the clojure file that contains function to read data from the CIFAR 10 images. The version for C is used. Please add some comments and clean it up nicely. 
;; **

;; @@
(ns moe.batchreader
  (:require [gorilla-plot.core :as plot]
            [mikera.image.core]
            [mikera.image.colours]
            [byte-streams]
            ))
(use 'nstools.ns)
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
(defn im-to-mx [image]
  (map (fn [x] (- (/ x 127.5) 1)) image))
;; @@

;; @@
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
          immx [];vectors are easy to concat on back
          pixels (mikera.image.core/get-pixels image)]
        	;(print "hi")
            (loop [i 0 j 0]
              (if (= j 32)
                (do
                  (mikera.image.core/show image) (println im-to-mx immx) (println immx)
                  image ;Loop done. return image
                  )
                (if (= i 32)
                  (recur 0 (+ j 1))
                  (do
                    (mikera.image.core/set-pixel image i j (mikera.image.colours/rgb-from-components
                                                             (sb2ub (nth firstarray (+ i (* 32 j))))
                                                             (sb2ub (nth firstarray (+ 1024 (+ i (* 32 j)))))
                                                             (sb2ub (nth firstarray (+ 2048 (+ i (* 32 j)))))))
                    (conj immx (/ (reduce + 0.0 (list (sb2ub (nth firstarray (+ i (* 32 j))))
                                                      (sb2ub (nth firstarray (+ 1024 (+ i (* 32 j)))))
                                                      (sb2ub (nth firstarray (+ 2048 (+ i (* 32 j))))))) 3)) 
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

;; @@
(defn for-images [file-name iter-num do-fun]
  "Read file-name, to 32*32 images, and for each image, apply do-fun. It will perform do-fun for n images."
  (let [f (java.io.File. file-name)
		  st (byte-streams/to-byte-array f)]
    (loop [chunk (partition 3073 st) n iter-num]
      (let [ch (first chunk)
            firstarray (next ch)
            image (mikera.image.core/new-image 32 32)
            pixels (mikera.image.core/get-pixels image)]
        	(loop [i 0 j 0]
              (if (= j 32)
                (do-fun image)
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

;; @@
(defn for-images-deer [file-name iter-num do-fun]
  "Read file-name, to 32*32 images, and for each image, apply do-fun. It will perform do-fun for n images."
  (let [f (java.io.File. file-name)
		  st (byte-streams/to-byte-array f)]
    (loop [chunk (partition 3073 st) n iter-num]
      (let [ch (first chunk)
            deerclassifier (first ch)
            firstarray (next ch)
            image (mikera.image.core/new-image 32 32)
            pixels (mikera.image.core/get-pixels image)]
        (if (not= deerclassifier 4) (recur (next chunk) n)
          (do 
            (loop [i 0 j 0]
              (if (= j 32)
                (do-fun image)
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
      )
    )
;; @@
