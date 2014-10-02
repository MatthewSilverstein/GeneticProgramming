package genetic.terminal;

import util.DoubleObj;
import genetic.GNode;

public class GVar extends GTerminal{
	DoubleObj number;
	
	public GVar(DoubleObj number){
		this.number=number;
	}
	
	public GNode copy() {
		GNode node;
		node=new GVar(number);
		node.size=1;
		return node;
	}

	@Override
	public double getValue() {
		return number.value;
	}

	@Override
	public String getSymbol() {
		return ""+number.symbol;
	}
	
	public GNode blank(){
		GNode node;
		node=new GVar(number);
		return node;
	}

}
