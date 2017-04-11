package NeuralNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.datasets.iterator.DataSetIterator;
import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.listener.EarlyStoppingListener;
import org.deeplearning4j.earlystopping.saver.LocalFileModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxScoreIterationTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

import Application.Config;

public class NeuralNetwork {
	
	/**
	 * DL4J Objects
	 */
	
	private MultiLayerConfiguration conf;
	
	private MultiLayerNetwork network;
	
	private ExportManager manager;
	
	/**
	 * Properties for size and dimension of NN
	 */
	
	private int alphabetSize;
	
	private int numberOfInputs;
	
	private int numberOfOutputs;
	
	private int numberOfFeatureMaps;
	
	private int firstFully;
	
	private int secondFully;
	
	private final int depth = 1;
	
	private final int convBigKernel = 7;
	
	private final int convSmallKernel = 3;
	
	private final int convStride = 1;
	
	private final int poolKernel = 3;
	
	private final int poolStride = 3;
	
	private final int nChannels = 1;
	
	/**
	 * Fixed Properties
	 */
	
	private int seed = 123;
	
	private final int iterations = 1;
	
	private final String activationFunction = "relu";
	
	private final boolean regularization = true;
	
	private final double dropOut = 0.5;
	
	private double normalDistributionLower;
	
	private double normalDistributionUpper;
	
	/**
	 * Lerning parameters
	 */
	
	private double learningRate = 0.01;
	
	private double momentum = 0.9;
	
	private double regularizationRate = 1e-3;
	
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
	private NeuralNetwork(){
		manager = ExportManager.getInstance();
	}
	
	public void setAlphabetSize(int size){
		this.alphabetSize = size;
	}
	
	public void setOutputSize(int size){
		this.numberOfOutputs = size;
	}
	
	public void setInputSize(int size){
		this.numberOfInputs = size;
	}
	
	public void setNetworkSize(String dim){
		this.normalDistributionLower = 0.0;
		switch (dim) {
		case "large":
			this.normalDistributionUpper = 0.02;
			this.numberOfFeatureMaps = 1024;
			this.secondFully = 2048;
			break;
		case "medium":
			this.normalDistributionUpper = 0.035;
			this.numberOfFeatureMaps = 512;
			this.secondFully = 1536;
			break;
		default:
			this.normalDistributionUpper = 0.05;
			this.numberOfFeatureMaps = 50;
			this.secondFully = 256;
			break;
		}
		this.firstFully = ((this.numberOfInputs-96)/27)*this.numberOfFeatureMaps;
	}
	
	public void setLearningRate(double LearningRate){
		this.learningRate = LearningRate;
		if(LearningRate > 1.0)
			this.learningRate = 1.0;
		if(LearningRate < 0.00)
			this.learningRate = 0.00;
	}
	
	public void setMomentum(double Momentum){
		this.momentum = Momentum;
		if(Momentum > 1.0)
			this.momentum = 1.0;
		if(Momentum < 0.00)
			this.momentum = 0.00;
	}
	
	public void setRegularizationRate(double Rate){
		this.regularizationRate = Rate;
		if(Rate >= 0)
			this.regularizationRate = 1e-3;
	}
	
