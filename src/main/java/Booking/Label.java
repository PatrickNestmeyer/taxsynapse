package Booking;

import java.util.ArrayList;
import java.util.List;

public class Label 
{	
	private String label_ID;
	
	private String Beleglink;
	
	private List<Integer> position_number;
	
	private List<String> description;
	
	public Label()
	{
		position_number = new ArrayList<Integer>();
		description = new ArrayList<String>();
	}
	
	public void setlabel_ID(String label_ID)
	{
		this.label_ID = label_ID;
	}
	public String getlabel_ID()
	{
		return label_ID;
	}
	public void setBeleglink(String Beleglink)
	{
		this.Beleglink = Beleglink;
	}
	public String getBeleglink()
	{
		return Beleglink;
	}
	public void addPosition(int position_number)
	{
		this.position_number.add(position_number);
	}
	public int getPosition(int i)
	{
		return position_number.get(i);
	}
	public void addDescription(String description)
	{
		this.description.add(description);
	}
	public String getDescription(int i)
	{
		return description.get(i);
	}
	public int getDescriptionSize()
	{
		return description.size();
	}
	
}
