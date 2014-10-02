package genetic.function;

import util.DoubleObj;
import genetic.GNode;

public class GModiNode extends GFunction{
	DoubleObj outputVector;
	String symbol;
	
	public GModiNode(DoubleObj outputVector,String symbol){
		this.outputVector=outputVector;
		children=new GNode[2];
		this.symbol=symbol;
	}
	
	@Override
	public double getValue() {
		outputVector.value+=children[0].getValue();
		return children[1].getValue();
	}
	
	@Override
	public String getSymbol() {
		return symbol;
	}
	
	@Override
	public GNode copy() {
		GNode node;
		node=new GModiNode(outputVector,symbol);
		node.size=size;
		copyChildren(node);
		return node;
	}
	
	@Override
	public GNode blank() {
		GNode node;
		node=new GModiNode(outputVector,symbol);
		return node;
	}
}