	public void setupNetworkConfiguration(){
		
		conf = new NeuralNetConfiguration.Builder()
				.seed(this.seed)
				.weightInit(WeightInit.XAVIER)
				.dist(new NormalDistribution(this.normalDistributionLower, this.normalDistributionUpper))
				.activation(this.activationFunction)
				.updater(Updater.NESTEROVS)
				.iterations(this.iterations)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				//TODO: start learning rate and momentum. Half it every three epochs but not implemented yet
				.learningRate(this.learningRate)
				.momentum(this.momentum)
				.regularization(this.regularization)
				.l2(this.regularizationRate)
				.list()
				// 258 - 7 + 1 = 252
				.layer(0, this.createConvolutionLayer("Conv1", this.convBigKernel, this.convStride, this.nChannels, this.numberOfFeatureMaps, this.alphabetSize))
				// 252 : 3 = 84
				.layer(1, this.createPoolingLayer("Pool1", this.poolKernel, this.poolStride, this.depth))
				
				// 84 - 7 + 1 = 78
				.layer(2, this.createConvolutionLayer("Conv2", this.convBigKernel, this.convStride, this.numberOfFeatureMaps, this.numberOfFeatureMaps, this.depth))
				// 78 : 3 = 26
				.layer(3, this.createPoolingLayer("Pool2", this.poolKernel, this.poolStride, this.depth))
				
				// 26 - 3 + 1 = 24
				.layer(4, this.createConvolutionLayer("Conv3", this.convSmallKernel, this.convStride, this.numberOfFeatureMaps, this.numberOfFeatureMaps, this.depth))
				
				// 24 - 3 + 1 = 22
				.layer(5, this.createConvolutionLayer("Conv4", this.convSmallKernel, this.convStride, this.numberOfFeatureMaps, this.numberOfFeatureMaps, this.depth))
				// 22 - 3 + 1 = 20
				.layer(6, this.createConvolutionLayer("Conv5", this.convSmallKernel, this.convStride, this.numberOfFeatureMaps, this.numberOfFeatureMaps, this.depth))
				// 20 - 3 + 1 = 18
				.layer(7, this.createConvolutionLayer("Conv6", this.convSmallKernel, this.convStride, this.numberOfFeatureMaps, this.numberOfFeatureMaps, this.depth))
				
				// 18 : 3 = 6
				.layer(8, this.createPoolingLayer("Pool3", this.poolKernel, this.poolStride, this.depth))
				
				//(258-96)/27 = 6
				.layer(9, this.createFullyConnectedLayer("Fully1", this.firstFully, this.dropOut))
				.layer(10, this.createFullyConnectedLayer("Fully2", this.secondFully, this.dropOut))
				
				.layer(11, this.createOutputLayer("Output", this.secondFully, this.numberOfOutputs))
		        .backprop(true)
		        .pretrain(false)
		        .setInputType(InputType.convolutionalFlat(this.alphabetSize, this.numberOfInputs, this.depth))
				.build();
		
		this.network = new MultiLayerNetwork(conf);
	}
	
	public void run(MultipleEpochsIterator iterator){
		
		List<String> metadata = this.getMetadata();
		
		String PATH_OF_CURRENT_RUN = manager.createNetworkSettingFolder(Config.PATH_TO_NETWORK_OUTPUT);
		
		network.init();
		network.setListeners(new ScoreIterationListener(1));
		//network.setListeners(new FlowIterationListener(1));
		
		manager.saveNetworkInitialSettings(PATH_OF_CURRENT_RUN, network, metadata);
		
		network.fit(iterator);
		
		manager.saveNetworkEpochSetting(PATH_OF_CURRENT_RUN, network, 1);
	}
	
	public Map<Integer, Double> run(int maxEpochs, double maxScore, int miniBatch, int cores, org.nd4j.linalg.dataset.api.iterator.DataSetIterator train, org.nd4j.linalg.dataset.api.iterator.DataSetIterator test){
		
		List<String> metadata = this.getMetadata();
		String PATH_OF_CURRENT_RUN = manager.createNetworkSettingFolder(Config.PATH_TO_NETWORK_OUTPUT);
		
		//Maybe worn package imported
        String directory = FilenameUtils.concat(PATH_OF_CURRENT_RUN, "DL4JEarlyStoppingExample/");
        EarlyStoppingModelSaver saver = new LocalFileModelSaver(directory);
		
		EarlyStoppingConfiguration esConf = new EarlyStoppingConfiguration.Builder()
				.epochTerminationConditions(new MaxEpochsTerminationCondition(maxEpochs))
				.evaluateEveryNEpochs(1)
				.iterationTerminationConditions(new MaxScoreIterationTerminationCondition(maxScore))
				.scoreCalculator(new DataSetLossCalculator(test, true))
				.modelSaver(saver)
				.build();
				
		EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, conf, train);
		
