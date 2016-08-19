package ReducedInvoice;

import io.konik.zugferd.*;
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
	
	
	public List<AInvoice> ReduceInvoiceList(List<Invoice> invoiceList){
		
		List<AInvoice> returnValue = null;
		for(int i=0; i < invoiceList.size(); i++){
			try{
				if(invoiceList.get(i) != null){
					RInvoice addToList = (this.ConvertInvoiceToRinvoice(invoiceList.get(i)));
					if(addToList != null)
						returnValue.add(addToList);
				}
			}catch(Exception e){
				System.out.println(i + " falsy");
			}
		}
		return returnValue;
	}
	
	
	@SuppressWarnings("deprecation")
	public RInvoice ConvertInvoiceToRinvoice(Invoice invoice){
		
		RInvoice returnValue = new RInvoice();
		try{
			if(invoice.getHeader().getIssued() != null){
			returnValue.setInvoiceDate(new Date(
					invoice.getHeader().getIssued().getYear(), 
					invoice.getHeader().getIssued().getMonth(), 
					invoice.getHeader().getIssued().getDay()));
			}
			returnValue.setInvoiceNumber(invoice.getHeader().getInvoiceNumber());
			returnValue.setBuyerName(invoice.getTrade().getAgreement().getBuyer().getName());
			returnValue.setSellerName(invoice.getTrade().getAgreement().getSeller().getName());
			returnValue.setCurrency(invoice.getTrade().getSettlement().getCurrency().getName());
			returnValue.setTotalPrice(new Price(
					invoice.getTrade().getSettlement().getMonetarySummation().getGrandTotal().getValue().floatValue(),
					invoice.getTrade().getSettlement().getMonetarySummation().getTaxBasisTotal().getValue().floatValue(),
					invoice.getTrade().getSettlement().getMonetarySummation().getTaxTotal().getValue().floatValue()));
			for(Item elem : invoice.getTrade().getItems()){
				if(elem != null){
					
					float lineTaxPerc = elem.getSettlement().getTradeTax().get(0).getPercentage().floatValue();
					float lineSumNet = elem.getSettlement().getMonetarySummation().getLineTotal().getValue().floatValue();
					float lineSumGross = lineSumNet + ( (lineSumNet * lineTaxPerc) / 100 );
					returnValue.addPosition( new Position(
							elem.getProduct().getName(),
							new Price(lineSumGross, lineSumNet, lineTaxPerc),
							elem.getDelivery().getBilled().getUnitCode(),
							elem.getDelivery().getBilled().getValue().intValue(),
							(int)lineTaxPerc));
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
