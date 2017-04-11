package Application;

public final class Config {
	//Simulating static class
	private Config(){}
	
	/**
	 * Config Parameters
	 */
	public static boolean LOG_SINGLE_STEPS = true;
	public static boolean SUPRESS_INVALID_INVOICES = false;
	public static boolean COPY_TRAIN_DATA = false;
	public static int COPY_TRAIN_DATA_SIZE = 20;
	// Falls Echtdaten verwendet werden Datei nach der Auswertung der Fehler löschen
	public static boolean LOG_INVOICE_AND_VOUCHER_ERROR = true;
	
	/**
	 * Data Input and Output Location Parameters
	 */
	public static String PATH_TO_VOUCHERS = "./src/main/resources/input/vouchers";
	public static String PATH_TO_JSON_INVOICES = "./src/main/resources/input/invoices/json";
	public static String PATH_TO_ZUGFERD_INVOICES = "./src/main/resources/input/invoices/zugferd";
	public static String PATH_TO_LABELS = "./src/main/resources/labeled_test/output";
	
	/**
	 * Error in Data Input Parameters
	 */
	public static String PATH_TO_FAILED_VOUCHER = "./src/main/resources/labeled_test/output";	
		
	/**
	 * Network Input and Output Parameters
	 */
	public static String PATH_TO_NETWORK_DATA = "./src/main/resources/labeled_test/output";
	public static String PATH_TO_NETWORK_OUTPUT = "./src/main/resources/output";
	
	/**
	 * Other Test Data
	 */
	//public static String PATH_TO_NETWORK_DATA = "./src/main/resources/labeled/other/amazon";
	//public static String PATH_TO_NETWORK_DATA = "./src/main/resources/labeled/other/imdb";
	//public static String PATH_TO_NETWORK_DATA = "./src/main/resources/labeled/other/names";
	//public static String PATH_TO_NETWORK_DATA = "./src/main/resources/labeled/other/yelp";
	
	
	/**
	 * Network Parameters
	 */
	public static String NETWORK_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789 @€!\"$%&/()=?+-#<>.,;:_-'*+#{[]}";
	public static int NETWORK_INPUT_LENGHT = 258;
	public static int NETWORK_OUTPUT_LENGTH = 4;
	public static int NETWORK_MINIBATCH = 64;
	public static String NETWORK_PATH_TO_DATA = Config.PATH_TO_NETWORK_DATA;
	public static int NETWORK_CORES = Runtime.getRuntime().availableProcessors();
	public static int NETWORK_EPOCHS = 1;
	public static double NETWORK_LEARNING_RATE = 1e-1; //0.1 to 1e-6 || try 1e-1, 1e-3 and 1e-6
	public static double NETWORK_REGULARIZATION = 1e-3; //1e-3 to 1e-6
	public static double NETWORK_MOMENTUM = 0.1; //common value is 0.9
	
	
	/**
	 * CSV-Handling Parameters
	 */
	public static String VOUCHER_ID = "Beleglink";
	public static String VOLUME_ID = "Umsatz (ohne Soll/Haben-Kz)";
	public static String DEBIT_ACCOUNT_ID = "Konto";
	public static String TAX_KEY_ID = "BU-Schlüssel";
	public static String BOOKING_NOTE = "Buchungstext";
	public static char VOUCHER_CSV_SEPERATOR = ';';
	public static char VOUCHER_CSV_REPLACEMENT = ',';
	
	public static boolean SHUFFLE_NETWORKDATA = false;
	
	public static String PATH_TO_LABELED_DATA = "./src/main/resources/labeled_test/testDataset_%d.csv";
}