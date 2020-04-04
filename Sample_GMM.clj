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
;;; ((-0.8395828995304894 -0.2536012801655205 5.5816946806860495 1.7265213028557316 3.002824875876845) (-1.2035402909384327 -0.19986173443859354 3.6364704964657477 -5.740072258299049 -1.321094548134809) (1.489572630406173 -1.236522415635402 -3.7903838391383182 -0.12991428245651107 0.5383345371247714) (-4.134111405027801 -3.376450897254843 1.8642564649270659 -1.9700951669206792 1.4746601903802603) (-2.336087167157298 -1.44856312574862 -0.9949978473409158 -1.480215166506895 -1.8281713382442835) (-1.937118919746505 2.1263631047690428 0.16503054358290187 0.9079955962857871 -4.002431961950937) (2.128775122598576 -2.0358349695831013 -1.728396226029702 -0.9849743893284509 -0.7682491251846784) (0.23880225751573136 -2.139191290898292 -0.3764988066747941 1.607387277794622 6.130701381097843) (1.6575571807005018 -2.177343054039569 -1.4426573049294449 -3.3953828096871326 -3.579320390786507) (-3.277874345003993 -2.402975276579216 -4.378352641321586 -1.8328236760596581 -4.325864443806491) (3.1943931160998087 0.377888130862563 6.220479283141312 0.9955667259460501 1.4777578624449235) (-1.999550637262506 -0.18125554505952235 3.53446150855541 2.4700983513875583 5.374228536678594) (0.4584369504661422 2.334244469953486 5.370504899859522 3.3229612737603533 3.3059583749507997) (-1.8010050328404466 -1.0833035934331003 0.37605300530796537 4.857761991031316 1.6249652773543155) (-1.3415065504290657 0.26215655609611344 -0.2526659108477799 0.318510334831774 -0.24414710243670903) (-5.05138587469154 -3.634609135825868 2.7110393233183103 0.145783280676068 3.640123532672345) (-4.0745944185289975 3.03519426129256 1.3670218520624478 -3.4016303503890364 0.668427586224366) (-2.6568061803117295 3.1757546448016925 -3.0938518973356954 -0.508505471582247 -7.418572478806862) (-2.808113758304613 2.4171436978185397 -1.5604818795558213 -0.7044114882798236 -0.7124866930844546) (1.0724338109730518 -3.829539485219356 -0.48335812617981744 -5.289083876048506 -6.91751507108575))
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
;;; (((0.6886496297254467 0.9253131768164722) (-0.2173606965926788 -0.9918101492289082)) ((1.4882689568709746 0.38230665234116795) (-1.679393021343309 -1.8519419573680789)) ((1.2461524639746722 1.3561021138614886) (0.6455111536346024 -0.8860705023357692)) ((1.148414187122757 0.7060531444310341) (0.222375232277664 0.12992383348176442)) ((0.4181696069392271 0.13514471165237482) (-0.9619118019696348 -0.8741332088483227)) ((0.030707410498170936 0.04192255288546771) (-0.657896094413645 0.1085282990546749)) ((-2.6210974236216615 0.03659058297068703) (-1.4360995841452442 -0.14420678895509703)) ((1.0490780909814752 0.6583792624607109) (-0.2949140074840026 -0.3186182466472515)) ((0.38963457559310666 -1.0889312066056667) (0.9437770357564883 -1.7088827390897379)) ((0.0251066172358864 -0.3208160003634906) (0.15956190647748889 0.21175348632603996)) ((-0.5792263405090743 -0.9579480111013245) (0.6948564927130484 -0.2985877692790959)) ((-0.18283051761540803 0.02055482061810033) (0.2828631500075478 -1.831927141172917)) ((0.23134300102160998 -1.0468813406630422) (-0.11266742725451305 -1.278845627592427)) ((-1.3754741404021225 1.3912644581682412) (-1.2315846614090742 0.41723240718824883)) ((0.4156109947671057 -2.0222150097291696) (-0.4899013639468335 0.05838872967135343)) ((-0.7800414072782134 -0.545077685331273) (-0.7295851806553492 -0.9168949379224801)) ((0.959797594091793 -0.7263312195497803) (-1.2520441464556733 0.25850663565242876)) ((-0.24461260375544766 -0.01092326417830625) (-0.17630401217003014 -0.7768977284120832)) ((-0.5574066149631639 1.056578183647551) (0.6603028553222399 -0.4237293669187699)) ((-0.6466813091943421 -0.18966861294131068) (0.13332824914956662 0.6939706742929496)))
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
;;; (((2.6565039039021388 -2.1119371362228705) (-2.1119371362228705 3.6689865499009646)) ((2.9982111106440548 -1.4488176243384217) (-1.4488176243384217 0.7740301184554202)) ((3.009550280763051 -0.536071589656585) (-0.536071589656585 0.8899038981533263)) ((2.3543498700455276 -0.6014827769774667) (-0.6014827769774667 0.15775375058641886)) ((1.4844081433292384 1.0467421692011991) (1.0467421692011991 1.1931152132536593)) ((1.552705782693096 1.2329116032670446) (1.2329116032670446 2.6406902459395027)) ((3.629098970351858 -1.3233409573192) (-1.3233409573192 1.698039023588592)) ((0.21552381963208794 0.34076498293448193) (0.34076498293448193 1.0090850582905144)) ((1.651306313955346 0.12219562073604578) (0.12219562073604578 2.1029012756604093)) ((0.6545620866487557 -0.3146301930982809) (-0.3146301930982809 1.7202569159714134)) ((0.8729922268365982 -1.5454845423933747) (-1.5454845423933747 2.8658238200962733)) ((1.3374128694551608 0.2713678545233035) (0.2713678545233035 1.3875842764693822)) ((0.5978978957908424 0.42758807786504216) (0.42758807786504216 1.033846136882036)) ((0.7138772795393407 0.22927866914871692) (0.22927866914871692 0.10405157103349845)) ((0.6965677509992274 -0.3253882940370216) (-0.3253882940370216 0.7405635121810802)) ((2.196679169862996 -0.880660674925889) (-0.880660674925889 1.4542669360810716)) ((1.403605488054381 1.4137984596129458) (1.4137984596129458 2.870068021888903)) ((7.7201170931977865 -2.696927663309399) (-2.696927663309399 1.2718132588368318)) ((1.3334421537354713 0.9622353180818013) (0.9622353180818013 1.8100321704240336)) ((0.06026063806125071 0.042150624285060036) (0.042150624285060036 0.10331620377352388)))
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
;;; ((0.9063840972684545 0.9274079209443638) (0.8548494162775604 0.2886498904773678) (-0.8657480391448318 -0.35710079569700376) (-1.6456949311615656 -0.9547240930548158) (2.322763869585205 1.7260580550113485) (0.09005407787560571 0.09202923772057592) (1.2745418053688133 1.3628573121488745) (-1.5063880732510797 -1.4712008177341165) (-0.4751025556445506 0.014426596277352599) (0.21684502110249476 0.9660593091395084) (0.6385731750097643 -0.4416502477740667) (0.22681297952401602 -0.5899871543484159) (0.6939560812954246 -0.8597036967105848) (0.7331957820573652 0.16129444649993574) (-0.23141733769197886 -0.21071967649021023) (-0.32164614848463036 -0.2827323813350355) (-1.391011372725813 -1.0505028224848065) (1.7154418389836144 1.0557844121056694) (3.6133671342794003 2.5343976007224236) (0.6668954328480142 0.5261912614109477))
;;; 
;; <-
;; =>
;;; {"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"list-like","open":"","close":"","separator":"</pre><pre>","items":[{"type":"html","content":"<span class='clj-var'>#&#x27;template/multivariate_normal</span>","value":"#'template/multivariate_normal"},{"type":"html","content":"<span class='clj-var'>#&#x27;template/samples_multivariate_normal</span>","value":"#'template/samples_multivariate_normal"}],"value":"[#'template/multivariate_normal,#'template/samples_multivariate_normal]"},{"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}],"value":"[[#'template/multivariate_normal,#'template/samples_multivariate_normal],nil]"}
;; <=

;; @@

;; @@
