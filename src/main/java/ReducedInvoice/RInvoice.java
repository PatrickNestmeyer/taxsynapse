package ReducedInvoice;

import java.util.List;
import java.util.*;

public class RInvoice extends AInvoice{

	
	public RInvoice(){
		Positions = new ArrayList<Position>();
	}
	
	@Override
	public void addPosition(Position p) {
		
		Positions.add(p);
	}

	@Override
	public void addPosition(String desc, Price p, String mm, int amount, int tax) {
		
		Positions.add(new Position(desc, p, mm, amount, tax));
	}

	@Override
	public Position getPosition(int index) {
		
		if(Positions.size() < index)
			return null;
		else
			return Positions.get(index);				
	}

	@Override
	public List<Position> getPositions() {
		
		return Positions;
	}

	@Override
	public void setPositionDescription(String description, int index) {
		
		Positions.get(index).setDescription(description);
	}

	@Override
	public void setPositionPrice(Price price, int index) {
		
		Positions.get(index).setPositionPrice(price);
	}

	@Override
	public void setPositionAmount(int amount, int index) {
		
		Positions.get(index).setAmount(amount);
	}

	@Override
	public void setPositionTaxRate(int taxrate, int index) {

		Positions.get(index).setTaxrate(taxrate);
	}

	@Override
	public int getPositionsLength() {
		
		return Positions.size();
	}

	@Override
	public void setPosition(Position position, int index) {
		
		Positions.get(index).setDescription(position.getDescription());
		Positions.get(index).setPositionPrice(position.getPositionPrice());
		Positions.get(index).setAmount(position.getAmount());
		Positions.get(index).setTaxrate(position.getTaxrate());
	}
}
