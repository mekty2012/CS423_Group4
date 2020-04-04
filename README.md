# CS423_Group4
This repository is sourceforge for group project in CS423, probprog in KAIST.

We are considering implementing GMM-like algorithm in anglican. It may be changed.

## Some references
iff condition for covariance matrix : <http://www.fepress.org/wp-content/uploads/2014/06/ch7-iff-covariance_correlation_matrix.pdf>

Sampling from multivariate normal : <https://en.wikipedia.org/wiki/Multivariate_normal_distribution#Computational_methods>

Some other methods for creating random matrix : <https://en.wikipedia.org/wiki/Random_matrix>

## Outline of algorithm
0. Assume m-dimensional data set.
1. First, sample n from poisson(lambda), where lambda is given as parameter.
2. Let pi_i for i=1...n, be weight of each multinomral.
3. These procedure is sampling multivariate normal. Then for i=1...n, do
  - Create m-length vector. (This will be mean vector)
  - Create m* m matrix that is positive semi-definite, symmetric. This condition is proven in reference, and creating these matrix can be done by MM^T or other method using SVD. (This will be covariance matrix)
  - With these vector and matrix, create multivariate normal random variable.
4. This query returns following. 
  - distribution of n
  - distribution of pi_i
  - distribution of each mean vector & covariance matrix.
5. Give regularization to n. 
6. For each data, 'observe' it. 
