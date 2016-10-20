package Booking;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	
	public void printVoucherListWithoutDebitAccount(List<AInvoice> aList, char Seperator) throws IOException
	{
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
            	outStream += aList.get(i).getBeleglink();
            	
            	entries = outStream.split(Character.toString(Seperator));
            	writer.writeNext(entries);
            }
		}
		
		writer.close();
	}
	
	public static List<Label> createReducedInvoiceVoucherList(List<AInvoice> ReducedInvoiceList, List<Voucher> VoucherList)
	{
		List<Label> LabelList = new ArrayList<Label>();
		Label temp;
		
		int ReducedInvoiceListItem = 0;
		for (int i = 0; i < VoucherList.size();i++)
		{
			ReducedInvoiceListItem = searchReducedInvoiceListItem(ReducedInvoiceList,VoucherList.get(i).getVoucherID());
			temp = rando_reconstruct_voucher(ReducedInvoiceList.get(ReducedInvoiceListItem),VoucherList.get(i),10000);
			if(temp.Beleglink != null)
			{
				LabelList.add(temp);
			}
			else
			{
				LabelList.add(reconstruct_voucher(ReducedInvoiceList.get(ReducedInvoiceListItem),VoucherList.get(i)));
			}
		}
		return LabelList;
	}
	
	
	/**
	 * veraltet
	 * 
	 * @param LabelList
	 * @param RandoLabelList
	 * @return
	 */
	public static List<Label> compareAlgo(List<Label> LabelList, List<Label> RandoLabelList)
	{
		int countLabelList = 0;
		int countRandoLabelList = 0;
		for(int i = 0; i < RandoLabelList.size(); i++)
		{
			if(LabelList.get(i).label_ID != null)
			{
				countLabelList++;
			}
			if(RandoLabelList.get(i).label_ID != null)
			{
				countRandoLabelList++;
			}
		}
		if(countLabelList > countRandoLabelList)
		{
			return LabelList;
		}
		else
		{
			return RandoLabelList;
		}
	}
	
	public static Label reconstruct_voucher(AInvoice invoice, Voucher voucher)
	{
		Label returnValue = null;
		float BuchungsUmsatz;
		boolean Rückrechnung = false;
		for(int i = 0; i < invoice.getPositionsLength(); i++)
		{
			returnValue = new Label();
			BuchungsUmsatz = Runden(Float.parseFloat(voucher.getVolume().replace(",", "."))); 
			for(int j = i; j < invoice.getPositionsLength(); j++ )
			{
				if(Float.parseFloat(voucher.getTaxKey()) == ((float) invoice.getPosition(j).getTaxrate()))
				{
					BuchungsUmsatz = Runden(BuchungsUmsatz) - Runden(invoice.getPosition(j).getPositionPrice().getBrutto());
					returnValue.position_number.add(j);
					returnValue.description.add(invoice.getPosition(j).getDescription());
					if(BuchungsUmsatz == 0.00)
					{
						returnValue.label_ID = voucher.getDebitAccount();
						returnValue.Beleglink = voucher.getVoucherID();
						Rückrechnung = true;
						break;
					}
				}
			}
			if(Rückrechnung == true)
			{
				break;
			}
		}	
		return returnValue;
	}
	
	public static Label rando_reconstruct_voucher(AInvoice invoice, Voucher voucher, int c)
	{
		List<Integer> steuerklasse = new ArrayList<Integer>();
		float BuchungsUmsatz = Runden(Float.parseFloat(voucher.getVolume().replace(",", ".")));
		Label returnValue = new Label();
		int rand = 0;
		
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
			BuchungsUmsatz = Runden(Runden(BuchungsUmsatz) - Runden(invoice.getPosition(steuerklasse.get(rand)).getPositionPrice().getBrutto()));
			returnValue.position_number.add(steuerklasse.get(rand));
			returnValue.description.add(invoice.getPosition(steuerklasse.get(rand)).getDescription());
			if(BuchungsUmsatz < 0.00)
			{
				returnValue = new Label();
				BuchungsUmsatz = Runden(Float.parseFloat(voucher.getVolume().replace(",", ".")));
			}
			if(BuchungsUmsatz == 0.00)
			{
				returnValue.label_ID = voucher.getDebitAccount();
				returnValue.Beleglink = voucher.getVoucherID();
				break;
			}
		}
		
		return returnValue;
	}
	
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
	
	private static int randInt(int min, int max) 
	{
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	public static float Runden(float a)
	{
		if(((a*1000) % 10) > 4 )
		{
			a = (float)((int)(a*100+1))/100; 
		}
		else
		{
			a = (float)((int)(a*100))/100; 
		}
		return a;
	}
	
	/**
	 * veraltet
	 * 
	 * @param Name
	 * @param Number
	 * @return
	 *
	 *	public String getFileNamesKey(String Name, String Number)
	 *	{
	 *		return Name + ":" + Number;
	 *	}
	 */
}
