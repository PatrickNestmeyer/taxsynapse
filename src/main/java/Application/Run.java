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
			CSVBookingHandler bHandler = CSVBookingHandler.getInstance();
			List<Voucher> VoucherList = new ArrayList<Voucher>();
			VoucherList = bHandler.getVoucherInfoFromFile(Config.PATH_TO_VOUCHERS, Config.VOLUME_ID, Config.DEBIT_ACCOUNT_ID, Config.TAX_KEY_ID, Config.VOUCHER_ID, Config.VOUCHER_CSV_SEPERATOR);
			bHandler.printVoucherListWithoutDebitAccount(ReducedInvoiceList, Filenames, Config.VOUCHER_CSV_SEPERATOR);
		}catch(FileNotFoundException e){
			System.out.println(e.getMessage());
		}
		
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
