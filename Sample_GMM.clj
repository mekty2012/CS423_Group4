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
(ns simple-gmm)
(use 'nstools.ns)
(ns+ template
  (:like anglican-user.worksheet))
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[nil,nil]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[nil,nil],nil]"}
;; <=

;; @@
(defn zero_vector [m]
  (if (= m 1)
    (list 0)
    (conj (zero_vector (- m 1)) 0)
    )
  )

(zero_vector 5)
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/zero_vector</span>","value":"#'template/zero_vector"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"}],"value":"(0 0 0 0 0)"}],"value":"[#'template/zero_vector,(0 0 0 0 0)]"}
;; <=

;; @@
; Also consider using defdist. It may be useful...
(with-primitive-procedures [template/zero_vector]
  (defquery random_vector [m mean std]
    (map (fn [k] (sample (normal mean std))) (zero_vector m))
    )
  )

(def samples_random_vector 
  (let [s (doquery :importance random_vector [5 0 3])]
    (map :result (take 20 s))))

(println samples_random_vector)
;; @@
;; ->
;;; ((2.2674458598995573 -1.642967187174722 5.291021096214445 -1.145054760736769 1.2126690593568368) (4.031453084385124 -4.630289592911921 -1.5655165765373222 1.6734172341958153 -4.83994207262894) (1.0838705256363703 -2.9974752184773736 -2.027815038017229 -0.4112997455087314 4.017342921223204) (-2.029180563799374 2.968735911556822 -0.8477257897830424 4.383002751880794 5.545557861166536) (1.7299510003964733 3.40886535594937 7.652581895185435 -2.653350851838919 3.0160149967382415) (-5.878528269915022 8.211474509582958 2.4051881754829965 0.9248360305858593 -0.13129910272225082) (0.04927667299098536 6.293983364491858 -1.0698049373286151 -0.43017206253521734 -1.4804074368863287) (-2.206637401282267 6.280065693326792 -3.6465861448549655 -1.7425626463058694 -0.3508061630147944) (0.8584575541819006 0.9324490150284372 -2.4810466578190744 -2.1532626536642416 -3.6160545816405794) (-2.7064294254667525 4.178914062430145 -0.9958674107160432 1.8576040875265472 6.464689229771759) (3.6091657842603113 1.981049346332557 -0.19156697080664353 2.186123210651866 3.307549270178807) (-0.26913932741655644 -1.0113435979896197 -0.4806594431128941 -1.884592757306933 3.237105571609523) (-5.2229410596321255 2.0769294712483894 3.1920263415093153 -0.3801325333491301 3.156367215706921) (-0.01509317189954499 0.09702348645031435 1.7855429881875193 -1.2022426015946743 2.1419304494517983) (-2.104939933703721 -0.3247779759802625 -6.045527075492119 1.0620354624809525 -0.17041074222966124) (0.061278098541161216 3.4688455586652767 4.22206180551852 2.205378321770203 2.9175685933904725) (1.7705776069461583 2.057644448089636 -0.8731437319731075 0.5387562043323353 1.655383516507867) (-4.887911777403799 -1.8931269258824828 -0.7816456273868706 -1.8228633554930849 0.2619489048091189) (1.8139879575090072 -3.9371154371403647 -0.3701489612084959 -1.3178898005007242 2.5016596504782203) (-0.060233085186852026 -1.0382886370245117 0.5651976347746133 -4.240850902415461 -0.7250311307784656))
;;; 
;; <-
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/random_vector</span>","value":"#'template/random_vector"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/samples_random_vector</span>","value":"#'template/samples_random_vector"}],"value":"[#'template/random_vector,#'template/samples_random_vector]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[#'template/random_vector,#'template/samples_random_vector],nil]"}
;; <=

;; @@
(defn zero_matrix [m n]
  (if (= m 1)
    (list (zero_vector n))
    (conj (zero_matrix (- m 1) n) (zero_vector n))
    )
  )

(zero_matrix 3 4)
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/zero_matrix</span>","value":"#'template/zero_matrix"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"}],"value":"(0 0 0 0)"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"}],"value":"(0 0 0 0)"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"}],"value":"(0 0 0 0)"}],"value":"((0 0 0 0) (0 0 0 0) (0 0 0 0))"}],"value":"[#'template/zero_matrix,((0 0 0 0) (0 0 0 0) (0 0 0 0))]"}
;; <=

