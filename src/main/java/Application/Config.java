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
	public static String PATH_TO_NETWORK_OUTPUT = "./src/main/resources/output";
	public static String PATH_TO_NETWORK_DATA = "./src/main/resources/labeled";
	
	/**
	 * Other Test Data
	 */
	
	//public static String PATH_TO_NETWORK_DATA = "./src/main/resources/labeled/other/amazon";
	//public static String PATH_TO_NETWORK_DATA = "./src/main/resources/labeled/other/imdb";
	//public static String PATH_TO_NETWORK_DATA = "./src/main/resources/labeled/other/names";
	//public static String PATH_TO_NETWORK_DATA = "./src/main/resources/labeled/other/yelp";
	
	/**
	 * Test Files Parameter
	 */
	public static String PATH_TO_LABEL_TESTLIST = "./src/main/resources/input/labeled_test/labeled.csv";
	public static String PATH_TO_VOUCHER_TEST_DUMP = "./src/main/resources/labeled_test/VoucherTestDump.csv";
	
	/**
	 * CSV-Handling Parameters
	 */
	public static String VOUCHER_ID = "Beleglink";
	public static String VOLUME_ID = "Umsatz (ohne Soll/Haben-Kz)";
	public static String DEBIT_ACCOUNT_ID = "Gegenkonto (ohne BU-Schlüssel)";
	public static String TAX_KEY_ID = "BU-Schlüssel";
	public static String BOOKING_NOTE = "Buchungstext";
	public static char VOUCHER_CSV_SEPERATOR = ';';
	public static char VOUCHER_CSV_REPLACEMENT = ',';
	
	
	public static String PATH_TO_LABELED_DATA = "./src/main/resources/labeled_test/testDataset_%d.csv";
}