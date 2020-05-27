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
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[nil,nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[nil,nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[nil,nil],nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[[nil,nil],nil],nil],nil]"}
;; <=

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
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/image</span>","value":"#'template/image"}
;; <=

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
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-unkown'>#object[java.awt.image.BufferedImage 0x1cd4a633 &quot;BufferedImage@1cd4a633: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0&quot;]</span>","value":"#object[java.awt.image.BufferedImage 0x1cd4a633 \"BufferedImage@1cd4a633: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0\"]"},{"type":"html","content":"<span class='clj-unkown'>#object[javax.swing.JFrame 0xe792705 &quot;javax.swing.JFrame[frame0,0,0,136x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,8,31,120x93,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]&quot;]</span>","value":"#object[javax.swing.JFrame 0xe792705 \"javax.swing.JFrame[frame0,0,0,136x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,8,31,120x93,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]"}],"value":"[#object[java.awt.image.BufferedImage 0x1cd4a633 \"BufferedImage@1cd4a633: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0\"],#object[javax.swing.JFrame 0xe792705 \"javax.swing.JFrame[frame0,0,0,136x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,8,31,120x93,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]]"}
;; <=

;; @@

;; @@