;; @@
; Also consider using defdist. It may be useful...
(with-primitive-procedures [template/zero_matrix template/zero_vector]
  (defquery random_matrix [m mean std]
    (map (fn [l] (map (fn [k] (sample (normal mean std))) l)) (zero_matrix m m))
    )
  )

(def samples_random_matrix 
  (let [s (doquery :importance random_matrix [2 0 1])]
    (map :result (take 20 s))))

(println samples_random_matrix)
;; @@
;; ->
;;; (((-1.7391176272942446 0.23310676741340475) (0.17458477823759336 0.03154272654687617)) ((-0.8554293510185894 1.7009992160762148) (-0.2139310406465748 0.09491642799346313)) ((-0.9021960576852537 -0.38388950521104037) (-1.0861241366329126 0.9694460503043353)) ((-0.46544605668722666 0.014515834363658154) (-0.03287287885862801 -0.4652445462648598)) ((-0.1453380110857287 0.7334943231594278) (-1.198849976689329 0.3963180290377088)) ((-0.6714582193867692 0.24462454191811817) (-0.4873906851858346 -0.1969413538468882)) ((0.1741038027457708 -0.13473736345505885) (0.8198765491406335 0.5426096743890019)) ((-1.4081969352065733 -0.30716176219554225) (-0.08005684026609568 -0.5727528904702743)) ((0.38511950144295615 0.9262654522722159) (-0.060143084956472935 2.099415201831379)) ((0.6712508046162696 -0.9490848414482623) (0.5251590640122894 0.020429591341873204)) ((-0.12396474733829813 -0.9100245820644665) (-0.7905806865840759 -2.317605122822782)) ((-0.566838958940911 0.6388325606538168) (-0.5866217841012624 2.376667961853209)) ((0.403899946834949 -1.1808074655121499) (-0.12068073419896021 0.31375566069544975)) ((-0.4940147488836177 0.04384053612913525) (-0.08930245909251075 1.133739841189881)) ((0.8142031629459485 -1.616619921357688) (-0.4996760071646669 -0.009193580343850436)) ((1.87384900357008 -1.0128774504236) (-0.9575536152079096 -1.5697166437140144)) ((0.22570713548163673 -1.1462551070814953) (-0.7184753309952749 -0.23786261919238955)) ((1.5234327839727224 -1.9496285038885002) (-1.1023103949945523 -0.20517663744851522)) ((0.20218377363579948 -0.5549806903452973) (0.3757064869586965 -1.2156398348426927)) ((-0.34335402589001807 -0.9221362047016545) (0.24329188555021347 0.7124787065230165)))
;;; 
;; <-
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/random_matrix</span>","value":"#'template/random_matrix"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/samples_random_matrix</span>","value":"#'template/samples_random_matrix"}],"value":"[#'template/random_matrix,#'template/samples_random_matrix]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[#'template/random_matrix,#'template/samples_random_matrix],nil]"}
;; <=

;; @@
(defn indexed_vector [m]
  (if (= m 1)
  (list 0)
  (conj (map (fn [n] (+ n 1)) (indexed_vector (- m 1))) 0)
  )
  )
(indexed_vector 10)
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/indexed_vector</span>","value":"#'template/indexed_vector"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"},{"type":"html","content":"<span class='clj-long'>5</span>","value":"5"},{"type":"html","content":"<span class='clj-long'>6</span>","value":"6"},{"type":"html","content":"<span class='clj-long'>7</span>","value":"7"},{"type":"html","content":"<span class='clj-long'>8</span>","value":"8"},{"type":"html","content":"<span class='clj-long'>9</span>","value":"9"}],"value":"(0 1 2 3 4 5 6 7 8 9)"}],"value":"[#'template/indexed_vector,(0 1 2 3 4 5 6 7 8 9)]"}
;; <=

;; @@
(defn fst [p] (nth p 0))
(defn snd [p] (nth p 1))

(defn indexed_matrix [m n]
  (if (= m 1)
    (list (map (fn [k] (list 0 k)) (indexed_vector n)))
    (conj (map (fn [l] (map (fn [k] (map + k (list 1 0))) l)) (indexed_matrix (- m 1) n)) (map (fn [k] (list 0 k)) (indexed_vector n)))
    )
  )

