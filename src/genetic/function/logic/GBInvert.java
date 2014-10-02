package genetic.function.logic;
import genetic.GNode;
import genetic.function.GFunction;
public class GBInvert extends GFunction{
	
	public GBInvert(){
		children=new GNode[1];
	}
	
	@Override
	public double getValue() {
		double child0Value=children[0].getValue();
		return Double.longBitsToDouble(~Double.doubleToRawLongBits(child0Value));
	}

	@Override
	public String getSymbol() {
		return "~";
	}
	
	@Override
	public GNode copy() {
		GNode node;
		node=new GBInvert();
		node.size=size;
		copyChildren(node);
		return node;
	}

	@Override
	public GNode blank() {
		GNode node;
		node=new GBInvert();
		return node;
	}
}
