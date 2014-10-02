package genetic.function.arithmetic;

import genetic.GNode;
import genetic.function.GFunction;

//Safe division, div 0 = 1
public class GDivision extends GFunction{

	public GDivision(){
		children=new GNode[2];
	}
	
	@Override
	public double getValue() {
		double denominator=children[1].getValue();
		double numerator=children[0].getValue();
		if (denominator==0){
			return 1;
		}
		return numerator/denominator;
	}

	@Override
	public String getSymbol() {
		return "/";
	}
	
	@Override
	public GNode copy() {
		GNode node;
		node=new GMultiplication();
		node.size=size;
		copyChildren(node);
		return node;
	}
	
	@Override
	public GNode blank() {
		GNode node;
		node=new GAddition();
		return node;
	}
}