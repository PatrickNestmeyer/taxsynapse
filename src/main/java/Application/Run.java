package Application;

import Import.*;
import ReducedInvoice.*;
import io.konik.zugferd.Invoice;
import Booking.Voucher;
import Booking.CSVBookingHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

/*
 *
 * TODO: 
 *
 * */

public class Run {
	
	/**
	 * The main entry point of the application
	 * Here are two paths possible
	 * a) the preperation of the data. 
	 * b) the configuration and run of the convolutinal neural netwrok
	 * 
	 */
	
	public static void main(String[] args) throws IOException {
		
		switch(args[0].toLowerCase()){
		case "data":
			runDataPreprocessing();
			break;
		case "network":
			runNetwork();
			break;
		default:
			System.out.println("The command " + args[0] + " was not found.");
			System.out.println("Please check for typo.");
			System.out.println("Legal parameters are data and network.");
			break;
		}
		
		System.out.println("FIN");
		
	}
	
	/**
	 * The core call functions of the data preprocessing
	 */
	
	public static void runDataPreprocessing(){
		List<Invoice> InvoiceList;
		List<AInvoice> ReducedInvoiceList = new ArrayList<AInvoice>();
		HashMap<String, String> Filenames = new HashMap<String, String>();
		
		InvoiceList = new ArrayList<Invoice>();
		readInvoicesFromFiles(InvoiceList, Filenames);
		reduceInvoices(InvoiceList, ReducedInvoiceList);
		
		try{
			CSVBookingHandler bHandler = CSVBookingHandler.getInstance();
			List<Voucher> VoucherList = new ArrayList<Voucher>();
			VoucherList = bHandler.getVoucherInfoFromFile(Config.PATH_TO_VOUCHERS, Config.VOLUME_ID, Config.DEBIT_ACCOUNT_ID, Config.TAX_KEY_ID, Config.VOUCHER_ID, Config.VOUCHER_CSV_SEPERATOR);
			CSVBookingHandler.getInstance().createReducedInvoiceVoucherList(ReducedInvoiceList,VoucherList);
			bHandler.printVoucherListWithoutDebitAccount(ReducedInvoiceList, Filenames, Config.VOUCHER_CSV_SEPERATOR);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * The core call functions of the neural network
	 */
	
	public static void runNetwork(){
		
		int numLinesToSkip = 1;
		int minibatchSize = 128;
		int numPossibleLables = 4;
		int labelIndex = 1;
		boolean regression = false;
		
		SequenceRecordReader reader = new CSVSequenceRecordReader(numLinesToSkip, Character.toString(Config.VOUCHER_CSV_SEPERATOR));
		try {
			reader.initialize(new NumberedFileInputSplit(Config.PATH_TO_LABELED_DATA, 0 , 0));
		} catch (IOException | InterruptedException e) {
			e.getMessage();
		}
		
		
		
		//DataSetIterator iterClassification = new SequenceRecordReaderDataSetIterator(reader, minibatchSize, numPossibleLables, labelIndex, regression);
		
		regression = false;
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
