package genetic.function.arithmetic;

import genetic.GNode;
import genetic.function.GFunction;

public class GSquareRoot extends GFunction{

	public GSquareRoot(){
		children=new GNode[1];
	}
	
	@Override
	public double getValue() {
		return Math.sqrt(Math.abs(children[0].getValue()));
	}
	
	@Override
	public String getSymbol() {
		return "sqrt";
	}

	@Override
	public GNode copy() {
		GNode node;
		node=new GSquareRoot();
		node.size=size;
		copyChildren(node);
		return node;
	}
	
	@Override
	public GNode blank() {
		GNode node;
		node=new GSquareRoot();
		return node;
	}
}