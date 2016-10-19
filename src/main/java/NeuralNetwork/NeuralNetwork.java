package NeuralNetwork;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

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
		
		int seed = 12345;
		int iterations = 1;
		
		int kernelSize = 7;
		int stride = 1;
		
		int numberOfChannels = 1;
		
		conf = new NeuralNetConfiguration.Builder()
				.seed(seed)
				.weightInit(WeightInit.DISTRIBUTION)
				.dist(new NormalDistribution(0.0, 0.02))
				.activation("relu")
				.updater(Updater.NESTEROVS)
				.iterations(iterations)
				//.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.learningRate(1e-2)
				//.biasLearningRate(1e-2*2)
				//.learningRateDecayPolicy(LearningRatePolicy.Step)
				//.lrPolicyDecayRate(0.1)
				//.lrpolicySteps(100000)
				.regularization(true)
				.l2(0.005)
				.momentum(0.9)
				.miniBatch(false)
				.list()
				.layer(0, new ConvolutionLayer.Builder(new int[] {7, 1}, new int[] {1, 1} , new int[] {0, 0})
						.name("ConvInit")
						.nIn(1)
						.nOut(252)
						.activation("relu")
						.build())
				.layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
						.name("FirstPooling")
						.kernelSize(3,1)
						.stride(3,1)
						.build())
				.layer(2, new DenseLayer.Builder()
						.name("FirstFully")
						.nOut(256)
						.dropOut(0.5)
						.build())
				.layer(3, new OutputLayer.Builder()
						.nOut(4)
						.activation("softmax")
						.build())
				.backprop(true)
				.pretrain(false)
				.cnnInputSize(258, 1, 1).build();
		
		this.network = new MultiLayerNetwork(conf);
	}
	
	@SuppressWarnings("deprecation")
	public void setupNetworkConfigurationSmall(){
		
		int seed = 12345;
		int iterations = 1;
		
		int kernelSize = 7;
		int stride = 1;
		
		int numberOfChannels = 1;
		
		conf = new NeuralNetConfiguration.Builder()
				.seed(seed)
				.weightInit(WeightInit.DISTRIBUTION)
				.dist(new NormalDistribution(0.0, 0.02))
				.activation("relu")
				.updater(Updater.NESTEROVS)
				.iterations(iterations)
				//.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.learningRate(1e-2)
				//.biasLearningRate(1e-2*2)
				//.learningRateDecayPolicy(LearningRatePolicy.Step)
				//.lrPolicyDecayRate(0.1)
				//.lrpolicySteps(100000)
				.regularization(true)
				.l2(0.005)
				.momentum(0.9)
				.miniBatch(false)
				.list()
				
				.layer(0, new ConvolutionLayer.Builder(1, 7)
						.name("ConvInit")
						.stride(1,1)
						.nIn(1)
						.nOut(252)
						.activation("relu")
						.build())
				.layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
						.name("Pooling1")
						.kernelSize(1,3)
						.stride(1,3)
						.build())
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
				.layer(2, new OutputLayer.Builder()
						.nOut(4)
						.activation("softmax")
						.build())
						
				.backprop(true)
				.pretrain(false)
				.cnnInputSize(1, 258, 1).build();
		
		this.network = new MultiLayerNetwork(conf);
	}
	
	public void run(DataSetIterator iterator){
		
		network.init();
		network.setListeners(new ScoreIterationListener(1));
		iterator.reset();
		network.fit(iterator);
		//network.setListeners(new ScoreIterationListener(1));
	}
}
