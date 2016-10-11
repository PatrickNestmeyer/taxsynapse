package Booking;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import Application.Config;
import ReducedInvoice.AInvoice;

public class CSVBookingHandler {
	
	private static CSVBookingHandler uniqueInstance = null;
	
	private CSVBookingHandler(){};
	
	public static CSVBookingHandler getInstance(){
		if(uniqueInstance == null) {
			synchronized(CSVBookingHandler.class){
				if(uniqueInstance == null){
					uniqueInstance = new CSVBookingHandler();
				}
			}
		}
		return uniqueInstance;
	}
	
	public List<Voucher> getVoucherInfoFromFile(String Path, String volumeKey, String debitAccountKey, String taxkKey, String idKey, char Seperator) throws IOException{
		
		String[] Line;
		List<Voucher> returnValue = new ArrayList<Voucher>();
		int volumeID = -1;
		int debitAccountID = -1;
		int taxkID = -1;
		int idID = -1;
		
		CSVReader cReader = new CSVReader(new FileReader(Path), Seperator);
		while((Line = cReader.readNext()) != null){
			
			for(int i = 0; i < Line.length; i++){
				if(Line[i].equals(volumeKey))
					volumeID = i;
				if(Line[i].equals(debitAccountKey))
					debitAccountID = i;
				if(Line[i].equals(taxkKey))
					taxkID = i;
				if(Line[i].equals(idKey))
					idID = i;
			}
			if(volumeID > -1 && debitAccountID > -1 && taxkID > -1 && idID > -1)
				break;
		}
		while((Line = cReader.readNext()) != null){
			returnValue.add(new Voucher(Line[debitAccountID], Line[volumeID], Line[taxkID], Line[idID]));
		}
		return returnValue;
	}
	
	public void printVoucherListWithoutDebitAccount(List<AInvoice> aList, HashMap<String, String> fNames, char Seperator) throws IOException{
		CSVWriter writer = new CSVWriter(new FileWriter(Config.PATH_TO_VOUCHER_TEST_DUMP), Seperator);
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
            	outStream += fNames.get(this.getFileNamesKey(aList.get(i).getBuyerName(), aList.get(i).getInvoiceNumber()));
            	
            	entries = outStream.split(Character.toString(Seperator));
            	writer.writeNext(entries);
            }
		}
		
		writer.close();
	}
	
	public String getFileNamesKey(String Name, String Number){
		return Name + ":" + Number;
	}
}
