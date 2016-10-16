package ReducedInvoice;

import io.konik.zugferd.*;
import io.konik.zugferd.entity.trade.MonetarySummation;
import io.konik.zugferd.entity.trade.item.Item;
import java.util.Date;
import java.util.List;
import ReducedInvoice.AInvoice.Error;

public class InvoiceReducer {

	private static InvoiceReducer uniqueInstance = null;
	
	private static int MetaExceptionCounter = 0;
	
	private static int PositionExceptionCounter = 0;
	
	private InvoiceReducer(){};
	
	public static InvoiceReducer getInstance(){
		if(uniqueInstance == null) {
			synchronized(InvoiceReducer.class){
				if(uniqueInstance == null){
					uniqueInstance = new InvoiceReducer();
				}
			}
		}
		return uniqueInstance;
	}

	/**
	 * veraltet
	 * 
	 * @param InvoiceList
	 * @param ReducedInvoiceList
	 *
	 *	public void ReducedInvoiceList(List<Invoice> InvoiceList, List<AInvoice> ReducedInvoiceList)
	 *	{
	 *		for(int i = 0; i < InvoiceList.size(); i++)
	 *		{
	 *			try
	 *			{
	 *				if(InvoiceList.get(i) != null)
	 *				{
	 *					RInvoice addToList = (ConvertInvoiceToRinvoice(InvoiceList.get(i)));
	 *					if(addToList != null)
	 *						ReducedInvoiceList.add(addToList);
	 *				}
	 *			}
	 *			catch(Exception e)
	 *			{
	 *				System.out.println(i + " falsy");
	 *			}
	 *		}
	 *	}
	 *
	 */
	
	public RInvoice ConvertInvoiceToRinvoice(Invoice invoice, String Beleglink)
	{	
		RInvoice returnValue = new RInvoice();
		AddMetaInformation(invoice, returnValue, Beleglink);
		AddPositions(invoice, returnValue);
		validateInvoice(returnValue);
		return returnValue;	
	}
	
