package ReducedInvoice;

public class Price 
{
	public Price(float Gross, float Net, float Tax){
		this.Gross = Gross;
		this.Net = Net;
		this.Tax = Tax;
	}
	
	private float Gross;
	
	private float Net;
	
	private float Tax;

	public float getBrutto() {
		return Gross;
	}

	public void setBrutto(float brutto) {
		Gross = brutto;
	}

	public float getNetto() {
		return Net;
	}

	public void setNetto(float netto) {
		Net = netto;
	}

	public float getSteuer() {
		return Tax;
	}

	public void setSteuer(float steuer) {
		Tax = steuer;
	}
	
}
