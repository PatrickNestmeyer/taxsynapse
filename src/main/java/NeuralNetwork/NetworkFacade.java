package NeuralNetwork;


import org.nd4j.linalg.dataset.DataSet;
import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
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
	
	private boolean use3D = false;
	
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
	
	public void train3DModel(){
		this.use3D = true;
	}
	
	public boolean readData(){
		try{
			InputToOneHot encoder = InputToOneHot.getInstance();
			
			encoder.setAlphabet(this.alphabet);
			encoder.setFrameLength(this.inputLength);
			
			if(this.use3D){
				this.trainDataset = encoder.readFiles3DFlat(this.path+"train/");
				this.testDataset = encoder.readFiles3DFlat(this.path+"test/");
			}else{
				this.trainDataset = encoder.readFiles2D(this.path+"train/");
				this.testDataset = encoder.readFiles2D(this.path+"test/");
			}

			return (this.trainDataset == null) ? false : true;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public void configNetwork(){
		this.network = network.getInstance();
		if(this.use3D){
			this.network.setAlphabetSize(InputToOneHot.getInstance().getAlphabet().length);
			this.network.setupNetworkConfiguration3D();
		}else{
			this.network.setupNetworkConfiguration2D();
		}
	}
	
	public void trainNetwork(){
		
		System.out.println("rows " + trainDataset.getFeatures().size(0)); //number of Examples
		System.out.println("columns: " + trainDataset.getFeatures().size(1)); //number of Characters
		
		this.network = network.getInstance();
		trainIterator = new MultipleEpochsIterator(this.epochs, new ListDataSetIterator(trainDataset.asList(), this.miniBatchSize), this.nCores);
		
		System.out.println("total outcomes: " + trainIterator.totalOutcomes());
		System.out.println("total examples: " + trainIterator.totalExamples());
		
		network.run(trainIterator);
	}
	
	public double testNetwork(){
		this.network = network.getInstance();
		testDataset = InputToOneHot.getInstance().setTestLabelNames(testDataset);
		return network.test(testDataset);
	}
}
