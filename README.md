# taxsynapse

This repository implements the Deep-Learning approach of Xiang Zhang [Text Understanding from scratch](https://arxiv.org/abs/1502.01710) and focuses on classification of digital invoices.

The artifact is not deployed anywhere. So if you want to use it you need to install it locally. The application consists of two subapplications called "data" and "network".

With the parameter "data" you can let the application read and transform training data. 

You can have a look at [src/main/resources/labeled/other](https://github.com/PatrickNestmeyer/taxsynapse/tree/master/src/main/resources/labeled/other) for examples. 
It is possible to change the input files for the network in the [config file](https://github.com/PatrickNestmeyer/taxsynapse/blob/master/src/main/java/Application/Config.java).

With the parameter "network" you can start the training of the network. Based on it's input config. You can edit the config for the network in the [config file](https://github.com/PatrickNestmeyer/taxsynapse/blob/master/src/main/java/Application/Config.java) too.
