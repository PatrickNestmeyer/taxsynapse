package NeuralNetwork;

import java.util.List;

import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
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
import org.deeplearning4j.ui.flow.FlowIterationListener;
import org.deeplearning4j.ui.weights.HistogramIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class NeuralNetwork {
	
	/**
	 * Properties
	 */
	
	private MultiLayerConfiguration conf;
	
	private MultiLayerNetwork network;
	
	private int alphabetSize;
	
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
	
	public void setAlphabetSize(int size){
		this.alphabetSize = size;
	}
	
	public void setupNetworkConfiguration3D(){
		
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
		double normalDistributionUpper = 0.05;
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
				.l2(1e-3)
				.miniBatch(false)
				.list()
				
				// 258 - 7 + 1 = 252
				.layer(0, this.createConvolutionInitLayer3D("Conv1", convBigKernelSize, convStride))
				// 252 : 3 = 84
				.layer(1, this.createPoolingLayer3D("Pool1", poolKernelSize, poolStride))
				/*
				// 84 - 7 + 1 = 78
				.layer(2, this.createConvolutionLayer3D("Conv2", convBigKernelSize, convStride))
				// 78 : 3 = 26
				.layer(3, this.createPoolingLayer3D("Pool2", poolKernelSize, poolStride))
				
				// 26 - 3 + 1 = 24
				.layer(4, this.createConvolutionLayer3D("Conv3", convSmallKernelSize, convStride))
				// 24 - 3 + 1 = 22
				.layer(5, this.createConvolutionLayer3D("Conv4", convSmallKernelSize, convStride))
				// 22 - 3 + 1 = 20
				.layer(6, this.createConvolutionLayer3D("Conv5", convSmallKernelSize, convStride))
				// 20 - 3 + 1 = 18
				.layer(7, this.createConvolutionLayer3D("Conv6", convSmallKernelSize, convStride))
				
				// 18 : 3 = 6
				.layer(8, this.createPoolingLayer3D("Pool3", poolKernelSize, poolStride))
				
				.layer(9, new DenseLayer.Builder().nOut(1024).dropOut(0.5).build())
				.layer(10, new DenseLayer.Builder().nOut(1024).dropOut(0.5).build())
				*/
				.layer(2, this.createOutputLayer3D("Output", 84))
		        .backprop(true)
		        .pretrain(false)
		        .setInputType(InputType.convolutionalFlat(alphabetSize, numberOfInputs, 1))
				.build();
		this.network = new MultiLayerNetwork(conf);
	}
	
	public void setupNetworkConfiguration2D(){
		
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
		double normalDistributionUpper = 0.05;
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
				.l2(1e-3)
				.miniBatch(false)
				.list()
				
				// 258 - 7 + 1 = 252
				.layer(0, this.createConvolutionInitLayer2D("Conv1", convBigKernelSize, convStride))
				// 252 : 3 = 84
				.layer(1, this.createPoolingLayer2D("Pool1", poolKernelSize, poolStride))
				
				// 84 - 7 + 1 = 78
				.layer(2, this.createConvolutionLayer2D("Conv2", convBigKernelSize, convStride))
				// 78 : 3 = 26
				.layer(3, this.createPoolingLayer2D("Pool2", poolKernelSize, poolStride))
				
				// 26 - 3 + 1 = 24
				.layer(4, this.createConvolutionLayer2D("Conv3", convSmallKernelSize, convStride))
				// 24 - 3 + 1 = 22
				.layer(5, this.createConvolutionLayer2D("Conv4", convSmallKernelSize, convStride))
				// 22 - 3 + 1 = 20
				.layer(6, this.createConvolutionLayer2D("Conv5", convSmallKernelSize, convStride))
				// 20 - 3 + 1 = 18
				.layer(7, this.createConvolutionLayer2D("Conv6", convSmallKernelSize, convStride))
				
				// 18 : 3 = 6
				.layer(8, this.createPoolingLayer2D("Pool3", poolKernelSize, poolStride))
				
				.layer(9, new DenseLayer.Builder().nOut(1024).dropOut(0.5).build())
				.layer(10, new DenseLayer.Builder().nOut(1024).dropOut(0.5).build())
				.layer(11, this.createOutputLayer2D("Output", 1024))
		        .backprop(true)
		        .pretrain(false)
		        .setInputType(InputType.convolutionalFlat(1, numberOfInputs, 1))
				.build();
		this.network = new MultiLayerNetwork(conf);
	}
	
	public void run(MultipleEpochsIterator iterator){
		
		network.init();
		network.setListeners(new ScoreIterationListener(1));
		//network.setListeners(new FlowIterationListener(1));
		network.fit(iterator);
	}
	
	public void run(INDArray features, INDArray labels){
		network.init();
		network.setListeners(new ScoreIterationListener(1));
		network.fit(features, labels);
		
	}
	
	public double test(DataSet testSet){
		int[] predict = network.predict(testSet.getFeatures());
		INDArray labels = testSet.getLabels();
		
		double hit = 0;
		for(int i = 0; i < testSet.numExamples(); i++){
			if(predict[i] == labels.getInt(i))
				hit++;
		}
		
		double returnValue = hit / testSet.numExamples(); 

		return returnValue;
	}
	
	private ConvolutionLayer createConvolutionInitLayer2D(String Name, int KernelSize, int Stride){
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
				.weightInit(WeightInit.DISTRIBUTION)
				.build();
	}
	
	private ConvolutionLayer createConvolutionLayer2D(String Name, int KernelSize, int Stride){
		//nIn = number of channels. 1 if 2-D Data, 3 for RGB-Pictures. No other parameter values are possible.
		//nOut = number of filters. In this context nOut means the number of filter-maps.
		//Both are 1 in this example
		//IMPORTANT: Do not change nIn or nOut
		return new ConvolutionLayer.Builder()
				.name(Name)
				.kernelSize(new int[] {1,KernelSize})
				.stride(new int[] {1,Stride})
				.nOut(1)
				.weightInit(WeightInit.DISTRIBUTION)
				.build();
	}
	
	private SubsamplingLayer createPoolingLayer2D(String Name, int KernelSize, int Stride){
		return new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
				.name(Name)
				.kernelSize(new int[] {1,KernelSize})
				.stride(new int[] {1,Stride})
				.build();
	}
	
	private OutputLayer createOutputLayer2D(String Name, int InputSize){
		//Number of outcomes (Network should make only one suggestion)
		//IMPORTANT: This is the number of predictions per unit not the number of possible outcomes.
		return new OutputLayer.Builder()
				.name(Name)
				.nIn(InputSize)
				.nOut(1)
				.activation("softmax")
				.build();
	}
	
	private ConvolutionLayer createConvolutionInitLayer3D(String Name, int KernelSize, int Stride){
		//nIn = number of channels. 1 if 2-D Data, 3 for RGB-Pictures. No other parameter values are possible.
		//nOut = number of filters. In this context nOut means the number of filter-maps.
		//Both are 1 in this example
		//IMPORTANT: Do not change nIn or nOut
		return new ConvolutionLayer.Builder()
				.name(Name)
				.kernelSize(new int[] {this.alphabetSize, KernelSize})
				.stride(new int[] {this.alphabetSize, Stride})
				.nIn(1)
				.nOut(1)
				.weightInit(WeightInit.DISTRIBUTION)
				.build();
	}
	
	private ConvolutionLayer createConvolutionLayer3D(String Name, int KernelSize, int Stride){
		//nIn = number of channels. 1 if 2-D Data, 3 for RGB-Pictures. No other parameter values are possible.
		//nOut = number of filters. In this context nOut means the number of filter-maps.
		//Both are 1 in this example
		//IMPORTANT: Do not change nIn or nOut
		return new ConvolutionLayer.Builder()
				.name(Name)
				.kernelSize(new int[] {this.alphabetSize, KernelSize})
				.stride(new int[] {this.alphabetSize, Stride})
				.nOut(1)
				.weightInit(WeightInit.DISTRIBUTION)
				.build();
	}
	
	private SubsamplingLayer createPoolingLayer3D(String Name, int KernelSize, int Stride){
		return new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
				.name(Name)
				.kernelSize(new int[] {1,KernelSize})
				.stride(new int[] {1,Stride})
				.build();
	}
	
	private OutputLayer createOutputLayer3D(String Name, int InputSize){
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
