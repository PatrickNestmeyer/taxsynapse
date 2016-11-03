package NeuralNetwork;

import java.util.List;

import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
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
		this.network = new MultiLayerNetwork(conf);
	}
	
	public void run(MultipleEpochsIterator iterator){
		
		network.init();
		network.fit(iterator);
	}
	
	public double test(DataSetIterator testIterator){
		double hitRatio = 0.00;
		
		int counter = 0;
		while(testIterator.hasNext()){
			DataSet elem = testIterator.next().get(counter);
			
			String label = elem.get(0).getLabels().toString();
			List<String> predict = this.network.predict(elem);
			if(label == predict.get(0))
				hitRatio++;
			counter++;
		}
		
		return (hitRatio/counter);
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
