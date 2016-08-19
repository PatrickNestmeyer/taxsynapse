package Import;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import io.konik.*;
import io.konik.exception.TransformationException;
import io.konik.validation.InvoiceValidator;
import io.konik.zugferd.Invoice;

public class zugferdHandler {

	private static zugferdHandler uniqueInstance = null;
	
	private zugferdHandler(){};
	
	public static zugferdHandler getInstance(){
		if(uniqueInstance == null) {
			synchronized(zugferdHandler.class){
				if(uniqueInstance == null){
					uniqueInstance = new zugferdHandler();
				}
			}
		}
		return uniqueInstance;
	}
	
	public Invoice readInvoice(String Path) throws IOException{
		
		PdfHandler handler = new PdfHandler();
		
		FileInputStream input = new FileInputStream(new File(Path));
		
		Invoice invoice = handler.extractInvoice(input);
		
		if(invoice != null)
			return invoice;
		else
			return null;
	}
	
	public void readInvoice(String Path, List<Invoice> InvoiceList, final boolean setLogging, final boolean supressInvalid) throws Exception {
		//Rekursive => elem can be a folder or a file 
		Files.walk(Paths.get(Path)).forEach(elem -> {
			String fileName = elem.getFileName().toString();
			String path = elem.toString();
			try{
				if (Files.isDirectory(elem)){
					if(setLogging){ System.out.println("recursive entry. elem is just a directory."); }
				}else{	if(Files.isRegularFile(elem)){
					if(Files.probeContentType(elem).intern() == "application/pdf"){
						Invoice invoice = this.readInvoice(path);
						if(invoice != null) {
							boolean add = false;
							if(supressInvalid){
								Set<ConstraintViolation<Invoice>> violations = this.getViolations(invoice);
								if((violations.size() > 0)){
									if(setLogging){ System.out.println("Violatins found in: " + fileName); }
									if(setLogging){ 
										violations.forEach(vio -> {
											System.out.println(vio.getExecutableReturnValue() + "|" + vio.getLeafBean() + "|" + vio.getInvalidValue() + "|" + vio.getMessage());
										});
									}
									add = false;
								}else{
									add = true;
								}
							}else{
								add = true;
							}
							if(add){
								if(setLogging){ System.out.println(fileName + " is valid." ); }
								InvoiceList.add(invoice);
								if(setLogging){ System.out.println( fileName + " added to the list."); }
							}
		    			}
					}else{
						if(setLogging){ System.out.println("a non pdf file was found. Ignoring..."); }
					}
				}else{
					if(setLogging){ System.out.println("elem is neither a directory nor a file. Ignoring..."); }
				}}
			}catch(TransformationException tfe){
				if(setLogging){ 
					System.out.println(fileName + " caused an Exception while transforming PDF/ZUGFERD to Java/Invoice");
					System.out.println("Message: " + tfe.getMessage());
				}
			}catch(IOException ioe){
				if(setLogging){ 
					System.out.println(fileName + " caused an IOException.");
					System.out.println("Message: " + ioe.getMessage());
				}
			}catch(Exception e){
				if(setLogging){ 
					System.out.println(fileName + " caused an Exception.");
					System.out.println("Message: " + e.getMessage());
				}
			}
		});
	}
	
	//This function will not work correct. Tt will only empty the matching Invoices and not delete them.
	public void removeInvalid(List<Invoice> InvoiceList){
		
		InvoiceList.removeIf(i -> getViolations(i).size() <= 0);
	}
	
	public boolean hasInvalid(List<Invoice> InvoiceList){
		
		for (Invoice temp : InvoiceList) {
			if(this.getViolations(temp) != null)
				return true;
		}
		return false;
	}
	
	public Set<ConstraintViolation<Invoice>> getViolations(Invoice invoice) {
		
		InvoiceValidator invoiceValidator = new InvoiceValidator();
	   
		Set<ConstraintViolation<Invoice>> violations = invoiceValidator.validate(invoice);
		
		return violations;
	}
	
	public void printViolations(List<Invoice> InvoiceList){
		
		InvoiceValidator invoiceValidator = new InvoiceValidator();
		
		Set<ConstraintViolation<Invoice>> violations;
		
		for (Invoice temp : InvoiceList) {
			violations = invoiceValidator.validate(temp);
			for (ConstraintViolation<Invoice> violation : violations) {
			     System.out.println((violation.getMessage() + " at: " + violation.getPropertyPath()));
			}
		}
	}
	
	public void printSingleInvoiceViolations(Invoice invoice){
		
		InvoiceValidator invoiceValidator = new InvoiceValidator();
		
		Set<ConstraintViolation<Invoice>> violations = invoiceValidator.validate(invoice);
		
		for (ConstraintViolation<Invoice> violation : violations) {
		     System.out.println((violation.getMessage() + " at: " + violation.getPropertyPath()));
		   }
	}
}