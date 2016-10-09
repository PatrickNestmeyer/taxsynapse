package Application;

import Import.*;
import ReducedInvoice.*;
import io.konik.zugferd.Invoice;
import BagOfWords.WordBag;
import Booking.Voucher;
import Booking.CSVBookingImport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;

/*
 *
 * TODO: 
 *
 * */

public class Run {
	
	public static void main(String[] args) throws IOException {
		
		Boolean readOriginalInvoices = true;
		
		
		List<Invoice> InvoiceList;
		List<AInvoice> ReducedInvoiceList = new ArrayList<AInvoice>();
		HashMap<String, String> Filenames = new HashMap<String, String>();
		
		if(readOriginalInvoices){
			InvoiceList = new ArrayList<Invoice>();
			readInvoicesFromFiles(InvoiceList, Filenames);
			reduceInvoices(InvoiceList, ReducedInvoiceList);
		}else{
			
		}
		
		try{
			CSVBookingImport bImport = CSVBookingImport.getInstance();
			List<Voucher> VoucherList = new ArrayList<Voucher>();
			VoucherList = bImport.getVoucherInfoFromFile(Config.PATH_TO_VOUCHERS, Config.VOLUME_ID, Config.DEBIT_ACCOUNT_ID, Config.TAX_KEY_ID, Config.VOUCHER_ID, Config.VOUCHER_CSV_SEPERATOR);
		}catch(FileNotFoundException e){
			System.out.println(e.getMessage());
		}
		PrintWriter pw = new PrintWriter(new File("test.csv"));
        StringBuilder sb = new StringBuilder();
        sb.append(Config.VOLUME_ID);
        sb.append(Config.VOUCHER_CSV_SEPERATOR);
        sb.append(Config.DEBIT_ACCOUNT_ID);
        sb.append(Config.VOUCHER_CSV_SEPERATOR);
        sb.append(Config.TAX_KEY_ID);
        sb.append(Config.VOUCHER_CSV_SEPERATOR);
        sb.append("Buchungstext");
        sb.append(Config.VOUCHER_CSV_SEPERATOR);
        sb.append(Config.VOUCHER_ID);
        for(int i = 0; i < ReducedInvoiceList.size(); i++){
            for(int j = 0; j < ReducedInvoiceList.get(i).getPositionsLength(); j++){
            	sb.append("\n");
            	sb.append(ReducedInvoiceList.get(i).getPosition(j).getPositionPrice().getBrutto());
            	sb.append(Config.VOUCHER_CSV_SEPERATOR);
            	sb.append("");
            	sb.append(Config.VOUCHER_CSV_SEPERATOR);
            	sb.append(ReducedInvoiceList.get(i).getPosition(j).getTaxrate());
            	sb.append(Config.VOUCHER_CSV_SEPERATOR);
            	sb.append(Filenames.get(ReducedInvoiceList.get(i).getBuyerName() + ":" + ReducedInvoiceList.get(i).getInvoiceNumber()));
            	sb.append(Config.VOUCHER_CSV_SEPERATOR);
            	sb.append("\"" + ReducedInvoiceList.get(i).getPosition(j).getDescription().replace(";", ",") + "\"");
            }
        }
        pw.write(sb.toString());
        pw.close();
		
		//int numLinesToSkip = 0;
		
		//RecordReader featuresReader = new CSVRecordReader()
		
		//Create Bag of Words
		/*
		WordBag wb = new WordBag();
		try{
			wb.createWordBag(ReducedInvoiceList, pathToInvalidWords, pathToInvalidTokens);
			//List<String> w = wb.getBag();
			//System.out.println(w);
			System.out.println("Bag of words sucessfully created");
		}catch(Exception e){
			System.out.print("Exception while creating bag of words");
			System.out.println("Details: ");
			System.out.println(e.getMessage());
		}
		*/
		
		System.out.println("FIN");
		
	}

	private static void readInvoicesFromFiles(List<Invoice> InvoiceList, HashMap<String, String> Filenames){
		String pathToInvoices = Config.PATH_TO_INVOICES;
		Boolean logSingleSteps = Config.LOG_SINGLE_STEPS;
		Boolean supressInvalid = Config.SUPRESS_INVALID_INVOICES;
		zugferdHandler zHandler = zugferdHandler.getInstance();
		try{
			zHandler.readInvoice(pathToInvoices, Filenames, InvoiceList, logSingleSteps, supressInvalid);
			System.out.println("All Invoices successfully readed");
		}catch(Exception e){
			System.out.print("Exception while reading Invoices");
			System.out.println("Details: ");
			System.out.println(e.getMessage());
		}
	}
	
	private static void reduceInvoices(List<Invoice> InvoiceList, List<AInvoice> ReducedInvoiceList){
		InvoiceReducer ir = InvoiceReducer.getInstance();
		try{
			ir.ReducedInvoiceList(InvoiceList, ReducedInvoiceList);
			System.out.println("Successfully reduced Invoices");
			System.out.println("MetaExceptionCounter: " + ir.getMetaExceptionCounter());
			System.out.println("PositionExceptionCounter: " + ir.getPositionExceptionCounter());
			System.out.println("Invoices sucessfully reduced");
		}catch(Exception e){
			System.out.print("Exception while reducing Invoices");
			System.out.println("Details: ");
			System.out.println(e.getMessage());
		}
	}
	
}