	/**
	 * @param invoice
	 * @param returnValue
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public void AddMetaInformation(Invoice invoice, RInvoice returnValue, String Beleglink)
	{
		try
		{
			returnValue.setBeleglink(Beleglink);
			if(invoice.getHeader().getIssued() != null)
			{
				returnValue.setInvoiceDate(new Date(
						invoice.getHeader().getIssued().getYear(), 
						invoice.getHeader().getIssued().getMonth(), 
						invoice.getHeader().getIssued().getDay()));
			}
			returnValue.setInvoiceNumber(invoice.getHeader().getInvoiceNumber());  //wichtig
			returnValue.setBuyerName(invoice.getTrade().getAgreement().getBuyer().getName());
			returnValue.setSellerName(invoice.getTrade().getAgreement().getSeller().getName());
			returnValue.setCurrency(invoice.getTrade().getSettlement().getCurrency().getName());
			
			returnValue.setTotalPrice(checkMonetarySummation(invoice.getTrade().getSettlement().getMonetarySummation())); 
		}
		catch(Exception e)
		{
			this.MetaExceptionCounter++;
		}
	}
	
	public Price checkMonetarySummation(MonetarySummation gesamtpreis)
	{
		float GrandTotal = 0;
		float TaxBasisTotal = 0;
		float TaxTotal = 0;
		boolean[] TotalPrice = new boolean[3];
		int count = 0;
		
		if(gesamtpreis.getGrandTotal() != null)
		{
			if(gesamtpreis.getGrandTotal().getValue() != null)
			{
				GrandTotal = gesamtpreis.getGrandTotal().getValue().floatValue();
				TotalPrice[0] = true;
				count++;
			}
		}
		if(gesamtpreis.getTaxBasisTotal() != null)
		{
			if(gesamtpreis.getTaxBasisTotal().getValue() != null)
			{
				TaxBasisTotal = gesamtpreis.getTaxBasisTotal().getValue().floatValue();
				TotalPrice[1] = true;
				count++;
			}
		}
		if(gesamtpreis.getTaxTotal() != null)
		{
			if(gesamtpreis.getTaxTotal().getValue() != null)
			{
				TaxTotal = gesamtpreis.getTaxTotal().getValue().floatValue();
				TotalPrice[2] = true;
				count++;
			}
		}
		
		if(count == 2)
		{
			if(TotalPrice[0] == false)
			{
				GrandTotal = TaxBasisTotal + TaxTotal;				
			}
			else if(TotalPrice[1] == false)
			{
				TaxBasisTotal = GrandTotal - TaxTotal;
			}
			else if(TotalPrice[2] == false)
			{
				TaxTotal = GrandTotal - TaxBasisTotal;
			}
		}
		
		return new Price(GrandTotal, TaxBasisTotal, TaxTotal);
	}

	
	/**
	 * @todo if elem.getDelivery().getBilled().getUnitCode() == null
	 * @param invoice
	 * @param returnValue
	 * @return
	 */
	public void AddPositions(Invoice invoice, RInvoice returnValue)
	{
		try
		{
		int lastPosition = -1;
		String lastPositionName = "";
		
		for(Item elem : invoice.getTrade().getItems())
		{
			if(elem != null)
			{
				String Measurement = "";
				int amount = 1;
				float lineTaxPerc = 0;
				float lineSumNet = 0;
				float lineSumGross = 0;
				Error positionerror = Error.NO_ERROR;
				
				// Preis einer Position
				if(elem.getSettlement() != null)
				{
					if(elem.getSettlement().getMonetarySummation() != null)
					{
						lineSumNet = elem.getSettlement().getMonetarySummation().getLineTotal().getValue().floatValue();
					}
					else
					{
						positionerror = Error.PRICE_ERROR;
					}
					if(elem.getSettlement().getTradeTax() != null  && elem.getSettlement().getTradeTax().size() >= 1)
					{
						lineTaxPerc = elem.getSettlement().getTradeTax().get(0).getPercentage().floatValue();
						lineSumGross = lineSumNet + ( (lineSumNet * lineTaxPerc) / 100 );
					}
					else
					{
						positionerror = Error.PRICE_ERROR;
					}
				}
				
				// Name der Position
				if(elem.getProduct() != null)
				{
					if(elem.getProduct().getName() != null)
					{
						if(elem.getDelivery().getBilled() != null)
						{
							Measurement = elem.getDelivery().getBilled().getUnitCode();
							amount = elem.getDelivery().getBilled().getValue().intValue();
						}
						if(lastPosition == -1)
						{
							lastPositionName = elem.getProduct().getName();
							returnValue.addPosition( new Position(
									elem.getProduct().getName(),
									new Price(lineSumGross, lineSumNet, lineTaxPerc),
									Measurement,
									amount,
									(int)(lineTaxPerc),
									positionerror));
							lastPosition++;
						}
						else
						{
							if(lastPositionName.equals(elem.getProduct().getName()))
							{
								returnValue.getPosition(lastPosition).setAmount(returnValue.getPosition(lastPosition).getAmount() + elem.getDelivery().getBilled().getValue().intValue());
							}
							else
							{
								/* 
								 * @todo if elem.getDelivery().getBilled().getUnitCode() == null
								 */
								lastPositionName = elem.getProduct().getName();
								returnValue.addPosition( new Position(
										elem.getProduct().getName(),
										new Price(lineSumGross, lineSumNet, lineTaxPerc),
										Measurement,
										amount,
										(int)(lineTaxPerc),
										positionerror));
								lastPosition++;		
							}

						}
					}
				}
				
			}
		}
		}
		catch(Exception e)
		{
			this.PositionExceptionCounter++;
		}
	}
	
	public void validateInvoice(RInvoice returnValue)
	{
		Error invoiceerror = Error.NO_ERROR;
		float GrandTotal = 0;
		float TaxBasisTotal = 0;
		float TaxTotal = 0;
		for(int i = 0; i < returnValue.getPositionsLength(); i++)
		{
			GrandTotal = GrandTotal + returnValue.getPosition(i).getPositionPrice().getBrutto();
			TaxBasisTotal = TaxBasisTotal + returnValue.getPosition(i).getPositionPrice().getNetto();
			TaxTotal = TaxTotal + (returnValue.getPosition(i).getPositionPrice().getBrutto() - returnValue.getPosition(i).getPositionPrice().getNetto());
		}
		GrandTotal = Runden(GrandTotal);
		TaxBasisTotal = Runden(TaxBasisTotal);
		TaxTotal = Runden(TaxTotal);
		
		if(GrandTotal != returnValue.getTotalPrice().getBrutto() || TaxBasisTotal != returnValue.getTotalPrice().getNetto() || TaxTotal != returnValue.getTotalPrice().getSteuer())
		{
			invoiceerror = Error.PRICE_ERROR;
		}
		returnValue.setInvoiceError(invoiceerror);
	}
	
	public float Runden(float a)
	{
		if(((a*1000) % 10) > 4  )
		{
			a = (float)((int)(a*100+1))/100; 
		}
		else
		{
			a = (float)((int)(a*100))/100; 
		}
		return a;
	}

	public static int getMetaExceptionCounter() {
		return MetaExceptionCounter;
	}
	
	public static int getPositionExceptionCounter(){
		return PositionExceptionCounter;
	}
}
