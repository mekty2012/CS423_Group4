# CS423_Group4
This repository is sourceforge for group project in CS423, probprog in KAIST.

We are considering implementing GMM-like algorithm in anglican. It may be changed.

1. Mixture of experts based on GMM.
2. Boolean function generator
3. Probabilistic Automata
4. Markov process based reinforcement learning

## Personal Opinion

One main failure in machine learning is creating optimizing algorithm. This is done by some general algorithm like gradient descent, EM(expectation-maximization), however has high cost, prone to local minima, even impossible to derive optimization algorithm when model is highly complex - for example, random language in course lecture. 

Then the probabilistic programming allows any strange models. This is one main advantage. 2, 3 well shows this feature. If we allow nested mixture of experts, 1 may also can show it.

AutoML is one of major challenge in machine learning society, where purpose is fully automate machine learning procedure. The problem is hyperparameter, for example degree in polynomial regression, number of mixtures in GMM, number of layer and size of dimension in deep neural network. These parameters are not differentiable, even semantic. 

Again, probabilistic programming can solve this problem. For example, we can nest hyperparameters so that they are tuned with training.
```
mu' ~ uniform-continuous(0, 10)
mu ~ normal(0, mu')
variance' ~ uniform-continuous(0, 10)
variance ~ exp(variance')
lambda' ~ uniform-continuous(0, 100)
lambda ~ poisson(lambda')
n ~ poisson(lambda)
means ~ n * (normal(mu, variance))
covariances ~ n*n* (normal(mu, variance))
```

This can be shown in any examples.

Other one is expressibility. Since our programming language allows recursion - which yields similar notion to turing completeness - the generative model expressed may not be expressed by mathematical models. This is one another benefit. I'm not sure what choice shows this well.

- is random process corecursion?
## Inverse problems and machine learning
Conditional Sampling from Invertible Generative Models with Applications to Inverse Problems <https://arxiv.org/abs/2002.11743>

A Novel Learnable Gradient Descent Type Algorithm for Non-convex Non-smooth Inverse Problems <https://arxiv.org/abs/2003.06748>

Solving Inverse Problems by Join Posterior Maximization with a VAE Prior <https://arxiv.org/abs/1911.06379>

Adaptive particle-based approximation of the Gibbs posterior for inverse problems <https://arxiv.org/abs/1907.01551>

Invertible generative models for inverse problems: mitigating representation error and dataset bias <https://arxiv.org/abs/1905.11672>

Variational Inference for Computational Imaging Inverse Problems <https://arxiv.org/abs/1904.06264>

Algorithmic Aspects of Inverse Problems Using Generative Models <https://arxiv.org/abs/1810.03587>

Probabilistic Numerical Methods for PDE-constrained Bayesian Inverse Problems <https://arxiv.org/abs/1701.04006>

Inverse Problems with Invariant Multiscale Statistics <https://arxiv.org/abs/1609.05502>

Inverse problems in approximate uniform generation <https://arxiv.org/abs/1211.1722>

Solving Inverse Problems with Piecewise Linear Estimators: From Gaussian Mixture Models to Structured Sparsity <https://arxiv.org/abs/1006.3056>

These papers are search results at arxiv.org, with search word "Inverse Problem" in field computer science.

Machine learning approach to inverse problem and unfolding procedure <https://ui.adsabs.harvard.edu/abs/2010arXiv1004.2006G/abstract>

Applying Machine Learning Algorithms to Solve Inverse Problems in Electrical Tomography <https://www.matec-conferences.org/articles/matecconf/abs/2018/69/matecconf_cscc2018_02016/matecconf_cscc2018_02016.html>

Inverse problems in machine learning: An
application to brain activity interpretation <https://iopscience.iop.org/article/10.1088/1742-6596/135/1/012085/pdf>

High Dimensional Data Clustering <https://hal.inria.fr/inria-00548591/document>
## Some references
iff condition for covariance matrix : <http://www.fepress.org/wp-content/uploads/2014/06/ch7-iff-covariance_correlation_matrix.pdf>

Sampling from multivariate normal : <https://en.wikipedia.org/wiki/Multivariate_normal_distribution#Computational_methods>

Some other methods for creating random matrix : <https://en.wikipedia.org/wiki/Random_matrix>

## Benefit of mixture of experts - using GMM

So mixture of experts is beneficial when applied to inverse problem. Simple reason is that mixture of experts may return multiple values - though probabilistic. Consider function y=x^2. then we know that inverse function is x=sqrt(y) or -sqrt(y). Then our mixture of experts divides space into two space, x>0 and x<0. And then it returns sqrt(y) for x>0, and -sqrt(y) for x<0. This is simple explanation of why mixture of experts works well in inverse problem.

## Outline of GMM algorithm
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
