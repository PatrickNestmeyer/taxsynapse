package NeuralNetwork;

import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class NeuralNetwork {
	
	/**
	 * Properties
	 */
	
	private MultiLayerConfiguration conf;
	
	private MultiLayerNetwork network;
	
	/**
	 * Singleton properties and methods
	 */
	
	static private NeuralNetwork uniqueInstance = null;
	public static NeuralNetwork getInstance(){
		if(uniqueInstance == null) {
			synchronized(NeuralNetwork.class){
				if(uniqueInstance == null){
					uniqueInstance = new NeuralNetwork();
				}
			}
		}
		return uniqueInstance;
	}
	private NeuralNetwork(){}
	
	@SuppressWarnings("deprecation")
	public void setupNetworkConfigurationReduced(){
		
		int seed = 123;
		int iterations = 1;
		
		int kernelSize = 7;
		int stride = 1;
		
		int numberOfChannels = 1;
		
		
		conf = new NeuralNetConfiguration.Builder()
				.seed(seed)
				.iterations(1)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.learningRate(0.1)
				.updater(Updater.NESTEROVS)
				.momentum(0.9)
				.list()
				.layer(0, new DenseLayer.Builder().nIn(258).nOut(1000)
						.weightInit(WeightInit.XAVIER)
						.activation("relu")
						.build())
				.layer(1, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
						.weightInit(WeightInit.XAVIER)
						.activation("softmax")
						.nIn(1000).nOut(4).build())
				.pretrain(false).backprop(true).build();
				
		this.network = new MultiLayerNetwork(conf);
	}
	
	public void setupNetworkConfiguration(){
		
		int numberOfInputs = 258;
		//Random number generator seed, for ability to reproduce
		int seed = 123;
		//This means not the number of iterations per epoch
		int iterations = 1;
		int convBigKernelSize = 7;
		int convSmallKernelSize = 3;
		int convStride = 1;
		int poolKernelSize = 3;
		int poolStride = 3;
		double normalDistributionLower = 0.00;
		double normalDistributionUpper = 0.02;
		String activationFunction = "relu";
		double learningRate = 0.01;
		double momentum = 0.9;
		
		
		conf = new NeuralNetConfiguration.Builder()
				.seed(seed)
				.weightInit(WeightInit.DISTRIBUTION)
				.dist(new NormalDistribution(normalDistributionLower, normalDistributionUpper))
				.activation(activationFunction)
				.updater(Updater.NESTEROVS)
				.iterations(iterations)
				//.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				//TODO: start learning rate and momentum. Half it every three epochs but not implemented yet
				.learningRate(learningRate)
				.momentum(momentum)
				.regularization(true)
				.miniBatch(false)
				.list()
				// 258 - 7 + 1 = 252
				.layer(0, this.createConvolutionLayer("Conv1", convBigKernelSize, convStride))
				// 252 : 3 = 84
				.layer(1, this.createPoolingLayer("Pool1", poolKernelSize, poolStride))
				// = 84
				.layer(2, this.createOutputLayer("Output", 84))
		        .backprop(true)
		        .pretrain(false)
		        .setInputType(InputType.convolutionalFlat(1, numberOfInputs, 1))
				.build();
				/*
				//size of kernel is 7 
				.layer(0, new ConvolutionLayer.Builder(1,7)
						.name("ConvInit")
						.stride(1,1)
						//Number of Channels in our example is just one
						.nIn(1)
						//Number of Filters in our example is just one
						.nOut(1)
						.build())
				.layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
						.name("Pooling1")
						//kernel and stride for pooling is 3 means a reduction from 3 to 1
						.kernelSize(1,3)
						.stride(1,3)
						.build())
				.layer(2, new OutputLayer.Builder()
						.name("Output")
						.nIn(84)
						//Number of outcomes (Network should make only one suggestion)
						.nOut(1)
						.activation("softmax")
						.build())
				
                .build();
                */
				/*
				.layer(2, new ConvolutionLayer.Builder(new int[] {7, 1}, new int[] {1, 1} , new int[] {0, 0})
						.name("Conv2")
						.nOut(78)
						.activation("relu")
						.build())
				.layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
						.name("Pooling2")
						.kernelSize(3,1)
						.stride(3,1)
						.build())
				
				.layer(4, new ConvolutionLayer.Builder(new int[] {3, 1}, new int[] {1, 1} , new int[] {0, 0})
						.name("Conv3")
						.nOut(24)
						.activation("relu")
						.build())
				.layer(5, new ConvolutionLayer.Builder(new int[] {3, 1}, new int[] {1, 1} , new int[] {0, 0})
						.name("Conv3")
						.nOut(22)
						.activation("relu")
						.build())
				.layer(6, new ConvolutionLayer.Builder(new int[] {3, 1}, new int[] {1, 1} , new int[] {0, 0})
						.name("Conv3")
						.nOut(20)
						.activation("relu")
						.build())
				
				.layer(7, new ConvolutionLayer.Builder(new int[] {3, 1}, new int[] {1, 1} , new int[] {0, 0})
						.name("Conv2")
						.nOut(18)
						.activation("relu")
						.build())
				.layer(8, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
						.name("Pooling2")
						.kernelSize(3,1)
						.stride(3,1)
						.build())
				
				.layer(9, new DenseLayer.Builder()
						.name("Reshape")
						.nOut(1536)
						.build())
				
				.layer(10, new DenseLayer.Builder()
						.name("Fully1")
						.nOut(256)
						.dropOut(0.5)
						.build())
				
				.layer(11, new DenseLayer.Builder()
						.name("Fully2")
						.nOut(256)
						.dropOut(0.5)
						.build())
				*/
				/*
				.layer(0, new DenseLayer.Builder()
						.nIn(258)
						.nOut(258)
						.weightInit(WeightInit.XAVIER)
						.activation("relu")
						.build())
				.layer(1, new OutputLayer.Builder()
						.nIn(258)
						.nOut(4)
						.activation("softmax")
						.build())
				*/	
				
		
		this.network = new MultiLayerNetwork(conf);
	}
	
	public void run(MultipleEpochsIterator iterator){
		
		network.init();
		network.fit(iterator);
	}
	
	private ConvolutionLayer createConvolutionLayer(String Name, int KernelSize, int Stride){
		//nIn = number of channels. 1 if 2-D Data, 3 for RGB-Pictures. No other parameter values are possible.
		//nOut = number of filters. In this context nOut means the number of filter-maps.
		//Both are 1 in this example
		//IMPORTANT: Do not change nIn or nOut
		return new ConvolutionLayer.Builder()
				.name(Name)
				.kernelSize(new int[] {1,KernelSize})
				.stride(new int[] {1,Stride})
				.nIn(1)
				.nOut(1)
				.build();
	}
	
	private SubsamplingLayer createPoolingLayer(String Name, int KernelSize, int Stride){
		return new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
				.name(Name)
				.kernelSize(new int[] {1,KernelSize})
				.stride(new int[] {1,Stride})
				.build();
	}
	
	private OutputLayer createOutputLayer(String Name, int InputSize){
		//Number of outcomes (Network should make only one suggestion)
		//IMPORTANT: This is the number of predictions per unit not the number of possible outcomes.
		return new OutputLayer.Builder()
				.name(Name)
				.nIn(InputSize)
				.nOut(1)
				.activation("softmax")
				.build();
	}
}
