package genetic.function;

import genetic.GNode;


public abstract class GFunction extends GNode{

	public GFunction(){
		super();
	}
	
	protected void copyChildren(GNode node){
		for (int i=0;i<children.length;i++){
			GNode child=children[i].copy();
			child.parent=node;
			node.children[i]=child;
		}
	}
}