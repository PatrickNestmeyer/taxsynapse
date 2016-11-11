package NeuralNetwork;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

public class InputManager {
	
	/**
	 * 
	 * Singleton Attributes an Methods
	 *  
	 */
	
	static private InputManager uniqueInstance = null;
	
	public static InputManager getInstance(){
		if(uniqueInstance == null) {
			synchronized(InputManager.class){
				if(uniqueInstance == null){
					uniqueInstance = new InputManager();
				}
			}
		}
		return uniqueInstance;
	}
	
	private InputManager(){}
	
	/**
	 * 
	 * Attributes
	 * 
	 */
	
	//Bunch of characters representing the allowed input features
	private char[] alphabet;
	
	//Featurelength 
	private int inputLength;
	
	//Each label represent an account number or a class of account numbers
	private List<String> labels;
	
	//The account description or text representing the label in natural language 
	//Need to be hashmap (expl: "4" => "AV")
	//private List<String> representation;
	
	/**
	 * 
	 * Getter and Setter
	 * 
	 */
	
	public void setLabels(List<String> labels){
		this.labels = labels;
	}
	
	public void setAlphabet(String alphabet){
		this.alphabet = alphabet.toCharArray();
	}
	
	public void setAlphabet(char[] alphabet){
		this.alphabet = alphabet;
	}
	
	public char[] getAlphabet(){
		return this.alphabet;
	}
	
	public void setInputLength(int frameLength){
		this.inputLength = frameLength;
	}
	
	/**
	 * Apply labels on the dataset LabelNames
	 * @param the DataSet where the labels will be applied on
	 * @return the DataSet where the labels are applied on
	 */
	
	public DataSet setTestLabelNames(DataSet set){
		List<String> labelNames = new ArrayList<String>();
		for (String label : this.labels) {
			labelNames.add(label);
		}
		set.setLabelNames(labelNames);
		return set;
	}
	
	public DataSet readFiles2D(String Path) throws IOException{
		
		String PathToInputFile = Path + "data.txt";
		String PathToLabels = Path + "labels.txt";
		
		int InputFileLength = this.countLines(PathToInputFile);
		int LabelFileLength = this.countLines(PathToLabels);
		if(InputFileLength == LabelFileLength){
			
			BufferedReader brI = new BufferedReader(new FileReader(new File(PathToInputFile)));
			BufferedReader brL = new BufferedReader(new FileReader(new File(PathToLabels)));
			String InputLineI = "";
			String InputLineL = "";
			
			INDArray input = Nd4j.zeros(InputFileLength, this.inputLength);
			//INDArray labels = Nd4j.zeros(InputFileLength, this.frameLength);
			INDArray labels = Nd4j.zeros(InputFileLength, 1);
			
			//Step over each example
			for(int lineCounter = 0; lineCounter < InputFileLength; lineCounter++){
				InputLineI = brI.readLine().toLowerCase();
				InputLineL = brL.readLine().toLowerCase();
				
				//Put character at the first position in labels file. It represents the class (0-4)
				labels.putScalar(new int[] {lineCounter, 0}, Character.getNumericValue(InputLineL.charAt(0)));
				
				//Step over each character in features file
				for(int characterCounter = 0; characterCounter < InputLineI.length(); characterCounter++){
					char cI = InputLineI.charAt(characterCounter);
					for(int alphabetCounter = 0; alphabetCounter < alphabet.length; alphabetCounter++){ 
						if(cI == alphabet[alphabetCounter]) 
							input.putScalar(new int[] {lineCounter, characterCounter}, alphabetCounter );
					}
				}
			}
			
			return new DataSet(input, labels);
			
		}else{
			return null;
		}
	}
	
	public DataSet readFiles3D(String Path) throws IOException{
		
		String PathToInputFile = Path + "data.txt";
		String PathToLabels = Path + "labels.txt";
		
		int InputFileLength = this.countLines(PathToInputFile);
		int LabelFileLength = this.countLines(PathToLabels);
		if(InputFileLength == LabelFileLength){
			
			BufferedReader brI = new BufferedReader(new FileReader(new File(PathToInputFile)));
			BufferedReader brL = new BufferedReader(new FileReader(new File(PathToLabels)));
			String InputLineI = "";
			String InputLineL = "";
			
			INDArray input = Nd4j.zeros(InputFileLength, this.inputLength, this.alphabet.length);
			//INDArray labels = Nd4j.zeros(InputFileLength, this.frameLength);
			INDArray labels = Nd4j.zeros(InputFileLength, 4);
			
			//Step over each example
			for(int lineCounter = 0; lineCounter < InputFileLength; lineCounter++){
				InputLineI = brI.readLine().toLowerCase();
				InputLineL = brL.readLine().toLowerCase();
				
				//Put character at the first position in labels file. It represents the class (0-4)
				labels.putScalar(new int[] {lineCounter, 0}, Character.getNumericValue(InputLineL.charAt(0)));
				
				//Step over each character in features file
				for(int characterCounter = 0; characterCounter < InputLineI.length(); characterCounter++){
					char cI = InputLineI.charAt(characterCounter);
					
					for(int alphabetPosition = 0; alphabetPosition < alphabet.length; alphabetPosition++){
						if(cI == alphabet[alphabetPosition])
							input.putScalar(new int[] {lineCounter, characterCounter, alphabetPosition}, 1);
					}
				}
			}
			
			return new DataSet(input, labels);
			
		}else{
			return null;
		}
	}

	public DataSet readFiles3DFlat(String Path) throws IOException{
		
		String PathToInputFile = Path + "data.txt";
		String PathToLabels = Path + "labels.txt";
		
		int InputFileLength = this.countLines(PathToInputFile);
		int LabelFileLength = this.countLines(PathToLabels);
		if(InputFileLength == LabelFileLength){
			
			BufferedReader brI = new BufferedReader(new FileReader(new File(PathToInputFile)));
			BufferedReader brL = new BufferedReader(new FileReader(new File(PathToLabels)));
			
			INDArray input = Nd4j.zeros(InputFileLength, (this.inputLength * this.alphabet.length));
			//INDArray labels = Nd4j.zeros(InputFileLength, this.frameLength);
			INDArray classes = Nd4j.zeros(InputFileLength, this.labels.size());
			
			//Step over each example
			for(int lineCounter = 0; lineCounter < InputFileLength; lineCounter++){
				
				//Feature
				String InputLineI = brI.readLine().toLowerCase();
				//Label
				String InputLineL = brL.readLine().toLowerCase();
				
				//Put character at the first position in labels file. It represents the class (0-4)
				classes.putScalar(new int[] {lineCounter, 0}, Character.getNumericValue(InputLineL.charAt(0)));
				for(int labelCounter = 0; labelCounter < labels.size(); labelCounter++){
					if(InputLineL.charAt(0) == this.labels.get(labelCounter).toCharArray()[0])
						classes.putScalar(new int[] {lineCounter, labelCounter}, 1);
				}
				
				int totalPosCounter = 0;
				//Step over each character in features file
				for(int characterCounter = 0; characterCounter < InputLineI.length(); characterCounter++){
					char cI = InputLineI.charAt(characterCounter);
					
					//Step over each alphabet character per character in features file
					for(int alphabetPosition = 0; alphabetPosition < alphabet.length; alphabetPosition++){
						if(cI == alphabet[alphabetPosition])
							input.putScalar(new int[] {lineCounter, totalPosCounter}, 1);
						totalPosCounter++;
					}
				}
			}
			
			return new DataSet(input, classes);
			
		}else{
			return null;
		}
	}
	
	private int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
}
