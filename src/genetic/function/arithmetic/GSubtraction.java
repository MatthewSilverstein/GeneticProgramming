package genetic.function.arithmetic;

import genetic.GNode;
import genetic.function.GFunction;

public class GSubtraction extends GFunction{

	public GSubtraction(){
		children=new GNode[2];
	}
	
	@Override
	public double getValue() {
		return children[0].getValue()-children[1].getValue();
	}
	
	@Override
	public String getSymbol() {
		return "-";
	}

	@Override
	public GNode copy() {
		GNode node;
		node=new GSubtraction();
		node.size=size;
		copyChildren(node);
		return node;
	}


	@Override
	public GNode blank() {
		GNode node;
		node=new GSubtraction();
		return node;
	}
}
