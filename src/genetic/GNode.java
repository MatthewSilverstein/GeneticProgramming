package genetic;

import util.IntObj;
import genetic.function.GFunction;
import genetic.function.arithmetic.GAddition;
import genetic.function.arithmetic.GMultiplication;
import genetic.terminal.GNumber;
import genetic.terminal.GTerminal;

public abstract class GNode{
	public GNode[] children;
	public GNode parent;
	public int height;
	public int size;
	
	public GNode(){
		children=new GNode[0];
		parent=null;
	}
	
	public void setParents(){
		for (int i=0;i<children.length;i++){
			GNode child=children[i];
			child.parent=this;
			child.setParents();
			children[i]=child;
		}
	}
	
	public GNode getNode(IntObj randObj){
		GNode node;
		int rand=randObj.value;
//		System.out.println("Rand = "+rand);
		if (rand==0){
			node=this;
			return node;
		}
		rand--;
		randObj.value=rand;
		for (int i=0;i<children.length;i++){
			GNode child;
			child=children[i];
			node=child.getNode(randObj);
			if (node!=null){
				return node;
			}
		}
		
		return null;
	}	
	
	public GNode createGNode(){
		double rand=Math.random();
		GNode node;
		if (rand<0.5){
			node=this.createGTerminal();
		}else{
			node=this.createGFunction();
		}
		return node;
	}
	
	public GTerminal createGTerminal(){
		int rand=(int)(Math.random()*2);
		GTerminal node=null;
		switch(rand){
		case 0:
			node=new GNumber(1);
			break;
		case 1:
			node=new GNumber(-1);
			break;
		}
		return node;
	}
	
	public GFunction createGFunction(){
		int rand=(int)(Math.random()*2);
		GFunction node=null;
		switch(rand){
		case 0:
			node=new GAddition();
			break;
		case 1:
			node=new GMultiplication();
			break;
		}
		return node;
	}
	
	public String toString(){
		String s="";
		s+=getSymbol();
		if (children.length==0){
			return s;
		}
		s+=" (";
		for (int i=0;i<children.length;i++){
			s+=" "+children[i].toString();
		}
		s+=")";
		return s;
	}
	
	public abstract double getValue();
	
	public abstract String getSymbol();
	
	public abstract GNode copy();
	
	public abstract GNode blank();
}