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
	
	private String featuresPath;
	
	private String labelsPath;
	
	private int frameLength;
	
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
	
	private int halfInit = 3;
	
	private int miniBatchSize;
	
	private DataSet trainDataset;
	
	private DataSet testDataSet;
	
	private Evaluation eval;
	
	private DataSetIterator iterator;
	
	private MultipleEpochsIterator trainIterator;
	
	private NeuralNetwork network;
	
	/**
	 * TODO: Change to Builder Pattern
	 */
	
	public void setConfigurationParameters(String FeaturesPath, String LabelsPath, int FrameLength, String Alphabet, int BatchSize, int NumberOfCores, int NumberOfEpochs){
		this.featuresPath = FeaturesPath;
		this.labelsPath = LabelsPath;
		this.frameLength = FrameLength;
		this.alphabet = Alphabet;
		this.miniBatchSize = BatchSize;
		this.nCores = NumberOfCores;
		this.epochs = NumberOfEpochs;
		this.halfInit = epochs/10;
	}
	
	public boolean readData(){
		try{
			InputToOneHot encoder = InputToOneHot.getInstance();
			encoder.setAlphabet(this.alphabet);
			encoder.setFrameLength(this.frameLength);
			this.trainDataset = encoder.readFiles2D(this.featuresPath, this.labelsPath);
			return (this.trainDataset == null) ? false : true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	public void trainNetwork(){
		this.network = network.getInstance();
		iterator = new ListDataSetIterator(trainDataset.asList(), this.miniBatchSize);
		trainIterator = new MultipleEpochsIterator(this.epochs, this.iterator, this.nCores);
		network.run(trainIterator);
	}
	
	public double testNetwork(){
		double hitRatio = 0.00;
		
		iterator.reset();
		//TestData should be its own dataset untouched from training set
		
		return hitRatio;
	}
}
