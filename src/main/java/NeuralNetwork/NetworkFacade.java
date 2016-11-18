package NeuralNetwork;


import org.nd4j.linalg.dataset.DataSet;

import java.util.List;

import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

public class NetworkFacade {
	
	/**
	 * Private Members
	 */
	
	private InputManager encoder;
	
	private NeuralNetwork network;
	
	private String alphabet;
	
	private int inputLength;
	
	private int outputLength;
	
	private DataSet trainDataset;
	
	private DataSet testDataset;
	
	private MultipleEpochsIterator trainIterator;
	
	/**
	 * Singleton properties and methods
	 */
	
	static private NetworkFacade uniqueInstance = null;
	public static NetworkFacade getInstance(){
		if(uniqueInstance == null) {
			synchronized(NetworkFacade.class){
				if(uniqueInstance == null){
					uniqueInstance = new NetworkFacade();
				}
			}
		}
		return uniqueInstance;
	}
	private NetworkFacade(){}
	
	public void setProperties(String Alphabet, int InputLength, int OutputLength, String NetworkSize){
		
		//Data information
		this.alphabet = Alphabet;
		this.inputLength = InputLength;
		this.outputLength = OutputLength;
		
		this.encoder = InputManager.getInstance();
		
		//Network parameters
		this.network = NeuralNetwork.getInstance();
		network.setAlphabetSize(Alphabet.length());
		network.setInputSize(InputLength);
		network.setOutputSize(OutputLength);
		network.setNetworkSize(NetworkSize);
	}
	
	public boolean readData(String Path){
		/* Structure of data must in the shape of:
		 * 
		 * Path => train	=> data.txt
		 * 					=> labels.txt
		 * 
		 * 		=> test		=> data.txt
		 * 					=> labels.txt
		 */
		try{
			encoder.setAlphabet(this.alphabet);
			encoder.setInputLength(this.inputLength);
			encoder.setNumberOfLabels(outputLength);
			this.trainDataset = encoder.readFiles(Path+"train/");
			this.testDataset = encoder.readFiles(Path+"test/");
			return (this.trainDataset == null) ? false : true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public void build(double learningRate, double momentum, double regularizationRate){
		network.setLearningRate(learningRate);
		network.setMomentum(momentum);
		network.setRegularizationRate(regularizationRate);
		this.network.setupNetworkConfiguration();
	}
	
	public void train(int Minibatch, int Epochs, int NumberOfCores){
		trainIterator = new MultipleEpochsIterator(Epochs, new ListDataSetIterator(trainDataset.asList(), Minibatch), NumberOfCores);
		network.run(trainIterator);
	}
	
	public double test(){
		return network.test(testDataset);
	}
}
