# Project description

## Dataset

Dataset is image dataset, which is N*M pixel. It is gray scale image.

I considered using image dataset like CIFAR-10, STL-10 for training set, however some papers did not use this kinds of large sized dataset. Instead, they used few number of high resolution images. I'm not sure inference will be well done with such dataset.

## Model

The model is hierarchical mixture of experts, where gating model is given by GMM, experts are given by kernel.

Papers suggest kernel to be 8*8 sized.

## Measurement

The accuracy will be measured by PSNR.
