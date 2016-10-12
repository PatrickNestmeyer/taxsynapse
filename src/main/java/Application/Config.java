package Application;

public final class Config {
	//Simulating static class
	private Config(){}
	
	public static boolean LOG_SINGLE_STEPS = true;
	public static boolean SUPRESS_INVALID_INVOICES = false;
	public static String PATH_TO_INVOICES = "./src/main/resources/examples_ferd/";
	
	public static String PATH_TO_INVALID_WORDS = "./src/main/resources/bag_config/forbiddenInputNeurons";
	public static String PATH_TO_INVALID_TOKENS = "./src/main/resources/bag_config/forbiddenTokens";
	public static String PATH_TO_VOUCHERS = "./src/main/resources/vouchers/voucherTetDump_2.csv";
	
	public static String PATH_TO_VOUCHER_TEST_DUMP = "./src/main/resources/VoucherTestDump.csv";
	public static String VOUCHER_ID = "Beleglink";
	public static String VOLUME_ID = "Umsatz (ohne Soll/Haben-Kz)";
	public static String DEBIT_ACCOUNT_ID = "Gegenkonto (ohne BU-Schlüssel)";
	public static String TAX_KEY_ID = "BU-Schlüssel";
	public static String BOOKING_NOTE = "Buchungstext";
	public static char VOUCHER_CSV_SEPERATOR = ';';
	public static char VOUCHER_CSV_REPLACEMENT = ',';
	
	public static String PATH_TO_LABELED_DATA = "./src/main/resources/labeled_data/testDataset_%d.csv";
}