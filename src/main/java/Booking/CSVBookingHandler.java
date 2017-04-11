package Booking;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.beust.jcommander.internal.Lists;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import Application.Config;
import ReducedInvoice.AInvoice;

public class CSVBookingHandler {
	
	private static CSVBookingHandler uniqueInstance = null;
	private static List<AInvoice> FailedVoucherList = null;
	
	private CSVBookingHandler(){}
	
	/**
	 * 
	 * @return
	 */
	public static CSVBookingHandler getInstance()
	{
		if(uniqueInstance == null) {
			synchronized(CSVBookingHandler.class){
				if(uniqueInstance == null){
					uniqueInstance = new CSVBookingHandler();
					FailedVoucherList = new ArrayList<AInvoice>();
				}
			}
		}
		return uniqueInstance;
	}
	
	/**
	 * 
	 * @return
	 */
	public static List<AInvoice> getFailedVoucherList()
	{
		return FailedVoucherList;
	}
	
	/**
	 * 
	 * @param Path
	 * @param volumeKey
	 * @param debitAccountKey
	 * @param taxKey
	 * @param idKey
	 * @param Seperator
	 * @return
	 * @throws IOException
	 */
	public List<Voucher> getVoucherInfoFromFolder(String Path, String volumeKey, String debitAccountKey, String taxKey, String idKey, char Seperator) throws IOException{
		
		List<Voucher> returnValue = new ArrayList<Voucher>();
		
		//Rekursive => elem can be a folder or a file 
		Files.walk(Paths.get(Path)).forEach(elem ->
		{
			String fileName = elem.getFileName().toString();
			String path = elem.toString();
			try
			{
				if (Files.isDirectory(elem))
				{
					
				}
				else
				{	
					if(Files.isRegularFile(elem))
					{
						if(fileName.toLowerCase().contains(".csv"))
						{
							String PathToFile = Path + "/" + fileName;
							returnValue.addAll(this.getVoucherInfoFromFile(PathToFile, volumeKey, debitAccountKey, taxKey, idKey, Seperator));
							List<Voucher> x = returnValue;
						}
					}
				}
			}catch(Exception e){
				System.out.println(fileName + " caused an Exception.");
				System.out.println("Message: " + e.getMessage());
			}
		});
		
		return returnValue;
	}
	
	/**
	 * @param Path
	 * @param volumeKey
	 * @param debitAccountKey
	 * @param taxkKey
	 * @param idKey
	 * @param Seperator
	 * @return
	 * @throws IOException
	 */
	public List<Voucher> getVoucherInfoFromFile(String Path, String volumeKey, String debitAccountKey, String taxkKey, String idKey, char Seperator) throws IOException{
		
		String[] Line;
		List<Voucher> returnValue = new ArrayList<Voucher>();
		int volumeID = -1;
		int debitAccountID = -1;
		int taxID = -1;
		int idID = -1;
		
		CSVReader cReader = new CSVReader(new FileReader(Path), Seperator);
		while((Line = cReader.readNext()) != null){
			for(int i = 0; i < Line.length; i++){
				if(Line[i].equals(volumeKey))
					volumeID = i;
				if(Line[i].equals(debitAccountKey))
					debitAccountID = i;
				if(Line[i].equals(taxkKey))
					taxID = i;
				if(Line[i].equals(idKey))
					idID = i;
			}
			//Is every value in each line AND is every Value at Index 0+
			if(debitAccountID > volumeID && taxID > volumeID && idID > volumeID && volumeID > -1 && debitAccountID > -1 && taxID > -1 && idID > -1)
				break;
		}
		while((Line = cReader.readNext()) != null){
			returnValue.add(
					new Voucher( 
							this.transformDebitAccount(Line[debitAccountID]), 
							Line[volumeID], 
							this.transformTaxKey(Line[taxID]), 
							this.transformVoucherID(Line[idID])
									));
		}
		return returnValue;
	}
	
