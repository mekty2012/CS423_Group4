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
;;; ((-0.6796734286657966 -5.8353355288987085 -0.8371160029581923 -2.3823963032991653 -0.39979495454231706) (2.7458657349549016 0.5287956408025305 1.0551663550206376 7.310849690942235 -4.442040422875119) (1.650766812458453 1.6952943490687127 4.203665707150817 3.43460210082764 1.1913771361091339) (0.6750619227974191 -3.648196850718038 0.39766497209585944 -4.253045439011695 -0.6605586335491167) (-3.7202173138736017 -2.995374289970812 -2.2166901617602015 -4.062173597032686 0.7523235125872338) (7.857946495735758 0.42666028340913165 0.43931246515161315 3.5670650086316726 -1.0753960870770776) (-0.7405671668128807 -1.7949474932392429 -1.9779508213118842 -5.124951690615315 -2.39356462070903) (-4.801463936597344 1.3667900762036347 -3.145034010005946 -2.961983466085487 0.3105637553396013) (0.407141572111311 1.6366398391351007 -2.173550184781879 -3.363336954852624 -1.660759178118763) (1.5125615037308928 1.1214200392306612 -3.2507746679439156 -1.1987075927401345 1.9658481979318037) (6.431516441644518 -1.1615775971593776 2.010602026740697 3.1409169759530053 1.5795079238405565) (-2.5008883383347005 0.7779007766839698 -5.796641888719982 1.3451622891070734 1.6052313352042766) (-2.415038021294926 -3.1014231394033183 0.5567213331928111 5.1234034146310705 2.0261293018077517) (-4.076341746872034 -0.356688626772576 2.1252296304703675 3.022978640147249 -3.934614596827974) (5.465245533830187 -4.219437943527927 -0.12420069070668745 2.1226501981187753 -4.870154059839942) (1.6309076788488386 7.585735738356604 0.6176824813283904 5.342262077825085 0.0970576289023449) (1.7376647024783227 -0.7439348415320108 0.9321893710311532 -4.400512081061066 -1.793263827010274) (2.3664917070252014 -0.7158381437498036 5.271956769599926 -4.19226042672652 3.412914861922234) (4.7163008829415105 -0.6582045660921554 -2.577594032296416 1.5462329972205302 1.3935484581522934) (1.5198015895882424 -3.8110867954335683 0.6823540166843058 -1.5336554152178268 -1.1425741096714392))
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
;;; (((0.5601868284958923 0.35326489866850375) (-0.3329061131811435 -0.6272225055992247)) ((-0.3293899768618249 -0.15096084258290954) (0.5828196983446509 0.25849029568712)) ((0.812202540641193 -0.11184197206522616) (0.15848250430732191 -0.6430889165946101)) ((0.11485801714720949 -0.5947039763981185) (2.0967696907123163 1.5739406307353667)) ((-1.2155667313066154 -0.0788907186675545) (-0.5370015951480657 -0.9389826755767056)) ((1.1135822821195298 2.0615151944968115) (-0.12781458419033903 -0.6610650787365732)) ((0.9204058759799587 0.4548745664437325) (1.1731175772240001 0.7983101141157147)) ((-0.6396361304834205 -0.3824428788775371) (2.8886908219621517 -0.21724718003484675)) ((-2.28164725253574 -1.539059245872718) (-2.0221508563851422 -1.3143582183575262)) ((0.8054750258294853 0.18328194616257343) (2.1869048512512923 0.004385813063344213)) ((-1.0680174216621912 -1.4429484844714036) (-1.1388181584751675 -0.34073363073750634)) ((-0.15223508021444146 -0.8798738363030526) (0.31988813220640744 1.768259964989704)) ((-0.27607076938671016 -0.5378203874028133) (-0.798202558481078 0.6548404585529902)) ((-0.025909178029129704 -0.4539424808749258) (1.4028559974173551 -0.10402558257971255)) ((1.133714490401998 0.7525193839072029) (0.6948666473357696 0.200124303732877)) ((-0.9341952625152087 -1.0270634383525232) (-0.3480020642869266 2.332526409290789)) ((-0.8047850319301858 0.5253207777044585) (0.014207769667089502 1.7276760038577184)) ((-0.09412105187478308 0.36838593155024063) (-0.6305364409808756 2.957865287211147)) ((0.28607877577788887 -0.42034355787691363) (0.6906142324649118 -1.579063276183114)) ((-0.346609513687254 0.38058859373936954) (0.6248300640590336 2.0776509498117983)))
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
()
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;template/indexed_vector</span>","value":"#'template/indexed_vector"}
;; <=

;; @@

;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-list'>(</span>","close":"<span class='clj-list'>)</span>","separator":" ","items":[{"type":"html","content":"<span class='clj-long'>0</span>","value":"0"},{"type":"html","content":"<span class='clj-long'>1</span>","value":"1"},{"type":"html","content":"<span class='clj-long'>2</span>","value":"2"},{"type":"html","content":"<span class='clj-long'>3</span>","value":"3"},{"type":"html","content":"<span class='clj-long'>4</span>","value":"4"},{"type":"html","content":"<span class='clj-long'>5</span>","value":"5"},{"type":"html","content":"<span class='clj-long'>6</span>","value":"6"},{"type":"html","content":"<span class='clj-long'>7</span>","value":"7"},{"type":"html","content":"<span class='clj-long'>8</span>","value":"8"}],"value":"(0 1 2 3 4 5 6 7 8)"}
;; <=

;; @@

;; @@
