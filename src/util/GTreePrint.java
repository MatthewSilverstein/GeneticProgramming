package util;

import genetic.GNode;
import genetic.GTree;
public class GTreePrint {
	public static String printParents(GTree tree){
		GNode node;
		String s="";
		for (int i=1;i<tree.size;i++){
			node=tree.getNode(i);
			s+=printParents(node);
		}
		return s;
	}
	
	public static String printParents(GNode node){
		String s="";
		GNode parent=null;
		s+=node+"\n";
		if (node==null)
			return s;
		parent=node.parent;
		do{
			s+=parent+"\n";
			parent=parent.parent;
		}while(parent!=null);
		return s;
	}
	
	public static String printTVS(GTree tree){
		String s="";
		s+=tree+"\n";
		s+="Value "+tree.getValue()+"\n";
		s+="Size "+tree.size+"\n";
		return s;
	}
	
	public static String printTVS(GNode node){
		String s="";
		s+=node+"\n";
		s+="Value "+node.getValue()+"\n";
		s+="Size "+node.size+"\n";
		return s;
	}
}