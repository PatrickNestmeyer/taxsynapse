package NeuralNetwork;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExportManager {

static private ExportManager uniqueInstance = null;
	
	/**
	 * Singleton Properties
	 */

	public static ExportManager getInstance(){
		if(uniqueInstance == null) {
			synchronized(ExportManager.class){
				if(uniqueInstance == null){
					uniqueInstance = new ExportManager();
				}
			}
		}
		return uniqueInstance;
	}
	private ExportManager(){}
	
	public String createNetworkSettingFolder(String outputPath){
		Date date = new Date();
		String timeStamp = new SimpleDateFormat("dd_MM_yyy_HH_mm_ss").format(date);
		boolean createFolderSucess = (new File(outputPath + "/" + timeStamp)).mkdirs();
		if(createFolderSucess){
			return outputPath + "/" + timeStamp;
		}else{
			String failure = "Folder with timestamp could not be generated";
			System.out.println(failure);
			return failure;
		}
	}
	
	public void saveNetworkInitialSettings(String timestampPath, MultiLayerNetwork net, List<String> metadata){
		try{
			this.saveList(new File(timestampPath + "/metadata.txt"), metadata);
			this.saveNetwork(new File(timestampPath+"/initial_model.zip"), net);
		}catch(Exception e){
			System.out.println("Exception while saving initial settings.");
			System.out.println("Details:");
			e.printStackTrace();
		}
	}
	
	public void saveNetworkEpochSetting(String timestampPath, MultiLayerNetwork net, int epoch){
		try{
			this.saveNetwork(new File(timestampPath+"/epoch"+epoch+".zip"), net);
		}catch(Exception e){
			System.out.print("Exception while saving initial settings.");
			System.out.println("Details:");
			e.printStackTrace();
		}
	}
	
	public void saveEvaluation(String timestampPath, List<String> epochsWithScores){
		try{
			this.saveList(new File(timestampPath + "/evaluation.txt"), epochsWithScores);
		}catch(Exception e){
			System.out.println("Exception while saving evaluation.");
			System.out.println("Details:");
			e.printStackTrace();
		}	
	}
		
	private void saveList(File FileToSave, List<String> lines) throws IOException{
		PrintWriter writer = new PrintWriter(FileToSave);
		for (String elem : lines) {
			writer.println(elem);
		}
		writer.close();
	}
	
	private void saveNetwork(File FileToSave, MultiLayerNetwork net) throws IOException {
		ModelSerializer.writeModel(net, FileToSave, false);
	}
	
	public MultiLayerNetwork loadNetworInitialSettings(String timestampPath){
		
		try {
			return ModelSerializer.restoreMultiLayerNetwork(timestampPath+"/initial_model.zip");
		}catch (FileNotFoundException fe){
			System.out.println("The initial file for the given timestamp could not get found.");
			return null;
		} 
		catch (IOException e) {
			System.out.println("Exception while loading initial network settings");
			System.out.println("Details:");
			e.printStackTrace();
			return null;
		}
	}
	
	public MultiLayerNetwork loadNetworkSettingsAfterEpoch(String timestampPath, int epoch){
		try {
			return ModelSerializer.restoreMultiLayerNetwork(timestampPath+"/epoch"+epoch+".zip");
		}catch (FileNotFoundException fe){
			System.out.println("The file to the given timestamp at epoch " + epoch + "could not get found.");
			return null;
		} 
		catch (IOException e) {
			System.out.println("Exception while loading network settings at epoch " + epoch);
			System.out.println("Details:");
			e.printStackTrace();
			return null;
		}
	}
}
