package Import;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;


import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import ReducedInvoice.AInvoice;
import ReducedInvoice.InvoiceReducer;
import ReducedInvoice.Price;
import ReducedInvoice.RInvoice;
import io.konik.PdfHandler;
import io.konik.exception.TransformationException;
import io.konik.zugferd.Invoice;

public class jsonHandler 
{

	private static jsonHandler uniqueInstance = null;
	
	private jsonHandler(){};
	
	public static jsonHandler getInstance()
	{
		if(uniqueInstance == null) 
		{
			synchronized(jsonHandler.class)
			{
				if(uniqueInstance == null)
				{
					uniqueInstance = new jsonHandler();
				}
			}
		}
		return uniqueInstance;
	}
	
	public Invoice readReducedInvoice(String Path) throws IOException
	{
		
		PdfHandler handler = new PdfHandler();
		
		FileInputStream input = new FileInputStream(new File(Path));
		
		Invoice invoice = handler.extractInvoice(input);
		
		if(invoice != null)
			return invoice;
		else
			return null;
	}
	
	public void readReducedInvoice(String Path, List<AInvoice> ReducedInvoiceList) throws Exception
	{
		//Recursive => elem can be a folder or a file 
		Files.walk(Paths.get(Path)).forEach(elem ->
		{
			String fileName = elem.getFileName().toString();
			String path = elem.toString();
			try{
				if (Files.isDirectory(elem))
				{
					
				}
				else
				{
					if(Files.isRegularFile(elem))
					{
						if(fileName.toLowerCase().contains(".json"))
						{
							RInvoice returnValue = new RInvoice(); 
							JSONParser parser = new JSONParser();
							Object object = parser.parse(new FileReader(path));
							JSONObject root = (JSONObject) object;
							
							returnValue.setVoucherID(fileName);
							returnValue.setInvoiceNumber( (String) ((JSONObject) root.get("invoice_number")).get("value"));
							returnValue.setTotalPrice(new Price(
									(float) ((double) ((JSONObject) root.get("total_gross_amount")).get("value")),
									(float) ((double) ((JSONObject) root.get("total_net_amount")).get("value")),
									19));			
							
							JSONArray arr = (JSONArray) root.get("line_items");
							JSONObject temp;
							for (int i = 0; i < arr.size(); i++)
							{
								temp = (JSONObject) arr.get(i);
								returnValue.addPosition((String)temp.get("text"), 
										new Price((float) ((double) temp.get("line_amount")*1.19), (float) (((double) temp.get("line_amount"))),19)
										, ""
										, (int) ((double) temp.get("line_quantity"))
										, 19);							
							}
							ReducedInvoiceList.add(returnValue);
							System.out.println("json invoice added");
						}
					}
				}
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		});
	}
}