	/**
	 * 
	 * @param aList
	 * @param Seperator
	 * @throws IOException
	 */
	public void printVoucherListWithoutDebitAccount(List<AInvoice> aList, String Path, char Seperator) throws IOException
	{
		CSVWriter writer = new CSVWriter(new FileWriter(Path), Seperator);
		String outStream = "";
		outStream += Config.VOLUME_ID + Seperator
				+ Config.DEBIT_ACCOUNT_ID + Seperator
				+ Config.TAX_KEY_ID + Seperator
				+Config.BOOKING_NOTE + Seperator
				+Config.VOUCHER_ID;
		String[] entries = outStream.split(Character.toString(Seperator));
		writer.writeNext(entries);
		
		for(int i = 0; i < aList.size(); i++){
            for(int j = 0; j < aList.get(i).getPositionsLength(); j++){
            	
            	outStream = "";
            	outStream += Float.toString(aList.get(i).getPosition(j).getPositionPrice().getBrutto()).replace(".", ",") + Seperator;
            	outStream += Seperator;
            	outStream += Float.toString(aList.get(i).getPosition(j).getTaxrate()) + Seperator;
            	outStream += aList.get(i).getPosition(j).getDescription().replace(Seperator, Config.VOUCHER_CSV_REPLACEMENT) + Seperator;
            	outStream += aList.get(i).getVoucherID();
            	
            	entries = outStream.split(Character.toString(Seperator));
            	writer.writeNext(entries);
            }
		}
		
		writer.close();
	}
	
	/**
	 * Print LabelList
	 * 
	 * @param LabelList
	 * @param Seperator
	 * @throws IOException
	 */
	public void printLabeledSingleFile(List<Label> LabelList, String Path, char Seperator) throws IOException{
		CSVWriter writer = new CSVWriter(new FileWriter(Path), Seperator);
		String outStream = "";
		outStream += Config.VOUCHER_ID + Seperator
				+Config.TAX_KEY_ID + Seperator
				+Config.BOOKING_NOTE;
				
		String[] entries = outStream.split(Character.toString(Seperator));
		writer.writeNext(entries);
		
		for(int i = 0; i < LabelList.size(); i++)
		{
            for(int j = 0; j < LabelList.get(i).getDescriptionSize(); j++)
            {
            	outStream = "";
            	outStream += LabelList.get(i).getBeleglink() + Seperator;
            	outStream += LabelList.get(i).getlabel_ID() + Seperator;
            	outStream += LabelList.get(i).getDescription(j) + Seperator;
            	
            	entries = outStream.split(Character.toString(Seperator));
            	writer.writeNext(entries);
            }
		}
		
		writer.close();
	}
	
	/**
	 * 
	 * @param error_list
	 * @param path
	 */
	public void printErrorVoucherList(List<AInvoice> error_list,List<Voucher> VoucherList, String path)
	{
		File errorFolderPath = new File(path + "/voucher_error");
		
		if(!errorFolderPath.exists())
		{
			try
			{
				errorFolderPath.mkdir();
			}
			catch(Exception e)
			{
				System.out.println("Could not create error folder");
			}
		}
		writeVoucherErrorList(error_list, VoucherList, errorFolderPath.getPath());
	}
	
	/**
	 * 
	 * @param error_list
	 * @param path
	 */
	public void writeVoucherErrorList(List<AInvoice> error_list,List<Voucher> VoucherList, String path)
	{
		File dataOut = new File(path + "/voucher_error.txt");
		try
		{
		dataOut.createNewFile();
		
		FileWriter dataWriter = new FileWriter(dataOut);
		int temp = 0;
		
		for(int i = 0; i < error_list.size(); i++)
		{
			if(error_list.get(i).checkVoucherIDError())
			{
				dataWriter.write(error_list.get(i).getVoucherID() +" - VOUCHER ID NOT FOUND\n");
			}
			else if(error_list.get(i).checkReconstructVoucherError())
			{
				dataWriter.write(error_list.get(i).getVoucherID() + " - COULD NOT RECONSTRUCT VOUCHER\n");
			}
			temp = searchVoucherListItem(VoucherList, error_list.get(i).getVoucherID());
			
			for(int j = 0; j < error_list.get(i).getPositionsLength(); j++)
			{
				if(error_list.get(i).checkReconstructVoucherError())
				{
					dataWriter.write(error_list.get(i).getPosition(j).getDescription() + "\n"
									+"Brutto: " +(error_list.get(i).getPosition(j).getPositionPrice().getNetto()/100) * (100+error_list.get(i).getPosition(j).getPositionPrice().getSteuer()) + " "
									+"Steuer: " +error_list.get(i).getPosition(j).getPositionPrice().getSteuer() + " "
									+"Netto: "+error_list.get(i).getPosition(j).getPositionPrice().getNetto() +  "\n");
				}
				else if(error_list.get(i).checkVoucherIDError())
				{
					dataWriter.write(error_list.get(i).getPosition(j).getDescription() + "\n");
				}
			}
			if(error_list.get(i).checkReconstructVoucherError())
			{
				dataWriter.write("Buchungsbetrag: " + VoucherList.get(temp).getVolume() + "\n\n");
			}
			else
			{
				dataWriter.write("\n");
			}
		}
		
		dataWriter.close();
		}
		catch(Exception e)
		{
			
		}
	}
	
