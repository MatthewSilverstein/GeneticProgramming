package genetic.function.logic;

import genetic.GNode;
import genetic.function.GFunction;
import genetic.function.arithmetic.GMultiplication;

public class GBAnd extends GFunction{

	public GBAnd(){
		children=new GNode[2];
	}
	
	@Override
	public double getValue() {
		double child0Value=children[0].getValue();
		double child1Value=children[1].getValue();
		return Double.longBitsToDouble(Double.doubleToRawLongBits(child0Value) & Double.doubleToRawLongBits(child1Value));
	}
	
	@Override
	public String getSymbol() {
		return "&";
	}
	
	@Override
	public GNode copy() {
		GNode node;
		node=new GBAnd();
		node.size=size;
		copyChildren(node);
		return node;
	}

	@Override
	public GNode blank() {
		GNode node;
		node=new GBAnd();
		return node;
	}
}
