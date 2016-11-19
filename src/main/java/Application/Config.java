package Application;

public final class Config {
	//Simulating static class
	private Config(){}
	
	/**
	 * Config Parameters
	 */
	public static boolean LOG_SINGLE_STEPS = true;
	public static boolean SUPRESS_INVALID_INVOICES = false;
	
	/**
	 * Input Location Parameters
	 */
	public static String PATH_TO_ZUGFERD_INVOICES = "./src/main/resources/input/invoices/zugferd";
	public static String PATH_TO_JSON_INVOICES = "./src/main/resources/input/invoices/json";
	public static String PATH_TO_VOUCHERS = "./src/main/resources/input/vouchers";
	public static String PATH_TO_NETWORKDATA = "./src/main/resources/labeled";
	//public static String PATH_TO_NETWORKDATA = "./src/main/resources/labeled/other/names";
	
	/**
	 * Test Files Parameter
	 */
	public static String PATH_TO_LABEL_LIST = "./src/main/resources/vouchers/traingNeuralNetwerk.csv";
	public static String PATH_TO_VOUCHER_TEST_DUMP = "./src/main/resources/VoucherTestDump.csv";
	
	/**
	 * CSV-Handling Parameters
	 */
	public static String VOUCHER_ID = "Beleglink";
	public static String VOLUME_ID = "Umsatz (ohne Soll/Haben-Kz)";
	public static String DEBIT_ACCOUNT_ID = "Gegenkonto (ohne BU-Schl�ssel)";
	public static String TAX_KEY_ID = "BU-Schl�ssel";
	public static String BOOKING_NOTE = "Buchungstext";
	public static char VOUCHER_CSV_SEPERATOR = ';';
	public static char VOUCHER_CSV_REPLACEMENT = ',';
	
	
	public static String PATH_TO_LABELED_DATA = "./src/main/resources/labeled_data/testDataset_%d.csv";
}