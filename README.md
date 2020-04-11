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

## Some rules
Before you commit, erase all outputs of code segment. You can do it by alt+g -> alt+z.

Do not commit unnecessary files like syntex from latex or clj~. If such file exists, add it to .gitignore.

Be sure to add simple description what you have done when you commit.

## Other literatures

A Novel Gaussian Mixture Model for Classification - IEEE Conference Publication - <https://ieeexplore.ieee.org/abstract/document/8914215>

Kernel Trick Embedded Gaussian Mixture Model <http://bigeye.au.tsinghua.edu.cn/english/paper/ALT03.pdf>

Fuzzy Gaussian Mixture Model <https://www.sciencedirect.com/science/article/abs/pii/S0031320311003852>

Mixture of experts: a literature survey <https://link.springer.com/article/10.1007/s10462-012-9338-y>
