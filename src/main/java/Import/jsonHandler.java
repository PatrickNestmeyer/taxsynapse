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
		InvoiceReducer ir = InvoiceReducer.getInstance();
		//Rekursive => elem can be a folder or a file 
		Files.walk(Paths.get(Path)).forEach(elem ->
		{
			String fileName = elem.getFileName().toString();
			String path = elem.toString();
			try{
				if (Files.isDirectory(elem))
				{
					System.out.println("test");
				}
				else
				{	
					if(Files.isRegularFile(elem))
					{
						if(fileName.toLowerCase().contains(".json"))
						{
							JSONParser parser = new JSONParser();
							
							Object object = parser.parse(new FileReader(path));
							
							JSONObject jsonObject = (JSONObject) object;
							
							System.out.println(jsonObject.get("credit_note"));

							JSONArray arr = new JSONArray();
							
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
