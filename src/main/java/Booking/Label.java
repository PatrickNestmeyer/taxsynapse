package Booking;

import java.util.ArrayList;
import java.util.List;

public class Label 
{	
	public String label_ID;
	
	public String Beleglink;
	
	public List<Integer> position_number;
	
	public List<String> description;
	
	public Label()
	{
		position_number = new ArrayList<Integer>();
		description = new ArrayList<String>();
	}
	
}