(indexed_matrix 4 3)
(nth (nth (indexed_matrix 4 3) 1) 0)
(fst (list 3 4))
(snd (list 3 4))
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/fst</span>","value":"#'template/fst"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/snd</span>","value":"#'template/snd"}],"value":"[#'template/fst,#'template/snd]"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/indexed_matrix</span>","value":"#'template/indexed_matrix"}],"value":"[[#'template/fst,#'template/snd],#'template/indexed_matrix]"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"}],"value":"(0 0)"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"(0 1)"},{"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"(0 2)"}],"value":"((0 0) (0 1) (0 2))"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"}],"value":"(1 0)"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"(1 1)"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"(1 2)"}],"value":"((1 0) (1 1) (1 2))"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"}],"value":"(2 0)"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"(2 1)"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"(2 2)"}],"value":"((2 0) (2 1) (2 2))"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"}],"value":"(3 0)"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"}],"value":"(3 1)"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"}],"value":"(3 2)"}],"value":"((3 0) (3 1) (3 2))"}],"value":"(((0 0) (0 1) (0 2)) ((1 0) (1 1) (1 2)) ((2 0) (2 1) (2 2)) ((3 0) (3 1) (3 2)))"}],"value":"[[[#'template/fst,#'template/snd],#'template/indexed_matrix],(((0 0) (0 1) (0 2)) ((1 0) (1 1) (1 2)) ((2 0) (2 1) (2 2)) ((3 0) (3 1) (3 2)))]"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"}],"value":"(1 0)"}],"value":"[[[[#'template/fst,#'template/snd],#'template/indexed_matrix],(((0 0) (0 1) (0 2)) ((1 0) (1 1) (1 2)) ((2 0) (2 1) (2 2)) ((3 0) (3 1) (3 2)))],(1 0)]"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"[[[[[#'template/fst,#'template/snd],#'template/indexed_matrix],(((0 0) (0 1) (0 2)) ((1 0) (1 1) (1 2)) ((2 0) (2 1) (2 2)) ((3 0) (3 1) (3 2)))],(1 0)],3]"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}],"value":"[[[[[[#'template/fst,#'template/snd],#'template/indexed_matrix],(((0 0) (0 1) (0 2)) ((1 0) (1 1) (1 2)) ((2 0) (2 1) (2 2)) ((3 0) (3 1) (3 2)))],(1 0)],3],4]"}
;; <=

;; @@
(defn transpose [m n mat]
  (let [ind_mat (indexed_matrix n m)]
    (map (fn [l] (map (fn [k] (nth (nth mat (snd k)) (fst k))) l)) ind_mat)
    )
  )

(let [sample_mat
      (list (list 0 1 2 3) (list 2 3 4 5) (list 3 4 5 6))
      ]
  (transpose 3 4 sample_mat)
  )
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/transpose</span>","value":"#'template/transpose"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"}],"value":"(0 2 3)"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}],"value":"(1 3 4)"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"},{"type":"html","content":"<span class='clj-long'>5</span>","value":"5"}],"value":"(2 4 5)"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>5</span>","value":"5"},{"type":"html","content":"<span class='clj-long'>6</span>","value":"6"}],"value":"(3 5 6)"}],"value":"((0 2 3) (1 3 4) (2 4 5) (3 5 6))"}],"value":"[#'template/transpose,((0 2 3) (1 3 4) (2 4 5) (3 5 6))]"}
;; <=

;; @@
(defn inner_prod [v1 v2]
  (reduce + 0 (map * v1 v2))
  )

(inner_prod (list 1 2 3 4) (list 1 2 3 4))
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/inner_prod</span>","value":"#'template/inner_prod"},{"type":"html","content":"<span class='clj-long'>30</span>","value":"30"}],"value":"[#'template/inner_prod,30]"}
;; <=

;; @@
; m1 : a * b, m2 : c * b matrix
(defn trans_mult [m1 m2 a b c]
  (map (fn [l] (map (fn [k] (
                              inner_prod (nth m1 (fst k)) (nth m2 (snd k))
                              )) l)) (indexed_matrix a c))
  )

(let 
  [m1 (list (list 1 2 3) (list 3 2 1) (list 2 2 2))
   m2 (list (list 1 2 3) (list 3 2 1))
   ]
  (trans_mult m1 m2 3 3 2)
  )
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/trans_mult</span>","value":"#'template/trans_mult"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>14</span>","value":"14"},{"type":"html","content":"<span class='clj-long'>10</span>","value":"10"}],"value":"(14 10)"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>10</span>","value":"10"},{"type":"html","content":"<span class='clj-long'>14</span>","value":"14"}],"value":"(10 14)"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>12</span>","value":"12"},{"type":"html","content":"<span class='clj-long'>12</span>","value":"12"}],"value":"(12 12)"}],"value":"((14 10) (10 14) (12 12))"}],"value":"[#'template/trans_mult,((14 10) (10 14) (12 12))]"}
;; <=

