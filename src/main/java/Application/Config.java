package Application;

public final class Config {
	//Simulating static class
	private Config(){}
	
	public static boolean LOG_SINGLE_STEPS = false;
	public static boolean SUPRESS_INVALID_INVOICES = false;
	public static String PATH_TO_INVOICES = "./src/main/resources/examples_ferd/";
	
	public static String PATH_TO_INVALID_WORDS = "./src/main/resources/bag_config/forbiddenInputNeurons";
	public static String PATH_TO_INVALID_TOKENS = "./src/main/resources/bag_config/forbiddenTokens";
	public static String PATH_TO_VOUCHERS = "./src/main/resources/vouchers/DTVF_Buchungsstapel.csv";
	
	
	public static String VOUCHER_ID = "Beleglink";
	public static String VOLUME_ID = "Umsatz (ohne Soll/Haben-Kz)";
	public static String DEBIT_ACCOUNT_ID = "Gegenkonto (ohne BU-Schl�ssel)";
	public static String TAX_KEY_ID = "BU-Schl�ssel";
	public static char VOUCHER_CSV_SEPERATOR = ';';
}