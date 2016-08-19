package Application;

import Import.*;
import ReducedInvoice.*;
import io.konik.zugferd.Invoice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Run {

	/**
	 * 
	 * Override Validation Counter
	 * 
	 * Serialization
	 * 
	 */
	
	public static void main(String[] args) throws IOException {
		
		String pathToFolder = args[0];
		Boolean logSingleSteps = Boolean.valueOf(args[1]);
		Boolean supressInvalid = Boolean.valueOf(args[2]);
		
		zugferdHandler zHandler = zugferdHandler.getInstance();
		List<Invoice> InvoiceList = new ArrayList<Invoice>();
		List<AInvoice> ReducedInvoiceList = new ArrayList<AInvoice>();
		
		System.out.println("Please ignore log4j Warnings");
		//Read Invoices from Folder and add to List
		try{
			zHandler.readInvoice(pathToFolder, InvoiceList, logSingleSteps, supressInvalid);
			System.out.println("All Invoices successfully readed");
		}catch(Exception e){
			System.out.println("Exception while reading from file system");
			System.out.println("Details: ");
			System.out.println(e.getMessage());
		}
		
		InvoiceReducer ir = InvoiceReducer.getInstance();
		try{
			ReducedInvoiceList = ir.ReduceInvoiceList(InvoiceList);
			System.out.println("All Invoices successfully reduced");
		}catch(Exception e){
			System.out.print("Exception while reducing Invoices");
			System.out.println("Details: ");
			System.out.println(e.getMessage());
		}
		
		System.out.println("Breakpoint");
		
	}

}