;; @@
(with-primitive-procedures [template/trans_mult template/zero_matrix]
  (defquery random_positive-semidefinite_symmetric_matrix [m mean std]
    (let [mat (
                map (fn [l] (map (fn [k] (sample (normal mean std))) l)) (zero_matrix m m)
                )]
      (trans_mult mat mat m m m)
      )
    )
  )

(def samples_positive-semidefinite_symmetric_matrix 
  (let [s (doquery :importance random_positive-semidefinite_symmetric_matrix [2 0 1])]
    (map :result (take 20 s))))

(println samples_positive-semidefinite_symmetric_matrix)
;; @@
;; ->
;;; (((0.5983178101453244 0.38652227992591437) (0.38652227992591437 2.4431337934452797)) ((0.31714996350405 -0.3591503476015699) (-0.3591503476015699 0.4165878777760544)) ((2.1344792262607015 -1.541024892650725) (-1.541024892650725 1.1341853097827599)) ((1.0336591585034036 0.24651660181669532) (0.24651660181669532 0.3507922285773344)) ((0.8327575621548619 -1.2012878458085223) (-1.2012878458085223 2.8253726261840173)) ((6.928368736134112 -1.4830080960656102) (-1.4830080960656102 1.8293452456099046)) ((0.5736470852232146 0.7268889673430142) (0.7268889673430142 1.0450048500428224)) ((0.5316062919906914 0.04984729550040373) (0.04984729550040373 1.5507506416391363)) ((0.9432460583547232 1.0807515560612724) (1.0807515560612724 1.9079931058338162)) ((0.9129557571952592 -0.16134145677269185) (-0.16134145677269185 1.5249004075708477)) ((0.06051526402572377 -0.034018373180442624) (-0.034018373180442624 0.04208954982655312)) ((1.1922053493955493 -1.559746235257441) (-1.559746235257441 5.6002363738975465)) ((1.9453570323718485 0.8013267182811405) (0.8013267182811405 2.03809850589952)) ((4.300047907210717 -1.7584017038032098) (-1.7584017038032098 0.778853331858911)) ((1.7494244775260541 1.833134850794672) (1.833134850794672 2.0022739510476377)) ((1.1223249089480711 -0.3726656340179385) (-0.3726656340179385 0.26855611210314334)) ((0.2512160185043426 0.26493054126688537) (0.26493054126688537 2.9037256502740547)) ((2.00309128146416 -0.8336031559028955) (-0.8336031559028955 4.0843739676398725)) ((0.5498530845709094 -0.046562574277032975) (-0.046562574277032975 0.1619498087739169)) ((3.3389875511059257 -3.441846246966898) (-3.441846246966898 3.552329395641043)))
;;; 
;; <-
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/random_positive-semidefinite_symmetric_matrix</span>","value":"#'template/random_positive-semidefinite_symmetric_matrix"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/samples_positive-semidefinite_symmetric_matrix</span>","value":"#'template/samples_positive-semidefinite_symmetric_matrix"}],"value":"[#'template/random_positive-semidefinite_symmetric_matrix,#'template/samples_positive-semidefinite_symmetric_matrix]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[#'template/random_positive-semidefinite_symmetric_matrix,#'template/samples_positive-semidefinite_symmetric_matrix],nil]"}
;; <=

;; @@
; m : a * b matrix, v : b vector
(defn mat_vec_mult [m v a b]
  (map (fn [l] (inner_prod l v)) m)
  )

(mat_vec_mult (list (list 1 2 3) (list 2 3 4) (list 3 4 5)) (list 1 -1 1) 3 3)
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/mat_vec_mult</span>","value":"#'template/mat_vec_mult"},{"type":"list-like","open":"<span class='clj-lazy-seq'>(</span>","close":"<span class='clj-lazy-seq'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"}],"value":"(2 3 4)"}],"value":"[#'template/mat_vec_mult,(2 3 4)]"}
;; <=

;; @@
; Given Sigma = AA^T, compute u + Az. This creates multivariate normal of (u, Sigma).
(with-primitive-procedures [template/mat_vec_mult template/zero_vector]
  (defquery multivariate_normal [m mean_vector sqrt_covariance_mat]
    (let [coeff_vec (map (fn [k] (sample (normal 0 1))) (zero_vector m))]
      (map + mean_vector (mat_vec_mult sqrt_covariance_mat coeff_vec m m))
      )
    )
  )

