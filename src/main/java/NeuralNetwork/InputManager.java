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
	 * Properties
	 */
	
	private char[] alphabet;
	
	private int inputLength;
	
	private int numberOfLabels;
	
	/**
	 * Getter and Setter
	 */
	
	public void setNumberOfLabels(int size){
		this.numberOfLabels = size;
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

	public DataSet readFiles(String Path) throws IOException{
		
		String PathToInputFile = Path + "data.txt";
		String PathToLabels = Path + "labels.txt";
		int InputFileLength = this.countLines(PathToInputFile);
		int LabelFileLength = this.countLines(PathToLabels);
		if(InputFileLength == LabelFileLength){
			BufferedReader brI = new BufferedReader(new FileReader(new File(PathToInputFile)));
			BufferedReader brL = new BufferedReader(new FileReader(new File(PathToLabels)));
			INDArray input = Nd4j.zeros(InputFileLength, (this.inputLength * this.alphabet.length));
			INDArray classes = Nd4j.zeros(InputFileLength, this.numberOfLabels);			
			//Step over each example
			for(int lineCounter = 0; lineCounter < InputFileLength; lineCounter++){
				//Feature
				String InputLineI = brI.readLine().toLowerCase();
				//Label
				String InputLineL = brL.readLine().toLowerCase();
				//Put character at the first position in labels file. It represents the class (0-4)
				int labelIdx = Character.getNumericValue(InputLineL.charAt(0));
				//Put a 1 at the index of the line and the index of the label
				classes.putScalar(new int[] {lineCounter, labelIdx}, 1);
				//Step over each character in features file
				int totalPosCounter = 0;
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
			brI.close();
			brL.close();
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
