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
(ns+ templete
  (:like anglican-user.worksheet))
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[nil,nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[nil,nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[nil,nil],nil],nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[[nil,nil],nil],nil],nil]"}
;; <=

;; @@
(moe.batchreader/for-Images "data/cifar-10-batches-bin/data_batch_1.bin" 2 (fn [im] (mikera.image.core/show im)))
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
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;templete/image</span>","value":"#'templete/image"},{"type":"html","content":"<span class='clj-unkown'>#object[java.awt.image.BufferedImage 0x11fac32c &quot;BufferedImage@11fac32c: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0&quot;]</span>","value":"#object[java.awt.image.BufferedImage 0x11fac32c \"BufferedImage@11fac32c: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0\"]"}],"value":"[#'templete/image,#object[java.awt.image.BufferedImage 0x11fac32c \"BufferedImage@11fac32c: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0\"]]"},{"type":"html","content":"<span class='clj-unkown'>#object[javax.swing.JFrame 0x710767d5 &quot;javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]&quot;]</span>","value":"#object[javax.swing.JFrame 0x710767d5 \"javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]"}],"value":"[[#'templete/image,#object[java.awt.image.BufferedImage 0x11fac32c \"BufferedImage@11fac32c: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0\"]],#object[javax.swing.JFrame 0x710767d5 \"javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]]"}
;; <=

;; @@
(def f (java.io.File. "data/cifar-10-batches-bin/data_batch_1.bin"))

(def st (byte-streams/to-input-stream f))

(def ch (take 1 (byte-streams/convert st (byte-streams/seq-of java.nio.ByteBuffer) {:chunk-size 1024})))

(byte-streams/print-bytes ch)
;; @@
;; ->
;;; 06 3B 2B 32 44 62 77 8B  91 95 95 83 7D 8E 90 89      .;+2Dbw.....}.......................3Xx..~tjeiqmpwmi}.z..y....{wz..1Sn..yqppji....zsx....~....vxm!.Wjsuriky}mq...vu.z.........}y.2;f..yxrk}.jl.ylbnux......ywgWKCGTn....wlz{ikolb.aSXfaXv..xkXC# ao{...zy...xkPDJeiA.?N.z...l_`YBsw.........lH3)HµÑ}D@R{p...rie~f....w{..y..J62,VËÙ.dMKJLk....w}.....{}~...Z?.FOg...ye`VKe................yPaZb.........fl.............ug.xo.........ÃÑ.}l............yTXme.Õ..Ó.ÍÏÕ.Ç................ZHQTk.å..ïÛäáÖØÒÈ.....~.........PYi`.À..ËßòôîñãáëÛà.............?BXq.Ü..ÎÄ.ÿÿõìæ.çúñ.}~.}~......@Tp.ßÎ.ÄÌÜóõïêçÃ.Ðúã.....yr.....dl.Òø..ÜâæéàÉ.µ.ª.çß........ur{~z].îøª.ñæ.......ØÔìì..............íüå..ÜÂ{......ÊððÞ.wxpdc.......ÛôÒÁ............Ê.Æ.dmwyl..}..{.......Ã.............ooy..............ÍÉ............{.qlq.Ç...........ÔËÏ........k[Ti.v`f.............ÊàÅÀ..~...sMO]~.wq..............................y..Ó.Ê...z.........................ºuØÁ...~....x.....~...........ËÎ.GÜÉº.......~....~......~....rº.8!ÐÉÆ...................x...Z2..85..ºÂÆÉ................upzh.&quot;..aS....ÊÚÚÏ..............pw.x\gªØ.
;;; 
;; <-
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;templete/f</span>","value":"#'templete/f"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/st</span>","value":"#'templete/st"}],"value":"[#'templete/f,#'templete/st]"},{"type":"html","content":"<span class='clj-var'>#&#x27;templete/ch</span>","value":"#'templete/ch"}],"value":"[[#'templete/f,#'templete/st],#'templete/ch]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[[#'templete/f,#'templete/st],#'templete/ch],nil]"}
;; <=

;; @@

;; @@
