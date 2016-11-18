package Application;

import Import.*;
import NeuralNetwork.NetworkFacade;
import ReducedInvoice.*;
import Booking.Voucher;
import Booking.Label;
import Booking.CSVBookingHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.deeplearning4j.ui.weights.ConvolutionalIterationListener;

public class Run {
	
	/**
	 * The main entry point of the application
	 * Here are two paths possible
	 * a) the preparation of the data. 
	 * b) the configuration and run of the convolutinal neural network
	 * 
	 */
	
	public static void main(String[] args) throws IOException 
	{
		
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
	
	public static void runDataPreprocessing()
	{
		/**
		 * Lists for the Invoices, vouchers (bookingtable data) and labels (positions+account info)
		 */
		List<AInvoice> ReducedInvoiceList = new ArrayList<AInvoice>();
		List<Voucher> VoucherList = new ArrayList<Voucher>();
		List<Label> LabelList;
		
		
		//read ZUGFERDs, reduce them to necessary informations and add them to the List
		readInvoicesFromFiles(ReducedInvoiceList);
		
		try
		{
			//Get File-Manager for csv
			CSVBookingHandler bHandler = CSVBookingHandler.getInstance();
			
			//Read vouchers 
			VoucherList = bHandler.getVoucherInfoFromFolder(Config.PATH_TO_VOUCHERS, Config.VOLUME_ID, Config.DEBIT_ACCOUNT_ID, Config.TAX_KEY_ID, Config.VOUCHER_ID, Config.VOUCHER_CSV_SEPERATOR);
			
			//Creates a List with both information voucher and invoice
			//TODO: The createReducedInvoiceVoucherList fails 
			LabelList = bHandler.createReducedInvoiceVoucherList(ReducedInvoiceList,VoucherList, true);
			
			//TODO: This file is probably not necessary
			bHandler.printVoucherListWithoutDebitAccount(ReducedInvoiceList,Config.PATH_TO_VOUCHER_TEST_DUMP, Config.VOUCHER_CSV_SEPERATOR);
			
			/**
			 * TODO: This has to be changed by the following:
			 * TODO: 1. scramble the list
			 * TODO: 2. Divide the list 9:1 into two list
			 * TODO: 3. print the greater list as training data and the smaller list as testdata
			 * TODO: 4. print two diffrent files of same length for each of training and test, 
			 *          one for the labels and one for the descriptions. This Makes in total 4 files.
			 */
			bHandler.printLabelList(LabelList, Config.PATH_TO_LABEL_LIST, Config.VOUCHER_CSV_SEPERATOR);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private static void readInvoicesFromFiles(List<AInvoice> ReducedInvoiceList)
	{ 
		Boolean logSingleSteps = Config.LOG_SINGLE_STEPS;
		Boolean supressInvalid = Config.SUPRESS_INVALID_INVOICES;
		zugferdHandler zHandler = zugferdHandler.getInstance();
		jsonHandler jHandler = jsonHandler.getInstance();
		try
		{
			zHandler.readInvoice(Config.PATH_TO_ZUGFERD_INVOICES, ReducedInvoiceList, logSingleSteps, supressInvalid);
			jHandler.readReducedInvoice(Config.PATH_TO_JSON_INVOICES, ReducedInvoiceList);
			System.out.println("All Invoices successfully readed");
		}
		catch(Exception e)
		{
			System.out.println("Exception while reading Invoices");
			System.out.println("Details: ");
			System.out.print(e.getMessage());
		}
	}

	/**
	 * The core call functions of the neural network
	 */
	
	public static void runNetwork()
	{
		String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789 @�!\"�$%&/()=?+-#<>.,;:_'*�{[]}";
		int inputLength = 258;
		int outputLength = 4;
		int minibatch = 64;
		String path_to_data = Config.PATH_TO_NETWORKDATA;
		int cores = Runtime.getRuntime().availableProcessors();
		int epochs = 1;
		double leraningRate = 1e-3; //0.1 to 1e-6 || try 1e-1, 1e-3 and 1e-6
		double regularization = 1e-6; //1e-3 to 1e-6
		double momentum = 0.9; //common value is 0.9
		
		NetworkFacade networkManager = NetworkFacade.getInstance();
		networkManager.setProperties(alphabet, inputLength, outputLength, "small");
		if(networkManager.readData(path_to_data)){
			networkManager.build(leraningRate, momentum, regularization);
			networkManager.train(minibatch, epochs, cores);
			System.out.println(networkManager.test());
		}
	}
}
