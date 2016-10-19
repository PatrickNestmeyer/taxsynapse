package NeuralNetwork;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

public class LabeledInputSeries {
	
	static private LabeledInputSeries uniqueInstance = null;
	
	public static LabeledInputSeries getInstance(){
		if(uniqueInstance == null) {
			synchronized(LabeledInputSeries.class){
				if(uniqueInstance == null){
					uniqueInstance = new LabeledInputSeries();
				}
			}
		}
		return uniqueInstance;
	}
	
	private LabeledInputSeries(){}
	
	private char[] alphabet;
	
	private INDArray input;
	
	private INDArray labels;
	
	public void setAlphabet(String alphabet){
		this.alphabet = alphabet.toCharArray();
	}
	
	public void setAlphabet(char[] alphabet){
		this.alphabet = alphabet;
	}
	
	public char[] getAlphabet(){
		return this.alphabet;
	}
	
	public boolean readFiles(String PathToInputFile, String PathToLabels, int FrameLength){
		try{
			int InputFileLength = this.countLines(PathToInputFile);
			int LabelFileLength = this.countLines(PathToLabels);
			if(InputFileLength == this.countLines(PathToLabels)){
				input = Nd4j.zeros(InputFileLength, FrameLength);
				labels = Nd4j.zeros(InputFileLength, 1);
				this.transformInputToINDArray(PathToInputFile, PathToLabels, InputFileLength);
				return true;
			}else{
				System.out.println("Failed to load data");
				System.out.println("Inputfile and labelfile has different lengths.");
			}
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	private void transformInputToINDArray(String PathI, String PathL, int length) throws IOException{
		BufferedReader brI = new BufferedReader(new FileReader(new File(PathI)));
		BufferedReader brL = new BufferedReader(new FileReader(new File(PathL)));
		String InputLine = "";
		
		for(int i = 0; i < length; i++){
			InputLine = brI.readLine().toLowerCase();
			for(int j = 0; j < InputLine.length(); j++){
				this.input.putScalar(new int[] {i, j}, this.encodeCharacterEmbedding(InputLine.charAt(j)));
			}
			this.labels.putScalar(new int[] {i, 0}, Float.parseFloat(brL.readLine()));
		}
	}
	
	public DataSetIterator giveInputAsDataSetIterator(int batchSize){
		DataSet dataSet = new DataSet(this.input, this.labels);
		return new ListDataSetIterator(dataSet.asList(), batchSize);
	}
	
	private float encodeCharacterEmbedding(char c){
		for(int i = 0; i < this.alphabet.length; i++){
			if(this.alphabet[i] == c)
				//Character found in alphabet => return encoding value
				return (i + 1f);
		}
		//Character not in the alphabet
		return 0f;
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
