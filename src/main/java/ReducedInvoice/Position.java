package ReducedInvoice;

import ReducedInvoice.AInvoice.Error;

public class Position {
	
	public Position(String Description, Price PositionPrice, String MeasureMent, int Amount, int Taxrate, Error positionerror)
	{
		this.Description = Description;
		this.PositionPrice = PositionPrice;
		this.Measurement = MeasureMent;
		this.Amount = Amount;
		this.Taxrate = Taxrate;
		this.PositionError = positionerror;
	}
	
	private String Description;
	
	private Price PositionPrice;
	
	private int Amount;
	
	private String Measurement;
	
	private int Taxrate;
	
	private Error PositionError;

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
	
	private Error getError()
	{
		return PositionError;
	}
	
	private void setError(Error positionerror)
	{
		PositionError = positionerror;
	}
	
}
