package ReducedInvoice;

import io.konik.zugferd.*;
import io.konik.zugferd.entity.trade.MonetarySummation;
import io.konik.zugferd.entity.trade.item.Item;
import java.util.Date;
import java.util.List;

public class InvoiceReducer {

	private static InvoiceReducer uniqueInstance = null;
	
	private static int exceptionCounter = 0;
	
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

	public void ReducedInvoiceList(List<Invoice> InvoiceList, List<AInvoice> ReducedInvoiceList)
	{
		for(int i = 0; i < InvoiceList.size(); i++)
		{
			try
			{
				if(InvoiceList.get(i) != null)
				{
					RInvoice addToList = (ConvertInvoiceToRinvoice(InvoiceList.get(i)));
					if(addToList != null)
						ReducedInvoiceList.add(addToList);
				}
			}
			catch(Exception e)
			{
				System.out.println(i + " falsy");
			}
		}
	}
	
	public Price checkMonetarySummation(MonetarySummation gesamtpreis)
	{
		float GrandTotal = 0;
		float TaxBasisTotal = 0;
		float TaxTotal = 0;
		if(gesamtpreis.getGrandTotal().getValue() != null)
		{
			GrandTotal = gesamtpreis.getGrandTotal().getValue().floatValue();
		}
		if(gesamtpreis.getTaxBasisTotal().getValue() != null)
		{
			TaxBasisTotal = gesamtpreis.getTaxBasisTotal().getValue().floatValue();
		}
		if(gesamtpreis.getTaxTotal().getValue() != null)
		{
			TaxTotal = gesamtpreis.getTaxTotal().getValue().floatValue();
		}
		
		if(GrandTotal == 0)
		{
			GrandTotal = TaxBasisTotal + TaxTotal;
		}
		
		return new Price(GrandTotal, TaxBasisTotal, TaxTotal);
	}
	
	@SuppressWarnings("deprecation")
	public RInvoice ConvertInvoiceToRinvoice(Invoice invoice){
		
		RInvoice returnValue = new RInvoice();
		try{
			if(invoice.getHeader().getIssued() != null)
			{
				returnValue.setInvoiceDate(new Date(
						invoice.getHeader().getIssued().getYear(), 
						invoice.getHeader().getIssued().getMonth(), 
						invoice.getHeader().getIssued().getDay()));
			}
			returnValue.setInvoiceNumber(invoice.getHeader().getInvoiceNumber());
			returnValue.setBuyerName(invoice.getTrade().getAgreement().getBuyer().getName());
			returnValue.setSellerName(invoice.getTrade().getAgreement().getSeller().getName());
			returnValue.setCurrency(invoice.getTrade().getSettlement().getCurrency().getName());
			
			returnValue.setTotalPrice(checkMonetarySummation(invoice.getTrade().getSettlement().getMonetarySummation())); 
			
			int lastPosition = -1;
			String lastPositionName = "";
			
			for(Item elem : invoice.getTrade().getItems()){
				if(elem != null)
				{
					float lineTaxPerc = 0;
					float lineSumNet = 0;
					float lineSumGross = 0;
					if(elem.getSettlement() != null)
					{
						lineTaxPerc = elem.getSettlement().getTradeTax().get(0).getPercentage().floatValue();
						lineSumNet = elem.getSettlement().getMonetarySummation().getLineTotal().getValue().floatValue();
						lineSumGross = lineSumNet + ( (lineSumNet * lineTaxPerc) / 100 );
					}
					if(elem.getProduct() != null && elem.getDelivery() != null)
					{
						if(elem.getProduct().getName() != null)
						{
							if(lastPosition == -1)
							{
								lastPositionName = elem.getProduct().getName();
								returnValue.addPosition( new Position(
										elem.getProduct().getName(),
										new Price(lineSumGross, lineSumNet, lineTaxPerc),
										elem.getDelivery().getBilled().getUnitCode(),
										elem.getDelivery().getBilled().getValue().intValue(),
										(int)lineTaxPerc));
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
									lastPositionName = elem.getProduct().getName();
									returnValue.addPosition( new Position(
											elem.getProduct().getName(),
											new Price(lineSumGross, lineSumNet, lineTaxPerc),
											elem.getDelivery().getBilled().getUnitCode(),
											elem.getDelivery().getBilled().getValue().intValue(),
											(int)lineTaxPerc));
									lastPosition++;		
								}

							}
						}
					}
				}
			}
		}catch(Exception e){
			this.exceptionCounter++;
		}
		return returnValue;	
	}
	

	public static int getExceptionCounter() {
		return exceptionCounter;
	}
}
