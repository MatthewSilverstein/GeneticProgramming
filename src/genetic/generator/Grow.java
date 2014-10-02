package genetic.generator;

import genetic.GNode;
import genetic.GTree;
import genetic.GTreeModi;
import genetic.function.GModiNode;

public class Grow extends TreeGenerator{
	double terminalFunctionRatio=0.75;
	@Override
	public GTree create(GTree tree, int initHeightMax) {
		int size=0;
		fill(tree,size,initHeightMax);
		return tree;
	}
	
	private void fill(GTree tree,int size,int initHeightMax){
		if (initHeightMax>1){
			tree.root=tree.createGFunction();
//			tree.root=GTree.createGFunction();
			tree.root.height=0;
			size=fill(tree,tree.root,2,size,initHeightMax);
			tree.size=size;
		}else{
			tree.root=tree.createGTerminal();
//			tree.root=GTree.createGTerminal();
			tree.size=1;
		}
		tree.root.size=tree.size;
	}
	
	public int fill(GTree tree,GNode node,int acc,int size,int initHeightMax){
		double rand=Math.random();
		if (rand<terminalFunctionRatio||acc>=initHeightMax){
			for (int i=0;i<node.children.length;i++){
				GNode child;
				child=tree.createGTerminal();
				child.size=1;
				child.parent=node;
				node.children[i]=child;
				size++;
			}
			return size+1;
		}
		for (int i=0;i<node.children.length;i++){
			GNode child;
			int size2;
			child=tree.createGFunction();
			child.height=acc;
			size2=size;
			size=fill(tree,child,acc+1,size,initHeightMax);
			child.size=size-size2;
			child.parent=node;
			node.children[i]=child;
		}
		return size+1;
	}
	
	public GTree create(GTreeModi tree,int initHeightMax){
		int size=0;
		fill(tree,size,initHeightMax);
		return tree;
	}
	
	private void fill(GTreeModi tree,int size,int initHeightMax){
		GNode node;
		do{
			node=tree.createGFunction();
		}while(node.getClass()!=GModiNode.class);
		tree.root=node;
		size=fill(tree,tree.root,2,size,initHeightMax);
		tree.size=size;
		tree.root.size=tree.size;
	}
}