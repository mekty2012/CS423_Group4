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

;; @@
(defn zero_vector [m]
  (if (= m 1)
    (list 0)
    (conj (zero_vector (- m 1)) 0)
    )
  )

(zero_vector 5)
;; @@

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

;; @@
(defn zero_matrix [m n]
  (if (= m 1)
    (list (zero_vector n))
    (conj (zero_matrix (- m 1) n) (zero_vector n))
    )
  )

(zero_matrix 3 4)
;; @@

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

;; @@
(defn indexed_vector [m]
  (if (= m 1)
  (list 0)
  (conj (map (fn [n] (+ n 1)) (indexed_vector (- m 1))) 0)
  )
  )
(indexed_vector 10)
;; @@

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

;; @@
(defn inner_prod [v1 v2]
  (reduce + 0 (map * v1 v2))
  )

(inner_prod (list 1 2 3 4) (list 1 2 3 4))
;; @@

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

;; @@
; m : a * b matrix, v : b vector
(defn mat_vec_mult [m v a b]
  (map (fn [l] (inner_prod l v)) m)
  )

(mat_vec_mult (list (list 1 2 3) (list 2 3 4) (list 3 4 5)) (list 1 -1 1) 3 3)
;; @@

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

;; @@
(def samples_gmm 
  (let [s (doquery :importance gaussian-mixture-model [4 2 0 1 0 1])]
    (map :result (take 1 s))))

(println samples_gmm)
;; @@
