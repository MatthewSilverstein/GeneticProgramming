package genetic.function.logic;

import genetic.GNode;
import genetic.function.GFunction;

public class GBXor extends GFunction{

	public GBXor(){
		children=new GNode[2];
	}
	
	@Override
	public double getValue() {
		double child0Value=children[0].getValue();
		double child1Value=children[1].getValue();
		return Double.longBitsToDouble(Double.doubleToRawLongBits(child0Value) ^ Double.doubleToRawLongBits(child1Value));
	}

	@Override
	public String getSymbol() {
		return "^";
	}

	@Override
	public GNode copy() {
		GNode node;
		node=new GBXor();
		node.size=size;
		copyChildren(node);
		return node;
	}

	@Override
	public GNode blank() {
		GNode node;
		node=new GBXor();
		return node;
	}
}