	/**
	 * 
	 * @param LabelList
	 * @param Path
	 * @param shuffle
	 * @throws IOException
	 */
	public void printLabeledOutputStructure(List<Label> LabelList, String Path, boolean shuffle) throws IOException{
		File trainFolderPath = new File(Path + "/train");
		File testFolderPath = new File(Path + "/test");
		
		if(!trainFolderPath.exists()){
			try{
				trainFolderPath.mkdir();
			}catch(Exception e){
				System.out.println("Could not create train folder");
			}
		}
		if(!testFolderPath.exists()){
			try{
				testFolderPath.mkdir();
			}catch(Exception e){
				System.out.println("Could not create test folder");
			}
		}
		
		if(shuffle)
			Collections.shuffle(LabelList);
		
		int splitPoint = (LabelList.size() - LabelList.size() / 5);
		List<Label> trainList = LabelList.subList(0, splitPoint);
		List<Label> testList = LabelList.subList(splitPoint, LabelList.size());
		
		this.writeLabeledDataToFileStructure(trainList, trainFolderPath.getPath());
		this.writeLabeledDataToFileStructure(testList, testFolderPath.getPath());
	}
	
	/**
	 * 
	 * @param labelList
	 * @param path
	 * @throws IOException
	 */
	private void writeLabeledDataToFileStructure(List<Label> labelList, String path) throws IOException
	{
		File dataOut = new File(path + "/data.txt");
		File labelsOut = new File(path + "/labels.txt");
		dataOut.createNewFile();
		labelsOut.createNewFile();
		
		FileWriter dataWriter = new FileWriter(dataOut);
		FileWriter labelsWriter = new FileWriter(labelsOut);
		
		String lineBreak = System.getProperty("line.separator");
		
		if(Config.COPY_TRAIN_DATA)
		{
			for(int copy_label_list = 0; copy_label_list < Config.COPY_TRAIN_DATA_SIZE;copy_label_list++)
			{
				for(int i = 0; i < labelList.size(); i++)
				{
					
					for(int j = 0; j < labelList.get(i).getDescriptionSize(); j++)
					{
						
						dataWriter.write(labelList.get(i).getDescription(j) + "\n");
						labelsWriter.write(labelList.get(i).getlabel_ID() + "\n");
						
						//If not the last line to print
						/**if(!((j == labelList.get(i).getDescriptionSize()-1) && i == (labelList.size()-1)))
						{
							dataWriter.write(lineBreak);
							labelsWriter.write(lineBreak);
						}*/
					}
				}
			}
		}
		else
		{
			for(int i = 0; i < labelList.size(); i++)
			{
				
				for(int j = 0; j < labelList.get(i).getDescriptionSize(); j++)
				{
					
					dataWriter.write(labelList.get(i).getDescription(j) + "\n");
					labelsWriter.write(labelList.get(i).getlabel_ID() + "\n");
					
					//If not the last line to print
					/**if(!((j == labelList.get(i).getDescriptionSize()-1) && i == (labelList.size()-1)))
					{
						dataWriter.write(lineBreak);
						labelsWriter.write(lineBreak);
					}*/
				}
			}
		}

		
		dataWriter.close();
		labelsWriter.close();
	}
	
