package genetic.terminal;

import genetic.GNode;

public class GNumber extends GTerminal{
	Double number;
	public GNumber(double number){
		super();
		this.number=number;
	}
	@Override
	public double getValue() {
		return number;
	}
	
	@Override
	public String getSymbol() {
		return ""+number;
	}
	@Override
	public GNode copy() {
		GNumber node;
		node=new GNumber(number);
		node.size=1;
		return node;
	}
	
	public GNode blank(){
		GNode node;
		node=new GNumber(number);
		return node;
	}
}
