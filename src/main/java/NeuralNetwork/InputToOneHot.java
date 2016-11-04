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

public class InputToOneHot {
	
static private InputToOneHot uniqueInstance = null;
	
	public static InputToOneHot getInstance(){
		if(uniqueInstance == null) {
			synchronized(InputToOneHot.class){
				if(uniqueInstance == null){
					uniqueInstance = new InputToOneHot();
				}
			}
		}
		return uniqueInstance;
	}
	
	private InputToOneHot(){}
	
	private char[] alphabet;
	
	private int frameLength;
	
	public void setAlphabet(String alphabet){
		this.alphabet = alphabet.toCharArray();
	}
	
	public void setAlphabet(char[] alphabet){
		this.alphabet = alphabet;
	}
	
	public void setFrameLength(int frameLength){
		this.frameLength = frameLength;
	}
	
	public DataSet setTestLabelNames(DataSet trainset){
		
		List<String> labelNames = new ArrayList<String>();
		for(int i = 0; i < trainset.numExamples(); i++){
			labelNames.add(trainset.getLabels().getInt(i)+"");
		}
		trainset.setLabelNames(labelNames);
		return trainset;
		
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
			
			INDArray input = Nd4j.zeros(InputFileLength, this.frameLength);
			//INDArray labels = Nd4j.zeros(InputFileLength, this.frameLength);
			INDArray labels = Nd4j.zeros(InputFileLength, 1);
			
			//Step over each example
			for(int lineCounter = 0; lineCounter < InputFileLength; lineCounter++){
				InputLineI = brI.readLine().toLowerCase();
				InputLineL = brL.readLine().toLowerCase();
				
				char cL = InputLineL.charAt(0);
				for(int alphabetCounter = 0; alphabetCounter < alphabet.length; alphabetCounter++){ 
					if(cL == alphabet[alphabetCounter]) 
						labels.putScalar(new int[] {lineCounter, 0}, Character.getNumericValue(cL));
				}
				
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