	/**
	 * 
	 * @param ReducedInvoiceList
	 * @param VoucherList
	 * @param LabelError 
	 * @return
	 */
	public List<Label> createReducedInvoiceVoucherList(List<AInvoice> ReducedInvoiceList, List<Voucher> VoucherList)
	{
		Label temp;
		int ReducedInvoiceListItem = 0;
		List<Label> LabelList = new ArrayList<Label>();
		
		for (int i = 0; i < VoucherList.size();i++)
		{
			ReducedInvoiceListItem = searchReducedInvoiceListItem(ReducedInvoiceList, VoucherList.get(i).getVoucherID());
			//System.out.println("Buchung " + i); // Laufzeit
			if(ReducedInvoiceListItem >= 0)
			{
				temp = fast_rando_recounstruct_voucher(ReducedInvoiceList.get(ReducedInvoiceListItem),VoucherList.get(i),100);
				if(temp.getBeleglink() == null && Config.LOG_INVOICE_AND_VOUCHER_ERROR == true)
				{
					ReducedInvoiceList.get(ReducedInvoiceListItem).setInvoiceError_RECONSTRUCT_ERROR();
					FailedVoucherList.add(ReducedInvoiceList.get(ReducedInvoiceListItem));
				}
				else
				{
					LabelList.add(temp);
				}				
			}
		}
				
		if(Config.LOG_INVOICE_AND_VOUCHER_ERROR)
		{
			boolean voucher_id_not_found = true;
			
			for(int i = 0; i < ReducedInvoiceList.size();i++)
			{
				for(int j = 0; j < VoucherList.size(); j++)
				{
					if(ReducedInvoiceList.get(i).getVoucherID().equals(VoucherList.get(j).getVoucherID()))
					{
						voucher_id_not_found = false;
						break;
					}
				}
				
				if(voucher_id_not_found)
				{
					ReducedInvoiceList.get(i).setInvoiceError_VOUCHER_ID_ERROR();
					FailedVoucherList.add(ReducedInvoiceList.get(i));
				}
				voucher_id_not_found = true;
			}
			
			printErrorVoucherList(FailedVoucherList, VoucherList, Config.PATH_TO_FAILED_VOUCHER);
			System.out.println(FailedVoucherList.size() + " Invoices could not be used for network data");
		}
		
		return LabelList;
	}	
	
	/**
	 * 
	 * @param invoice 
	 * @param voucher the accounting record
	 * @param c boost the probability to find the valid accounting record
	 * @return
	 */
	public Label fast_rando_recounstruct_voucher(AInvoice invoice, Voucher voucher, int c)
	{
		List<Integer> steuerklasse = new ArrayList<Integer>();
		List<Integer> temp = new ArrayList<Integer>();
		float BuchungsUmsatz = rounding(Float.parseFloat(voucher.getVolume().replace(",", ".")));
		Label returnValue = new Label();
		int rand = 0;
		//int count = 0;
		
		for(int i = 0; i < invoice.getPositionsLength(); i++)
		{
			if(Float.parseFloat(voucher.getTaxKey()) == ((float) invoice.getPosition(i).getTaxrate()))
			{
				steuerklasse.add(i);
				temp.add(i);
			}
		}
		
		int wiederholungen = 2 * steuerklasse.size() * steuerklasse.size() * c;
		for(int i = 0; i < wiederholungen; i++)
		{
			if(temp.size() <= 0)
			{
				returnValue = new Label();
				BuchungsUmsatz = rounding(Float.parseFloat(voucher.getVolume().replace(",", ".")));
				copyArrayList(steuerklasse, temp);
				
				rand = randInt(0, temp.size()-1);
				BuchungsUmsatz = rounding(rounding(BuchungsUmsatz) - rounding(invoice.getPosition(temp.get(rand)).getPositionPrice().getBrutto()));
				
				returnValue.addPosition(temp.get(rand));
				returnValue.addDescription(invoice.getPosition(temp.get(rand)).getDescription());
				temp.remove(rand);
			}
			else
			{
				rand = randInt(0, temp.size()-1);
				BuchungsUmsatz = rounding(rounding(BuchungsUmsatz) - rounding(invoice.getPosition(temp.get(rand)).getPositionPrice().getBrutto()));
				
				returnValue.addPosition(temp.get(rand));
				returnValue.addDescription(invoice.getPosition(temp.get(rand)).getDescription());
				temp.remove(rand);
			}
			//count++;
			
			if(BuchungsUmsatz < 0.00)
			{
				returnValue = new Label();
				BuchungsUmsatz = rounding(Float.parseFloat(voucher.getVolume().replace(",", ".")));
				copyArrayList(steuerklasse, temp);
			}
			if(BuchungsUmsatz == 0.00)
			{
				returnValue.setlabel_ID(voucher.getDebitAccount());
				returnValue.setBeleglink(voucher.getVoucherID());
				break;
			}
		}
		//System.out.println("New Rando Algo Iteration: "+ count + "\n"); // Iteration
		return returnValue;
	}
	
	/**
	 * 
	 * @param source the source
	 * @param clone the copy of the source
	 */
	public int copyArrayList(List<Integer> source, List <Integer> clone)
	{
		clone.clear();
		int i = 0;
		for(; i < source.size(); i++)
		{
			clone.add(source.get(i));
		}
		return i;
	}
	
