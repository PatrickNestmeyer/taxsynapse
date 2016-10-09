package Booking;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import Application.Config;

public class CSVBookingImport {
	
	private static CSVBookingImport uniqueInstance = null;
	
	private CSVBookingImport(){};
	
	public static CSVBookingImport getInstance(){
		if(uniqueInstance == null) {
			synchronized(CSVBookingImport.class){
				if(uniqueInstance == null){
					uniqueInstance = new CSVBookingImport();
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
}
