package genetic.function.arithmetic;

import genetic.GTree;
import genetic.GNode;
import genetic.function.GFunction;
import genetic.terminal.GNumber;

public class GAddition extends GFunction{
	public GAddition(){
		children=new GNode[2];
	}
	
	public double getValue(){
		return children[0].getValue()+children[1].getValue();
	}

	@Override
	public String getSymbol() {
		return "+";
	}

	@Override
	public GNode copy() {
		GNode node;
		node=new GAddition();
		node.size=size;
		copyChildren(node);
		return node;
	}
	
	public GNode blank(){
		GNode node;
		node=new GAddition();
		return node;
	}
}
