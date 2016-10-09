package Booking;

public class Voucher {
	
	//Sollkonto
	private String debitAccount;
	//Gesamtbetrag der Buchung
	private String volume;
	//Steuerschlüssel
	private String taxKey;
	//Beleglink
	private String voucherID;
	
	public Voucher(String debitAccount, String volume, String taxKey, String voucherID){
		this.setDebitAccount(debitAccount);
		this.setVolume(volume);
		this.setTaxKey(taxKey);
		this.setVoucherID(voucherID);
	}

	public String getDebitAccount() {
		return debitAccount;
	}

	public void setDebitAccount(String debitAccount) {
		this.debitAccount = debitAccount;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getTaxKey() {
		return taxKey;
	}

	public void setTaxKey(String taxKey) {
		this.taxKey = taxKey;
	}

	public String getVoucherID() {
		return voucherID;
	}

	public void setVoucherID(String voucherID) {
		this.voucherID = voucherID;
	}
	
	
}