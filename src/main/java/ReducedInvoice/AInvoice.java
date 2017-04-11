package ReducedInvoice;

import java.util.Date;
import java.util.List;

public abstract class AInvoice {
	
	public enum Error {PRICE_ERROR, NO_ERROR, VOUCHER_ID_ERROR, RECONSTRUCT_ERROR }; 
	
	//Date
	private Date invoiceDate;
	
	//Beleglink
	private String VoucherID;
	
	//Number
	private String invoiceNumber;
	
	//Buyer
	protected String buyerName;
	
	//Seller
	protected String sellerName;
	
	//TotalPrice
	protected Price TotalPrice;
	
	//Positions
	protected List<Position> Positions;
	
	//MetaData
	private Metadata MetaData;
	
	protected String Currency;
	
	protected Error InvoiceError;

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	
	public abstract void addPosition(Position p);
	
	public abstract void addPosition(String desc, Price p, String mm, int amount, int tax);
	
	public abstract void addPosition(String desc, Price p, String mm, int amount, int tax, Error positionerror);
	
	public abstract Position getPosition(int index);
	
	public abstract List<Position> getPositions();
	
	public abstract void setPosition(Position position, int index);
	
	public abstract void setPositionDescription(String description, int index);
	
	public abstract void setPositionPrice(Price price, int index);
	
	public abstract void setPositionAmount(int amount, int index);
	
	public abstract void setPositionTaxRate(int taxrate, int index);
	
	public abstract int getPositionsLength();
	
	public String getVoucherID()
	{
		return VoucherID;
	}
	
	public void setVoucherID(String VoucherID)
	{
		this.VoucherID = VoucherID;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	

	public String getCurrency() {
		return Currency;
	}
	

	public void setCurrency(String currency) {
		Currency = currency;
	}

	
	public Price getTotalPrice() {
		return TotalPrice;
	}

	
	public void setTotalPrice(Price totalPrice) {
		TotalPrice = totalPrice;
	}
	
	public Error getInvoiceError()
	{
		return InvoiceError;
	}
	
	public void setInvoiceError(Error invoiceerror)
	{
		InvoiceError = invoiceerror;
	}
	
	public void setInvoiceError_VOUCHER_ID_ERROR()
	{
		InvoiceError = Error.VOUCHER_ID_ERROR;
	}
	
	public void setInvoiceError_RECONSTRUCT_ERROR()
	{
		InvoiceError = Error.RECONSTRUCT_ERROR;
	}
	
	public boolean checkVoucherIDError()
	{
		if(InvoiceError == Error.VOUCHER_ID_ERROR)
			return true;
		else
			return false;
	}
	
	public boolean checkReconstructVoucherError()
	{
		if(InvoiceError == Error.RECONSTRUCT_ERROR)
			return true;
		else
			return false;
	}

	public Metadata getMetaData() {
		return MetaData;
	}

	public void setMetaData(Metadata metaData) {
		MetaData = metaData;
	}

}
