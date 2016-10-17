package NeuralNetwork;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;

public class NetworkFacade {

	/**
	 * Configuration Parameters
	 */
	
	private String featuresPath;
	
	private String labelsPath;
	
	private int frameLength;
	
	private String alphabet;
	
	private int batchSize;
	
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
	
	private LabeledInputSeries input;
	
	private ListDataSetIterator iterator;
	
	/**
	 * TODO: Change to Builder Pattern
	 */
	
	public void setConfigurationParameters(String FeaturesPath, String LabelsPath, int FrameLength, String Alphabet, int BatchSize){
		this.featuresPath = FeaturesPath;
		this.labelsPath = LabelsPath;
		this.frameLength = FrameLength;
		this.alphabet = Alphabet;
		this.batchSize = BatchSize;
	}
	
	public boolean readData(){
		input = LabeledInputSeries.getInstance();
		input.setAlphabet(this.alphabet);
		if(input.readFiles(this.featuresPath, this.labelsPath, this.frameLength))
		{
			this.iterator = input.giveInputAsDataSetIterator(this.batchSize);
			return true;
		}
		return false;
	}
	
	public void runNetwork(){
		
	}
}