		trainer.setListener(new EarlyStoppingListener<MultiLayerNetwork>(){

			@Override
			public void onStart(EarlyStoppingConfiguration<MultiLayerNetwork> esConfig, MultiLayerNetwork net) {
				System.out.println("Start early stopping run");
			}
			
			@Override
			public void onEpoch(int epochNum, double score, EarlyStoppingConfiguration<MultiLayerNetwork> esConfig,
					MultiLayerNetwork net) {
				System.out.println("Epoch No. " + epochNum + " completed:");
				System.out.println("Score is " + score);
			}

			@Override
			public void onCompletion(EarlyStoppingResult<MultiLayerNetwork> esResult) {
				System.out.println("Termination reason: " + esResult.getTerminationReason());
				System.out.println("Termination details: " + esResult.getTerminationDetails());
				System.out.println("Total epochs: " + esResult.getTotalEpochs());
				System.out.println("Best epoch number: " + esResult.getBestModelEpoch());
				System.out.println("Score at best epoch: " + esResult.getBestModelScore());
			}
		});
		
		//manager.saveNetworkInitialSettings(PATH_OF_CURRENT_RUN, network, metadata);
		
		EarlyStoppingResult result = trainer.fit();
		
		List<String> criterias = new ArrayList<String>();
		
		manager.saveEvaluation(PATH_OF_CURRENT_RUN, criterias);
		
		return result.getScoreVsEpoch();
	}

	
	public void saveNetworkParameters(){
		//TODO: Create folder output in resources
		//TODO: Create folder structure: for each run a folder with timestamp
		//TODO: Inside: Create a txt-file with metadata (learning parameters, results and duration of training) and a xml with the serialization of the network after each epoch.
	}
	
	public double test(DataSet testSet)
	{
		INDArray n = network.getOutputLayer().params();
		
		double hit = 0;
		INDArray realLabelOneHot = testSet.getLabels();
		for(int i = 0; i < testSet.numExamples(); i++){
			int realLabel = 0;
			int predict = network.predict(testSet.getFeatures().getRow(i))[0];
			INDArray output = network.output(testSet.getFeatures().getRow(i));
			for(int j = 0; j < realLabelOneHot.getRow(i).length(); j++){
				if(realLabelOneHot.getDouble(i, j) == 1.00)
					realLabel = j;
			}
			System.out.println("For Input " + (i+1) + ", " + predict + " was predicted. The real label is " + realLabel);
			if(realLabel == predict)
				hit++;
		}
		
		double returnValue = hit / testSet.numExamples(); 
			
		return returnValue;
	}
	
	private List<String> getMetadata(){
		List<String> metadata = new ArrayList<String>();
		metadata.add("Size of alphabet: " + this.alphabetSize);
		metadata.add("Number of Inputs: " + this.numberOfInputs);
		metadata.add("Number of feature maps: " + this.numberOfFeatureMaps);
		metadata.add("Number of Outputs: " + this.numberOfOutputs);
		metadata.add("Lerning rate: " + this.learningRate);
		metadata.add("Momentum: " + this.momentum);
		metadata.add("Regularization: " + this.regularizationRate);
		return metadata;
	}
	
	private ConvolutionLayer createConvolutionLayer(String Name, int KernelSize, int Stride, int In, int Out, int Depth){
		return new ConvolutionLayer.Builder()
				.name(Name)
				.kernelSize(new int[] {Depth, KernelSize})
				.stride(new int[] {Depth, Stride})
				.nIn(In)
				.nOut(Out)
				.build();
	}
	
	private SubsamplingLayer createPoolingLayer(String Name, int KernelSize, int Stride, int Depth){
		return new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
				.name(Name)
				//= 1 x 252
				.kernelSize(new int[] {Depth, KernelSize})
				.stride(new int[] {Depth, Stride})
				.build();
	}
	
	private DenseLayer createFullyConnectedLayer(String Name, int NumberOfNodes, double Dropout){
		return new DenseLayer.Builder()
				.name(Name)
				.nOut(NumberOfNodes)
				.dropOut(Dropout)
				.build();
	}
	
	private OutputLayer createOutputLayer(String Name, int InputSize, int OutputSize){
		return new OutputLayer.Builder()
				.name(Name)
				.nIn(InputSize)
				.nOut(OutputSize)
				.activation("softmax")
				.build();
	}
}