(def samples_multivariate_normal 
  (let [s (doquery :importance multivariate_normal [2 (list 0 0) (list (list 1 0.5) (list 0.5 1))])]
    (map :result (take 20 s))))

(println samples_multivariate_normal)
;; @@
;; ->
;;; ((0.33333201000568746 -0.3361470724056045) (-0.39214660178677213 -0.7629914167974341) (0.46516790212744574 1.1106410073942643) (0.7027363765282191 0.6060345792221253) (-1.2315029780849822 -2.0713288207481653) (0.08451975894313708 0.12395662818806127) (-0.6194300662410925 -0.8395824860525614) (0.31757479033370595 1.3718826508882422) (-1.9896922712756697 -2.1895170791308027) (-0.15826810442691197 0.2800225195762097) (0.21051043602579741 0.23752220590298312) (0.3747799138548269 0.677217549724464) (-0.030650222892557824 0.1397338160904042) (-1.2251502324712074 -1.4183702397181919) (1.1907515930732455 0.728338496414841) (1.8832666657132888 0.30262433855223014) (0.8165426031907204 0.4107068693533974) (0.47578603093363037 -0.0036156821981899734) (0.5978768169078552 0.24149098747436865) (1.2263423463645085 0.2725407366951075))
;;; 
;; <-
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/multivariate_normal</span>","value":"#'template/multivariate_normal"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/samples_multivariate_normal</span>","value":"#'template/samples_multivariate_normal"}],"value":"[#'template/multivariate_normal,#'template/samples_multivariate_normal]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[#'template/multivariate_normal,#'template/samples_multivariate_normal],nil]"}
;; <=

;; @@
(defn n_vector [n k]
  (if (= n 1)
  (list k)
  (conj (n_vector (- n 1) k) k))
  )

(with-primitive-procedures [template/n_vector template/zero_vector template/zero_matrix template/mat_vec_mult]
  (defquery gaussian-mixture-model [dim lambda mean_mean mean_std std_mean std_std]
    (let [n (sample (poisson lambda))
          pi (sample (dirichlet (n_vector n 1)))
          mean_vectors (map (fn [kkk] (map (fn [kkkk] (sample (normal mean_mean mean_std))) (zero_vector dim))) (zero_vector n))
          covariance_matrices (map (fn [kkk] 
                                (map (fn [l] 
                                  (map (fn [kkkk] (sample (normal std_mean std_std))) l)
                                ) 
                                (zero_matrix dim dim)
                                )) (zero_vector n)
                              )
          chooser (sample (discrete pi))
          model {:n n,
                 :pi pi,
                 :mean_vectors mean_vectors,
                 :covariance_matrices covariance_matrices}
          ]
      (
        let [mean_vector (nth mean_vectors chooser)
             covariance_matrix (nth covariance_matrices chooser)
             ]
        (
          let [coeff-vec (map (fn [kkk] (sample (normal 0 1))) (zero_vector dim))]
          (
            let [result (map + mean_vector (mat_vec_mult covariance_matrix coeff-vec dim dim))]
              {:model model, :result result}
            )
          )
        )
      )
    )
  )
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/n_vector</span>","value":"#'template/n_vector"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/gaussian-mixture-model</span>","value":"#'template/gaussian-mixture-model"}],"value":"[#'template/n_vector,#'template/gaussian-mixture-model]"}
;; <=

;; @@
(def samples_gmm 
  (let [s (doquery :importance gaussian-mixture-model [2 2 0 1 0 1])]
    (map :result (take 2 s))))

(println samples_gmm)
;; @@
;; ->
;;; ({:model {:n 2, :pi (0.3405523280518287 0.6594476719481712), :mean_vectors ((-1.0399917635353846 1.8485059689894383) (0.8945893086374305 0.12476183303894559)), :covariance_matrices (((1.052003590527338 -0.29457293855940164) (-0.4855409392358547 -0.8676131420062221)) ((0.8984711272611344 -0.6775605511335426) (-1.3127797802292493 0.9589939762840167)))}, :result (0.5323521686347832 0.665800816726409)} {:model {:n 1, :pi (1.0), :mean_vectors ((0.0102595549313888 0.53528285267674)), :covariance_matrices (((-1.561077571829171 0.09919492587488117) (1.542429844412356 1.2780925008670805)))}, :result (-0.9426664425476512 0.8161318099690443)})
;;; 
;; <-
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/samples_gmm</span>","value":"#'template/samples_gmm"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[#'template/samples_gmm,nil]"}
;; <=

;; @@

;; @@
