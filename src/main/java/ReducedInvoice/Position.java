package ReducedInvoice;

public class Position {
	
	public Position(String Description, Price PositionPrice, String MeasureMent, int Amount, int Taxrate)
	{
		this.Description = Description;
		this.PositionPrice = PositionPrice;
		this.Amount = Amount;
		this.Taxrate = Taxrate;
	}
	
	private String Description;
	
	private Price PositionPrice;
	
	private int Amount;
	
	private String Measurement;
	
	private int Taxrate;

	public String getDescription() {
		return Description;
	}

	public void setDescription(String descrition) {
		Description = descrition;
	}

	public Price getPositionPrice() {
		return PositionPrice;
	}

	public void setPositionPrice(Price positionPrice) {
		PositionPrice = positionPrice;
	}

	public int getAmount() {
		return Amount;
	}

	public void setAmount(int amount) {
		Amount = amount;
	}

	public int getTaxrate() {
		return Taxrate;
	}

	public void setTaxrate(int taxrate) {
		Taxrate = taxrate;
	}

	private String getMeasurement() {
		return Measurement;
	}

	private void setMeasurement(String measurement) {
		Measurement = measurement;
	}
}
