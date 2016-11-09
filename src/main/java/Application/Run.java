package Application;

import Import.*;
import NeuralNetwork.NetworkFacade;
import ReducedInvoice.*;
import Booking.Voucher;
import Booking.Label;
import Booking.CSVBookingHandler;

import java.io.IOException;
import java.util.ArrayList;
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
		List<AInvoice> ReducedInvoiceList = new ArrayList<AInvoice>();
		List<Label> LabelList;

		readInvoicesFromFiles(ReducedInvoiceList);
		
		try
		{
			CSVBookingHandler bHandler = CSVBookingHandler.getInstance();
			List<Voucher> VoucherList = new ArrayList<Voucher>();
			VoucherList = bHandler.getVoucherInfoFromFile(Config.PATH_TO_VOUCHERS, Config.VOLUME_ID, Config.DEBIT_ACCOUNT_ID, Config.TAX_KEY_ID, Config.VOUCHER_ID, Config.VOUCHER_CSV_SEPERATOR);
			LabelList = CSVBookingHandler.getInstance().createReducedInvoiceVoucherList(ReducedInvoiceList,VoucherList, true);
			bHandler.printVoucherListWithoutDebitAccount(ReducedInvoiceList, Config.VOUCHER_CSV_SEPERATOR);
			bHandler.printLabelList(LabelList, Config.VOUCHER_CSV_SEPERATOR);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private static void readInvoicesFromFiles(List<AInvoice> ReducedInvoiceList)
	{
		String pathToInvoices = Config.PATH_TO_INVOICES;
		Boolean logSingleSteps = Config.LOG_SINGLE_STEPS;
		Boolean supressInvalid = Config.SUPRESS_INVALID_INVOICES;
		zugferdHandler zHandler = zugferdHandler.getInstance();
		try
		{
			zHandler.readInvoice(pathToInvoices, ReducedInvoiceList, logSingleSteps, supressInvalid);
			System.out.println("All Invoices successfully readed");
		}
		catch(Exception e)
		{
			System.out.print("Exception while reading Invoices");
			System.out.println("Details: ");
			System.out.println(e.getMessage());
		}
	}

	/**
	 * The core call functions of the neural network
	 */
	
	public static void runNetwork()
	{
		String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789 @€!\"§$%&/()=?+-#<>.,;:_'*´{[]}";
		int frameSize = 258;
		int minibatchSize = 128;
		int numPossibleLabels = 4;
		boolean regression = false;
		String path = "./src/main/resources/labeled_data/";
		int numberOfCores = 2;
		int numberOfEpochs = 30;
		
		NetworkFacade nf = NetworkFacade.getInstance();
		nf.setConfigurationParameters(path, frameSize, alphabet, minibatchSize, numberOfCores, numberOfEpochs);
		nf.train3DModel();
		if(nf.readData()){
			nf.configNetwork();
			nf.trainNetwork();
			System.out.println(nf.testNetwork());
		}
	}
}
