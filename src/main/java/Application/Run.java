package Application;

import Import.*;
import ReducedInvoice.*;
import io.konik.zugferd.Invoice;
import BagOfWords.WordBag;

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
		
		/*
		String pathToInvoices = args[0];
		Boolean logSingleSteps = Boolean.valueOf(args[1]);
		Boolean supressInvalid = Boolean.valueOf(args[2]);
		String pathToInvalidWords = args[3];
		String pathToInvalidTokens = args[4];
		*/
		
		String pathToInvoices = "./src/main/resources/examples_ferd/";
		Boolean logSingleSteps = false;
		Boolean supressInvalid = false;
		String pathToInvalidWords = "./src/main/resources/bag_config/forbiddenInputNeurons";
		String pathToInvalidTokens = "./src/main/resources/bag_config/forbiddenTokens";
		
		zugferdHandler zHandler = zugferdHandler.getInstance();
		List<Invoice> InvoiceList = new ArrayList<Invoice>();
		List<AInvoice> ReducedInvoiceList = new ArrayList<AInvoice>();
		
		//Read Invoices from Folder
		try{
			zHandler.readInvoice(pathToInvoices, InvoiceList, logSingleSteps, supressInvalid);
			System.out.println("All Invoices successfully readed");
		}catch(Exception e){
			System.out.print("Exception while reading Invoices");
			System.out.println("Details: ");
			System.out.println(e.getMessage());
		}
		
		//Reduce Invoices to necessary Informations
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
		
		//Create Bag of Words
		WordBag wb = new WordBag();
		try{
			wb.setPaths(pathToInvalidWords, pathToInvalidTokens);
			for(AInvoice ai : ReducedInvoiceList){
				for(int i = 0; i < ai.getPositionsLength(); i++){
					wb.addSentence(ai.getPosition(i).getDescription());
				}
			}
			//List<String> w = wb.getBag();
			//System.out.println(w);
			System.out.println("Bag of words sucessfully created");
		}catch(Exception e){
			System.out.print("Exception while creating bag of words");
			System.out.println("Details: ");
			System.out.println(e.getMessage());
		}
		
		System.out.println("Breakpoint");
		
	}

}
