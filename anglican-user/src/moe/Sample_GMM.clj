;; gorilla-repl.fileformat = 1

;; **
;;; # Gaussian Mixture Model
;;; 
;;; Gaussian Mixture Model is a model that views data as probabilistic sum of several multivariate normal random variable.
;;; 
;;; In machine learning technique, it requires iterative optimization technique, however due to inference engine of anglican, we can directly implement GMM.
;; **

;; @@
(ns moe.Sample_GMM)
(use 'nstools.ns)
(ns+ template
  (:like anglican-user.worksheet))
;; @@

;; @@
(defn zero_vector [m]
  (repeat m 0)
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
(defdist normal_vector
  "N dimensional independent normal vector"
  [n mean std]  ; distribution parameters
  []   ; auxiliary bindings
     (sample* [this] (map (fn [k] (sample* (normal mean std))) (repeat n 0)))
     (observe* [this value] (reduce + (map (fn [v] (observe* (normal mean std) v)) value) 0))
  )
;; @@

;; @@
(defn zero_matrix [m n]
  (repeat m (repeat n 0)
  ))

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
(defdist normal_matrix
  "m*n independent normal matrix"
  [m n mean std]  ; distribution parameters
  []   ; auxiliary bindings
     (sample* [this] (map (fn [l] (map (fn [k] (sample* (normal mean std))) l)) (zero_matrix m n)))
     (observe* [this value] (reduce + (map (fn [l] (reduce + (map (fn [v] (observe* (normal mean std) v)) l) 0)) value) 0))
  )
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
; This is wrong implementation, however works well. To 'learn' GMM, we need our dataset as input.
(with-primitive-procedures [template/zero_matrix template/mat_vec_mult]
  (defquery gaussian-mixture-model [dim lambda mean_mean mean_std std_mean std_std]
    (let [n (sample (poisson lambda))
          pi (sample (dirichlet (repeat n 1)))
          mean_vectors (map (fn [kkk] (map (fn [kkkk] (sample (normal mean_mean mean_std))) (repeat dim 0))) (repeat n 0))
          covariance_matrices (map (fn [kkk]
                                (map (fn [l]
                                  (map (fn [kkkk] (sample (normal std_mean std_std))) l)
                                )
                                (zero_matrix dim dim)
                                )) (repeat n 0)
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
          let [coeff-vec (map (fn [kkk] (sample (normal 0 1))) (repeat dim 0))]
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
(with-primitive-procedures [template/zero_matrix template/mat_vec_mult template/indexed_vector]
  (defquery gaussian-mixture-model [dim data lambda mean_mean mean_std std_mean std_std]
    (let [n (sample (poisson lambda))
          pi (sample (dirichlet (repeat n 1)))
          mean_vectors (map (fn [kkk] (map (fn [kkkk] (sample (normal mean_mean mean_std))) (repeat dim 0))) (repeat n 0))
          covariance_matrices (map (fn [kkk]
                                (map (fn [l]
                                  (map (fn [kkkk] (sample (normal std_mean std_std))) l)
                                )
                                (zero_matrix dim dim)
                                )) (repeat n 0)
                              )
          model {:n n, :pi pi, :mean_vectors mean_vectors, :covariance_matrices covariance_matrices}
          ]
      (loop [rest_data data chooser_list (list)]
        (if (= rest_data (list))
          (list model chooser_list)
          (let [chooser (sample (discrete pi))
                mean_vector (nth mean_vectors chooser)
                covariance_matrix (nth covariance_matrices chooser)]
            (let [coeff_vec (map (fn [kkk] (sample (normal 0 1))) (repeat dim 0))
                  result (map + mean_vector (mat_vec_mult covariance_matrix coeff_vec dim dim))]
                (map (fn [i] (observe (normal (nth result i) 1) (nth (first rest_data) i))) (indexed_vector dim))
                (recur (rest rest_data) (conj chooser_list chooser))
              )
            )
          )
        )
      )
    )
  )
;; @@

;; @@
; Some error produced
; 1. With large enough lambda, we have index out of bounds exception.
; 2. With low probability, we have NumberIsTooLargeException.


(def samples_gmm
  (let [s (doquery :importance gaussian-mixture-model [2 (list (list 0.0 0.0) (list 1.0 1.0) (list 2.0 2.0)) 2 0 1 0 1])]
    (map :result (take 1 s))))

(println samples_gmm)
;; @@
