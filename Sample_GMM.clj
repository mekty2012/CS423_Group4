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
;;; ((2.242003352849569 -1.5714002461350387 -5.612202652649485 -3.2943620617251455 -1.6200188251224872) (-0.41570650155562255 0.6738309248475186 4.756024275978797 -3.7114433137482523 -4.09599433841197) (-0.5598880024671005 -3.2439433593743434 -1.4107792248992215 -0.6890201895091876 -3.739258979312823) (-1.0298393282705225 5.320400377545029 0.9301644602346812 1.7416568700820536 1.4333226623077469) (0.2876285319831458 0.7201599305668641 1.0971401975905437 -2.098404119960119 6.264630600602892) (-2.2549879513397726 -0.058664199151793855 2.769344174696757 0.23326472106748722 1.6096900398338074) (3.218557462876295 -2.7467038942519926 -0.2626672236091199 2.16613943944268 1.6099053714081637) (1.8006199740783662 2.916901990495819 -4.774162645027232 -2.0488856805845432 0.45665363077928545) (-3.740937203499865 -6.401784600169634 3.3890642193245126 4.849902027281816 -2.5973700175802037) (-1.9061809645555925 3.298505471058599 0.5357431494699176 2.337592383338058 5.487681494971028) (5.801987936730671 0.04858906272582113 -2.016281026942629 1.2577411653197985 -4.406633000756035) (1.798628971280272 -0.7305024316714614 0.30886481235575536 -0.2647520297904371 -3.387456514502131) (-1.6893381793726654 -1.1133478130952446 1.188406829951831 0.8377639585032606 1.3293646695842778) (-1.9413271973096409 4.149368097943902 -4.758158576298719 2.3063910932895784 2.1538451846621567) (-1.2280254993057165 0.6539384392275653 -1.1473810409072436 -0.43764691803622724 -0.6004552897011971) (-0.7017571675627974 -1.3523513719281421 -5.496079651510381 -0.5381805965886429 -3.486547213256417) (7.458507677862761 8.634612705546184 1.759883743006424 4.1636182742151115 -3.8354811464195264) (0.21247058763298857 1.8256766896490153 -2.9918406917273623 2.2394384823923836 -2.735424540474896) (-1.0812541604545678 1.8849907538722563 -5.1719658982145065 3.5601692345084466 -0.22095149396951197) (-0.9071421735520455 -2.171134871570067 -0.1868823984623929 0.43226290165495784 -3.7966590536529825))
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
;;; (((1.861810223469417 -1.873683121419539) (-0.7406139101424939 1.173758889192656)) ((-0.3215935226323275 0.08063435703822805) (-1.5824962957474173 -0.15625442270734172)) ((0.4895614257438464 0.6994129805514537) (-0.06727244965613535 -1.1059212031969525)) ((0.4957225457620589 -1.0155276556703137) (0.4120829523722742 1.5355226203361558)) ((1.9703954255029827 1.1365273220935954) (-0.6913710890133706 0.179718025709252)) ((-0.03198078027731708 -1.4830166343094766) (0.2885238479237256 0.778002297501248)) ((0.422333471248994 0.5956987191428932) (-0.5093981392519356 3.145170093082965)) ((1.2452314266396858 -0.06547496446600146) (1.2102035784444705 -0.3268342929444767)) ((0.2259676033994354 0.5470257490552645) (-1.0960680727222134 -0.4755405909999329)) ((1.98530124370669 1.3343116744249082) (-0.6816397653681151 -0.22343511240531996)) ((0.5056475547692322 -1.249507358477424) (-0.1213700322746577 0.1712166825471942)) ((-0.960492948301868 0.1000368559672354) (1.1906863731320851 1.2019094069025649)) ((-0.39059822785570447 0.11289494729269013) (1.2210942423507976 0.8455531709462337)) ((1.2644920170519958 -0.06511574205478318) (0.9215670822554324 0.16433632365554973)) ((-0.17051685384624402 1.1379875394367267) (2.1365936883211503 -0.45878134457123254)) ((0.22721346563179154 2.429311766545128) (-2.015640219018522 -1.936517875449933)) ((1.5088663322153821 -0.26784255792071615) (-1.1421707785610358 0.7605206459065269)) ((-0.3254078386094308 -1.4832119686656022) (-0.3756400666092518 -0.4814391599882034)) ((0.5341300378885683 0.39830194873696667) (-1.147804203386197 0.6364491465174361)) ((-0.39813361038575223 -0.7436936791385256) (-0.14080569234297655 0.9597331863676942)))
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

(inner_prod (list 1 2 3) (list 1 2 3))
;; @@
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/inner_prod</span>","value":"#'template/inner_prod"},{"type":"html","content":"<span class='clj-long'>14</span>","value":"14"}],"value":"[#'template/inner_prod,14]"}
;; <=

;; @@
; m1 : a * b, m2 : b * c matrix
(defn trans_mult [m1 m2 a b c]
  (list 0 0 0 0 0)
  )
;; @@
