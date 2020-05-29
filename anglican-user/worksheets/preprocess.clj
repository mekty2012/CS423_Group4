;; gorilla-repl.fileformat = 1

;; **
;;; # Preprocessing
;;; 
;;; This is the clojure file that contains functions needed to preprocess the CIFAR 10 images. Completed functions are dropout, grayscale, and nbox
;; **

;; @@
(ns image-preprocess
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
;Function to return image with each pixel dropped with probability p
(defn dropoutted [image p]
  (let [ret-image (mikera.image.core/new-image 32 32)]
    (loop [x 0 y 0]
      (if (= y 32)
        ret-image ;Loop done. Return new image.
        (if (= x 32)
          (recur 0 (+ y 1))
          (do
            (if (< (rand) p)
              (mikera.image.core/set-pixel ret-image x y (mikera.image.colours/rgb-from-components 0 0 0)) ;Below threshold. Drop pixel
              (mikera.image.core/set-pixel ret-image x y (mikera.image.core/get-pixel image x y)) ;Else keep pixel
              ) 
            (recur (+ x 1) y)
            )
          )
        )
      )
    )
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/dropoutted</span>","value":"#'template/dropoutted"}
;; <=

;; @@
;Testing dropout
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
(def dropped (dropoutted image 0.5))
(mikera.image.core/show dropped)
(mikera.image.core/show image)
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/image</span>","value":"#'template/image"},{"type":"html","content":"<span class='clj-unkown'>#object[java.awt.image.BufferedImage 0xc5285b2 &quot;BufferedImage@c5285b2: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0&quot;]</span>","value":"#object[java.awt.image.BufferedImage 0xc5285b2 \"BufferedImage@c5285b2: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0\"]"}],"value":"[#'template/image,#object[java.awt.image.BufferedImage 0xc5285b2 \"BufferedImage@c5285b2: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0\"]]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/dropped</span>","value":"#'template/dropped"}],"value":"[[#'template/image,#object[java.awt.image.BufferedImage 0xc5285b2 \"BufferedImage@c5285b2: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0\"]],#'template/dropped]"},{"type":"html","content":"<span class='clj-unkown'>#object[javax.swing.JFrame 0xc164159 &quot;javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]&quot;]</span>","value":"#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]"}],"value":"[[[#'template/image,#object[java.awt.image.BufferedImage 0xc5285b2 \"BufferedImage@c5285b2: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0\"]],#'template/dropped],#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]]"},{"type":"html","content":"<span class='clj-unkown'>#object[javax.swing.JFrame 0xc164159 &quot;javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]&quot;]</span>","value":"#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]"}],"value":"[[[[#'template/image,#object[java.awt.image.BufferedImage 0xc5285b2 \"BufferedImage@c5285b2: type = 2 DirectColorModel: rmask=ff0000 gmask=ff00 bmask=ff amask=ff000000 IntegerInterleavedRaster: width = 32 height = 32 #Bands = 4 xOff = 0 yOff = 0 dataOffset[0] 0\"]],#'template/dropped],#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]],#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]]"}
;; <=

;; @@
;Return appropriate grayscale value of given rgb
(defn togray [rgb]
  (let [sum (+ (nth rgb 0) (+ (nth rgb 1) (nth rgb 2)))]    
    (int (/ sum 3))
    )
  )

;Get grayscaled image of given image
(defn grayscaled [image]
  (let [ret-image (mikera.image.core/new-image 32 32)]
    (loop [x 0 y 0]
      (if (= y 32)
        ret-image ;Loop done. Return new image.
        (if (= x 32)
          (recur 0 (+ y 1))
          (do
            (let [pixelcolor (mikera.image.core/get-pixel image x y); Get rgb of current pixel
                  rgb (mikera.image.colours/components-rgb pixelcolor)]
              (mikera.image.core/set-pixel ret-image x y (mikera.image.colours/rgb-from-components (togray rgb) (togray rgb) (togray rgb))) ;Insert grayscale pixel in new image
            )
            (recur (+ x 1) y)
            )
          )
        )
      )
    )
  )
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/togray</span>","value":"#'template/togray"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/grayscaled</span>","value":"#'template/grayscaled"}],"value":"[#'template/togray,#'template/grayscaled]"}
;; <=

;; @@
;Testing grayscale

(def grayed (grayscaled image))
(mikera.image.core/show grayed)
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/grayed</span>","value":"#'template/grayed"},{"type":"html","content":"<span class='clj-unkown'>#object[javax.swing.JFrame 0xc164159 &quot;javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]&quot;]</span>","value":"#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]"}],"value":"[#'template/grayed,#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,52x132,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,52x110,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]]"}
;; <=

;; @@
;Function to get 2n+1 by 2n+1 image that is centered at ij pixel of original image. 
(defn nbox [image n i j]
  
  (let [box (mikera.image.core/new-image (+ (* 2 n) 1) (+ (* 2 n) 1))]
    (loop [x (- 0 n) y (- 0 n)]
    	(if (= y (+ 1 n))
            box
           ;Loop done. Return new image
    		(if (= x (+ 1 n))
              (recur 0 (+ y 1)) ;One column done, go to next
    		(do
              (if (or (< (+ i x) 0) (< (+ j y) 0) (> (+ i x) 31) (> (+ j x ) 31)) ;Check for out of bound in given 32 by 32 image			
                  (mikera.image.core/set-pixel box (+ x n) (+ x n)(mikera.image.colours/rgb-from-components 0 0 0));If out of bound black pixel in new image
                (let [pixelcolor (mikera.image.core/get-pixel image (+ i x) (+ j y))] ;Else get rgb and set into image's pixel                  
                    (mikera.image.core/set-pixel box (+ x n) (+ y n) pixelcolor)
                  )
                )
              (recur (+ x 1 ) y) ;Pixel done. Go to right pixel
              )
          	)
          )
    	)
    )
  )
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/nbox</span>","value":"#'template/nbox"}
;; <=

;; @@
;Testing nbox


(def sevenbox1616 (nbox image 3 16 16))
	
(mikera.image.core/show sevenbox1616)

(def sevenbox00 (nbox image 3 0 0))
	
(mikera.image.core/show sevenbox00)
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/sevenbox1616</span>","value":"#'template/sevenbox1616"},{"type":"html","content":"<span class='clj-unkown'>#object[javax.swing.JFrame 0xc164159 &quot;javax.swing.JFrame[frame0,0,23,42x107,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,42x85,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]&quot;]</span>","value":"#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,42x107,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,42x85,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]"}],"value":"[#'template/sevenbox1616,#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,42x107,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,42x85,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/sevenbox00</span>","value":"#'template/sevenbox00"}],"value":"[[#'template/sevenbox1616,#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,42x107,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,42x85,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]],#'template/sevenbox00]"},{"type":"html","content":"<span class='clj-unkown'>#object[javax.swing.JFrame 0xc164159 &quot;javax.swing.JFrame[frame0,0,23,42x107,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,42x85,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]&quot;]</span>","value":"#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,42x107,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,42x85,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]"}],"value":"[[[#'template/sevenbox1616,#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,42x107,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,42x85,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]],#'template/sevenbox00],#object[javax.swing.JFrame 0xc164159 \"javax.swing.JFrame[frame0,0,23,42x107,layout=java.awt.BorderLayout,title=Imagez Frame,resizable,normal,defaultCloseOperation=DISPOSE_ON_CLOSE,rootPane=javax.swing.JRootPane[,0,22,42x85,layout=javax.swing.JRootPane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximumSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]\"]]"}
;; <=

;; @@

;; @@