	/**
	 * 
	 * @param voucherList
	 * @param VoucherID
	 * @return
	 */
	public int searchVoucherListItem(List<Voucher> voucherList, String VoucherID)
	{
		for(int i = 0; i < voucherList.size(); i++)
		{
			if(VoucherID.equals(voucherList.get(i).getVoucherID()))
			{
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * 
	 */
	public int searchReducedInvoiceListItem(List<AInvoice> ReducedInvoiceList, String VoucherID)
	{
		for(int i = 0; i < ReducedInvoiceList.size(); i++)
		{
			if(ReducedInvoiceList.get(i).getVoucherID().equals(VoucherID))
			{
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	private int randInt(int min, int max) 
	{
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	/**
	 * @param account
	 */
	private String transformDebitAccount(String account){
		return account.substring(0, 1);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	private String transformTaxKey(String key){
		return key;
	}
	
	/**
	 * 
	 * @param vID
	 * @return
	 */
	private String transformVoucherID(String vID){
		//TODO: Remove unnecessary characters from ID
		return vID;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public float rounding(float value)
	{
		if(((value*1000) % 10) > 4 )
		{
			value = (float)((int)(value*100+1))/100; 
		}
		else
		{
			value = (float)((int)(value*100))/100; 
		}
		return value;
	}
	
	/**
	 * veraltet
	 * 
	 * @param invoice
	 * @param voucher
	 * @return
	 */
	public Label reconstruct_voucher(AInvoice invoice, Voucher voucher)
	{
		Label returnValue = null;
		float BuchungsUmsatz;
		boolean Ruckrechnung = false;
		for(int i = 0; i < invoice.getPositionsLength(); i++)
		{
			returnValue = new Label();
			BuchungsUmsatz = rounding(Float.parseFloat(voucher.getVolume().replace(",", "."))); 
			for(int j = i; j < invoice.getPositionsLength(); j++ )
			{
				if(Float.parseFloat(voucher.getTaxKey()) == ((float) invoice.getPosition(j).getTaxrate()))
				{
					BuchungsUmsatz = rounding(BuchungsUmsatz) - rounding(invoice.getPosition(j).getPositionPrice().getBrutto());
					returnValue.addPosition(j);
					returnValue.addDescription(invoice.getPosition(j).getDescription());
					if(BuchungsUmsatz == 0.00)
					{
						returnValue.setlabel_ID(voucher.getDebitAccount());
						returnValue.setBeleglink(voucher.getVoucherID());
						Ruckrechnung = true;
						break;
					}
				}
			}
			if(Ruckrechnung == true)
			{
				break;
			}
		}	
		return returnValue;
	}
	
	/**
	 * veraltet
	 * 
	 * @param invoice
	 * @param voucher
	 * @param c
	 * @return
	 */
	public Label rando_reconstruct_voucher(AInvoice invoice, Voucher voucher, int c)
	{
		List<Integer> steuerklasse = new ArrayList<Integer>();
		float BuchungsUmsatz = rounding(Float.parseFloat(voucher.getVolume().replace(",", ".")));
		Label returnValue = new Label();
		int rand = 0;
		int count = 0;
		
		for(int i = 0; i < invoice.getPositionsLength(); i++)
		{
			if(Float.parseFloat(voucher.getTaxKey()) == ((float) invoice.getPosition(i).getTaxrate()))
			{
				steuerklasse.add(i);
			}
		}
		
		int wiederholungen = 2 * steuerklasse.size() * steuerklasse.size() * c;
		for(int i = 0; i < wiederholungen; i++)
		{
			rand = randInt(0, steuerklasse.size()-1);
			BuchungsUmsatz = rounding(rounding(BuchungsUmsatz) - rounding(invoice.getPosition(steuerklasse.get(rand)).getPositionPrice().getBrutto()));
			
			returnValue.addPosition(steuerklasse.get(rand));
			returnValue.addDescription(invoice.getPosition(steuerklasse.get(rand)).getDescription());
			count++;
			if(BuchungsUmsatz < 0.00)
			{
				returnValue = new Label();
				BuchungsUmsatz = rounding(Float.parseFloat(voucher.getVolume().replace(",", ".")));
			}
			if(BuchungsUmsatz == 0.00)
			{
				returnValue.setlabel_ID(voucher.getDebitAccount());
				returnValue.setBeleglink(voucher.getVoucherID());
				break;
			}
		}
		System.out.println("Old Rando Algo Iteration: "+ count); // Iteration
		return returnValue;
	}
}
