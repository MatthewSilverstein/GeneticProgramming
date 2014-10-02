package genetic.terminal;

import genetic.GNode;

public class GRandNumber extends GTerminal{
	double lowerBound;
	double upperBound;
	double value;
	public GRandNumber(double lowerBound,double upperBound){
		this.lowerBound=lowerBound;
		this.upperBound=upperBound;
		value=lowerBound+Math.random()*(upperBound-lowerBound);
	}
	
	@Override
	public double getValue() {
		return value;
	}

	@Override
	public String getSymbol() {
		return ""+value;
	}

	@Override
	public GNode copy() {
		GRandNumber node;
		node=new GRandNumber(lowerBound,upperBound);
		node.size=1;
		node.value=value;
		return node;
	}

	@Override
	public GNode blank() {
		GNode node;
		node=new GRandNumber(lowerBound,upperBound);
		return node;
	}
}
