package Booking;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import Application.Config;
import ReducedInvoice.AInvoice;

public class CSVBookingHandler {
	
	private static CSVBookingHandler uniqueInstance = null;
	
	private CSVBookingHandler(){};
	
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
				}
			}
		}
		return uniqueInstance;
	}
	
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
					System.out.println("test");
				}
				else
				{	
					if(Files.isRegularFile(elem))
					{
						if(fileName.toLowerCase().contains(".csv"))
						{
							String PathToFile = Path + "/" + fileName;
							returnValue.addAll(this.getVoucherInfoFromFile(PathToFile, volumeKey, debitAccountKey, taxKey, idKey, Seperator));
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
            	outStream += aList.get(i).getBeleglink();
            	
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
	public void printLabelList(List<Label> LabelList, String Path, char Seperator) throws IOException
	{
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
	 * @param ReducedInvoiceList
	 * @param VoucherList
	 * @param LabelError 
	 * @return
	 */
	public static List<Label> createReducedInvoiceVoucherList(List<AInvoice> ReducedInvoiceList, List<Voucher> VoucherList, boolean RemoveLabelError)
	{
		List<Label> LabelList = new ArrayList<Label>();
		Label temp;
		
		int ReducedInvoiceListItem = 0;
		for (int i = 0; i < VoucherList.size();i++)
		{
			ReducedInvoiceListItem = searchReducedInvoiceListItem(ReducedInvoiceList,VoucherList.get(i).getVoucherID());
			System.out.println("Buchung " + i); // Laufzeit
			temp = fast_rando_recounstruct_voucher(ReducedInvoiceList.get(ReducedInvoiceListItem),VoucherList.get(i),100);
			if(temp.getBeleglink() != null)
			{
				LabelList.add(temp);
			}
			else
			{
				LabelList.add(reconstruct_voucher(ReducedInvoiceList.get(ReducedInvoiceListItem),VoucherList.get(i)));
			}
		}
		if(RemoveLabelError == true)
		{
			RemoveLabel(LabelList);
		}
		
		return LabelList;
	}
	
	/**
	 * Remove the Labels which could not be reconstructed 
	 * 
	 * @param LabelList
	 */
	public static void RemoveLabel(List<Label> LabelList)
	{
		for(int i = 0; i < LabelList.size(); i++)
		{
			if(LabelList.get(i).getBeleglink() == null)
			{
				LabelList.remove(i);
			}
		}
	}
	
	/**
	 * 
	 * @param invoice
	 * @param voucher
	 * @return
	 */
	public static Label reconstruct_voucher(AInvoice invoice, Voucher voucher)
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
	public static Label rando_reconstruct_voucher(AInvoice invoice, Voucher voucher, int c)
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
	
	/**
	 * 
	 * @param invoice 
	 * @param voucher the accounting record
	 * @param c boost the probability to find the valid accounting record
	 * @return
	 */
	public static Label fast_rando_recounstruct_voucher(AInvoice invoice, Voucher voucher, int c)
	{
		List<Integer> steuerklasse = new ArrayList<Integer>();
		List<Integer> temp = new ArrayList<Integer>();
		float BuchungsUmsatz = rounding(Float.parseFloat(voucher.getVolume().replace(",", ".")));
		Label returnValue = new Label();
		int rand = 0;
		int count = 0;
		
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
			count++;
			
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
		System.out.println("New Rando Algo Iteration: "+ count + "\n"); // Iteration
		return returnValue;
	}
	
	/**
	 * 
	 * @param source the source
	 * @param clone the copy of the source
	 */
	public static int copyArrayList(List<Integer> source, List <Integer> clone)
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
	 * @param ReducedInvoiceList
	 * @param VoucherID
	 * @return
	 */
	public static int searchReducedInvoiceListItem(List<AInvoice> ReducedInvoiceList, String VoucherID)
	{
		for(int i = 0; i < ReducedInvoiceList.size(); i++)
		{
			if(VoucherID.equals(ReducedInvoiceList.get(i).getBeleglink()))
			{
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	private static int randInt(int min, int max) 
	{
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static float rounding(float value)
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
}
