package genetic.function.arithmetic;

import genetic.GNode;
import genetic.function.GFunction;

public class GMultiplication extends GFunction{

	public GMultiplication(){
		children=new GNode[2];
	}
	
	@Override
	public double getValue() {
		return children[0].getValue()*children[1].getValue();
	}
	
	@Override
	public String getSymbol() {
		return "*";
	}

	@Override
	public GNode copy() {
		GNode node;
		node=new GMultiplication();
		node.size=size;
		copyChildren(node);
		return node;
	}
	
	public GNode blank(){
		GNode node;
		node=new GMultiplication();
		return node;
	}
}
