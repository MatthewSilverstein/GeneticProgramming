package genetic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import util.IntObj;
import genetic.function.*;
import genetic.generator.Full;
import genetic.generator.Grow;
import genetic.generator.TreeGenerator;
import genetic.terminal.*;

public class GTree {
	static Full TreeGenFull;
	static Grow TreeGenGrow;
	static{
		TreeGenFull=new Full();
		TreeGenGrow=new Grow();
	}
	public GNode root;
	public int size;
	int height;
	int initHeightMax=10;
	int mutationHeightMax;
	public Set<GTerminal>terminals;
	public Set<GFunction>functions;
	
	public static enum InitType{
		GROW,FULL
	}
	
	//initialize
	
	public GTree(){
		root=null;
		terminals=new HashSet<GTerminal>();
		functions=new HashSet<GFunction>();
	}
	
	public GTree(Set<GTerminal>terminals,Set<GFunction>functions){
		this.terminals=terminals;
		this.functions=functions;
	}
	
	public GTree copy(){
		GTree copy=new GTree(terminals,functions);
		if (root==null)
			return copy;
		
		copy.root=root.copy();
		copy.update();
		return copy;
	}
	
	public void setParents(){
		if (root==null){
			return;
		}
		root.setParents();
	}
	
	public GNode getRandomNode(){
		int rand=(int)(Math.random()*size);
		IntObj randObj=new IntObj(rand);
		return root.getNode(randObj);
	}
	
	public GNode getNode(int index){
		IntObj indexObj=new IntObj(index);
		return root.getNode(indexObj);
	}
	
	public double getValue(){
		if (root==null){
			return 0;
		}
		return root.getValue();
	}
	
	public String toString(){
		if (root==null){
			return "";
		}
		return root.toString();
	}
	
	public static GTree reproduction(GTree tree){
		return tree.copy();
	}
	
	public void swap(GNode node1,GNode node2){
		GNode[] children;
		GNode parent;
		int size1,size2,dsize;
		parent=node1.parent;
		if (parent==null){
			root=node2;
		}else{
			children=parent.children;
			size1=node1.size;
			size2=node2.size;
			for (int i=0;i<children.length;i++){
				GNode child=children[i];
				if (child!=node1)
					continue;
				children[i]=node2;
			}
			
			dsize=size2-size1;
			while(parent!=null){
				parent.size+=dsize;
				parent=parent.parent;
			}
		}
	}
	
	public void update(){
		root.parent=null;
		size=root.size;
	}
	
	public GTree reproduce(){
		return copy();
	}
	
	public GTree[] crossover(GTree other){
		GTree[] children=new GTree[2];
		GNode node1,node2;
		
		node1=this.getRandomNode();
		node2=other.getRandomNode();
		if (node1==null||node2==null){
			System.out.println("YO");
		}
		this.swap(node1,node2);
		other.swap(node2, node1);
		GNode temp=node1.parent;
		node1.parent=node2.parent;
		node2.parent=temp;
		this.update();
		other.update();
		
		children[0]=this;
		children[1]=other;
		return children;
	}
	
	public void mutate(){
		//Choose node, create a new node, swap nodes
		GNode node1=getRandomNode();
		GTree tree=new GTree(terminals,functions);
		GNode node2;
		if (Math.random()<0.5){
			node2=TreeGenFull.create(tree, mutationHeightMax).root;
		}else{
			node2=TreeGenGrow.create(tree, mutationHeightMax).root;
		}
		swap(node1,node2);
		this.update();
	}
	
	public GNode createGFunction(){
		int rand=(int)(Math.random()*functions.size());
		Iterator<GFunction>iter=functions.iterator();
		GNode node;
		do{
			node=iter.next();
			rand--;
		}while(iter.hasNext()&&rand>=0);
		return node.blank();
	}
	
	public GNode createGTerminal() {
		int rand=(int)(Math.random()*terminals.size());
		Iterator<GTerminal>iter=terminals.iterator();
		GNode node;
		do{
			node=iter.next();
			rand--;
		}while(iter.hasNext()&&rand>=0);
		return node.blank();
	}

	public static void mutate(GTree tree) {
		GNode node1=tree.getRandomNode();
	}
}