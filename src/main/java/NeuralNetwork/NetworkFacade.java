package NeuralNetwork;

import org.nd4j.linalg.dataset.DataSet;

import java.util.Collection;

import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.deeplearning4j.datasets.iterator.IteratorDataSetIterator;
import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

public class NetworkFacade {

	/**
	 * Configuration Parameters
	 */
	
	private String path;
	
	private int inputLength;
	
	private String alphabet;
	
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
	
	/**
	 * properties of the neural network
	 */
	
	private int nCores;
	
	private int epochs = 30;
	
	//private int halfInit = 3;
	
	private int miniBatchSize;
	
	private DataSet trainDataset;
	
	private DataSet testDataset;
	
	private MultipleEpochsIterator trainIterator;
	
	private DataSetIterator testIterator;
	
	private NeuralNetwork network;
	
	/**
	 * TODO: Change to Builder Pattern
	 */
	
	public void setConfigurationParameters(String Path, int InputLength, String Alphabet, int BatchSize, int NumberOfCores, int NumberOfEpochs){
		
		/* Structure of data must in the shape of:
		 * 
		 * Path => train	=> data.txt
		 * 					=> labels.txt
		 * 
		 * 		=> test		=> data.txt
		 * 					=> labels.txt
		 */
		
		this.path = Path;
		this.inputLength = InputLength;
		this.alphabet = Alphabet;
		this.miniBatchSize = BatchSize;
		this.nCores = NumberOfCores;
		this.epochs = NumberOfEpochs;
		//this.halfInit = epochs/10;
	}
	
	public boolean readData(){
		try{
			InputToOneHot encoder = InputToOneHot.getInstance();
			
			encoder.setAlphabet(this.alphabet);
			encoder.setFrameLength(this.inputLength);
			
			this.trainDataset = encoder.readFiles2D(this.path+"train/");
			this.testDataset = encoder.readFiles2D(this.path+"test/");
			
			
			return (this.trainDataset == null) ? false : true;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public void configNetwork(){
		this.network = network.getInstance();
		this.network.setupNetworkConfiguration();
	}
	
	public void trainNetwork(){
		
		this.network = network.getInstance();
		
		trainIterator = new MultipleEpochsIterator(this.epochs, new ListDataSetIterator(trainDataset.asList(), this.miniBatchSize), this.nCores);
		
		
		
		network.run(trainIterator);
	}
	
	public double testNetwork(){
		this.network = network.getInstance();
		
		this.testIterator = new ListDataSetIterator(this.testDataset.asList(), miniBatchSize);
		
				
		return network.test(this.testIterator);
	}
}
