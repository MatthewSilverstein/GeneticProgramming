package examples.sudoku;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



public class SudokuSolver {
	
	public static final String NEWLINE="\n";
	public static String output="output";
	public static String output2="output2";
	public static BufferedWriter out,out2;
	public static final boolean DEBUG=false;
	
	public static void main(String[]args){
		String sudoku=	"013400000"+
				"000050007"+
				"002008605"+
				"007000002"+
				"050040010"+
				"800000900"+
				"406300800"+
				"200080000"+
				"000007360";
		System.out.println(solveSudoku(sudoku));
	}
	
	public static String solveSudoku(String s){
		Node[][] sudoku=createSudoku(s);
		ArrayList<Node> result=solveSudoku(sudoku);
		int[]symbols=new int[81];
		for (int i=0;i<result.size();i++){
			symbols[i]=((result.get(i).symbol.value));
		}
		Arrays.sort(symbols);
		String solved="";
		for(int i=0;i<9;i++){
			for (int j=0;j<9;j++){
				solved+=((symbols[i*9+j]%9+1)+"\t");
			}
			solved+="\n";
		}
		return solved;
	}
	
	
	/************************************************************** SOLVE SUDOKU and subparts ******************************************************/
	//TODO SOLVE SUDOKU
	public static ArrayList<Node> solveSudoku(Node[][] nodes){
		Node[] sums=new Node[nodes.length];
		for (int i=0;i<nodes.length;i++){
			int sum=0;
			for (int j=0;j<nodes[i].length;j++){
				sum+=nodes[i][j].value;
			}
			
			sums[i]=new Node(sum);
			for (int j=0;j<nodes[i].length;j++){
				nodes[i][j].sum=sums[i];
			}
		}
		connectNodes(sums);
		return solveSudokuR(nodes,nodes[0][0],729,324,NEWLINE);
	}
	
	public static ArrayList<Node> solveSudokuR(Node[][] nodes,Node node,int rows,int cols,String indent){
		node=findConnectedNode(nodes);
		
		if (node==null){
			return new ArrayList<Node>();
		}
		if (rows==0||cols==0){
			return new ArrayList<Node>();
		}
		Node minSum=node.sum;
		Node nodeSum=node.sum;
		Node minNode=node;
		Node nodee=node;
		do{
			node=node.right;
			nodeSum=node.sum;
			if (nodeSum.value<minSum.value){
				minSum=nodeSum;
				minNode=node;
			}
		}while(!nodee.equals(node));
		
		if (minSum.value==0){
			return null;
		}
		
		//Now loop through minCol
		node=minNode;
		do{
			node=node.down;
			if (node.value==0){
				continue;
			}
			Node node2=node;
			ArrayList<Node> removedNodes=new ArrayList<Node>();
			Map<Node,Character> removedMap=new HashMap<Node,Character>();
			do{	//Goes through all constraints for this symbol and finds any matches
				node2=node2.right;
				if (node2.value==1){
					Node node3=node2.down;
					while(!node3.equals(node2)){	//Goes through all symbols for this constraint except for chosen symbol, if match, removes symbol/row
						if (node3.value==1){
							Node node4=node3;
							removeRow(node3,rows,cols);
							rows--;
							removedNodes.add(node3);
							removedMap.put(node3, 'r');
						}
						node3=node3.down;
					}
					//Now remove constraint/column
					node3=node2;
					while(!node3.connected){
						node3=node3.down;
					}
					removeColumn(node3,rows,cols);
					removedNodes.add(0,node3);
					removedMap.put(node3, 'c');
					cols--;
				}
			}while(!node2.equals(node));
			node2=node;
			ArrayList<Node>list=new ArrayList<Node>();
			
			//Try solving like this
			ArrayList<Node> result=solveSudokuR(nodes,node, rows, cols,indent+"\t");
			if (result!=null){
				result.add(node);
				return result;
			}
			//Otherwise undo everything
			for(int j=0;!removedNodes.isEmpty();j++){
				node2=removedNodes.get(0);
				Character type=removedMap.get(node2);
				switch(type){
				case 'r':
					addRow(node2,rows,cols);
					rows++;
					break;
				case 'c':
					addColumn(node2,rows,cols);
					cols++;
					break;
				}
				
				removedNodes.remove(0);
			}
		} while(!node.equals(minNode));
		return null;
	}
	
	public static void nodesToString(Node[][] nodes,BufferedWriter out) throws IOException{
		String s=""+NEWLINE;
		Node node=findConnectedNode(nodes);
		Node node2=node;
		s+=node.symbol.value+","+node.constraint.value+NEWLINE;
		out.write(s);
		s="";
		do{
			node2=node2.right;
			if (node2.connected)
				s+="\t"+node2.constraint.value;
		} while(!node2.equals(node));
		s+=NEWLINE;
		out.write(s);
		s="";
		do{
			node2=node2.down;
			if (node2.connected)
				s+="\t"+node2.symbol.value;
		} while(!node2.equals(node));
		s+=NEWLINE;
		out.write(s);
		s="";
		do{
			node2=node2.right;
			Node node3=node2;
			do{
				node3=node3.down;
				s+="\t"+node3.symbol.value;
			} while(!node3.equals(node2));
			s+=NEWLINE;
			out.write(s);
			s="";
		} while(!node2.equals(node));
		s+=NEWLINE;
		out.write(s);
		out.flush();
	}
	
	public static Node findConnectedNode(Node[][] nodes){
		Node node=null;
		for (int i=0;i<nodes.length;i++){
			for (int j=0;j<nodes[i].length;j++){
				node=nodes[i][j];
				if (node.connected){
					return node;
				}
			}
		}
		return null;
	}

	
	public static void addRow(Node node,int rows,int cols){
		Node node2=node;
		do{
			node2=node2.right;
			if (node2.value==1){
				node2.sum.value++;
			}
			node2.up.down=node2;
			node2.down.up=node2;
			node2.connected=true;
		} while(!node2.equals(node));
	}
	
	public static void removeRow(Node node,int rows,int cols){
		Node node2=node;
		do{
			node2=node2.right;
			if (node2.value==1){
				node2.sum.value--;
			}
			node2.up.down=node2.down;
			node2.down.up=node2.up;
			node2.connected=false;
		} while(!node2.equals(node));
	}
	
	public static void addColumn(Node node,int rows,int cols){
		Node node2=node;
		do{
			node2=node2.down;
			node2.left.right=node2;
			node2.right.left=node2;
			node2.connected=true;
		} while(!node2.equals(node));
	}
	
	public static void removeColumn(Node node,int rows,int cols){
		Node node2=node;
		do{
			node2=node2.down;
			node2.left.right=node2.right;
			node2.right.left=node2.left;
			node2.connected=false;
		} while(!node2.equals(node));
	}

	
	/**************************************************************** CREATE SUDOKU and sub parts **************************************************/
	//TODO CREATE SUDOKU
	public static Node[][] createSudoku(){
		Node[][] nodes=new Node[324][729];
		initNodes(nodes);
		setRC(nodes);
		setR(nodes);
		setC(nodes);
		setB(nodes);
		connectNodes(nodes);
		setSymbols(nodes);
		setConstraints(nodes);
		return nodes;
	}
	
	public static Node[][] createSudoku(String s){
		Node[][] nodes=new Node[324][729];
		initNodes(nodes);
		setRC(nodes);
		setR(nodes);
		setC(nodes);
		setB(nodes);
		connectNodes(nodes);
		setSymbols(nodes);
		setConstraints(nodes);
		reduceSudoku(nodes,s);
		return nodes;
	}
	
	public static void initNodes(Node[][] nodes){
		for (int i=0;i<nodes.length;i++){
			for (int j=0;j<nodes[i].length;j++){
				nodes[i][j]=new Node(0);
			}
		}
	}
	
	public static void setRC(Node[][] nodes){
		for (int r=0;r<9;r++){
			for (int c=0;c<9;c++){
				int index1=r*9+c;
				for (int n=0;n<9;n++){
					int index2=r*81+c*9+n;
					nodes[index1][index2]=new Node(1);
				}
			}
		}
	}
	
	public static void setR(Node[][] nodes){
		for (int r=0;r<9;r++){
			for (int n=0;n<9;n++){
				int index1=r*9+n+81;
				for (int c=0;c<9;c++){
					int index2=r*81+c*9+n;
					nodes[index1][index2]=new Node(1);
				}
			}
		}
	}
	
	public static void setC(Node[][] nodes){
		for (int c=0;c<9;c++){
			for (int n=0;n<9;n++){
				int index1=c*9+n+81*2;
				for (int r=0;r<9;r++){
					int index2=r*81+c*9+n;
					nodes[index1][index2]=new Node(1);
				}
			}
		}
	}
	
	public static void setB(Node[][] nodes){
		for (int br=0;br<3;br++){
			for (int bc=0;bc<3;bc++){
				for (int n=0;n<9;n++){
					int index1=(br*3+bc)*9+n+81*3;
					for (int r=0;r<3;r++){
						for (int c=0;c<3;c++){
							int index2=(r+br*3)*81+(c+bc*3)*9+n;
							nodes[index1][index2]=new Node(1);
						}
					}
				}
			}
		}
	}
	
	public static void connectNodes(Node[][] nodes){
		for (int i=0;i<nodes.length;i++){
			for (int j=0;j<nodes[i].length;j++){
				Node node=nodes[i][j];
				int[] left=new int[2];
				int[] right=new int[2];
				int[] up=new int[2];
				int[] down=new int[2];
				left[0]=(nodes.length+i-1)%nodes.length;
				left[1]=j;
				right[0]=(nodes.length+i+1)%nodes.length;
				right[1]=j;
				up[0]=i;
				up[1]=(nodes[i].length+j+1)%nodes[i].length;
				down[0]=i;
				down[1]=(nodes[i].length+j-1)%nodes[i].length;
				node.leftIndex=left;
				node.rightIndex=right;
				node.upIndex=up;
				node.downIndex=down;
				node.left=nodes[left[0]][left[1]];
				node.right=nodes[right[0]][right[1]];
				node.up=nodes[up[0]][up[1]];
				node.down=nodes[down[0]][down[1]];
				node.connected=true;
			}
		}
	}
	
	public static void connectNodes(Node[] nodes){
		for (int i=0;i<nodes.length;i++){
			Node node=nodes[i];
			int[] left=new int[1];
			int[] right=new int[1];
			left[0]=(nodes.length+i-1)%nodes.length;
			right[0]=(nodes.length+i+1)%nodes.length;
			node.leftIndex=left;
			node.rightIndex=right;
			node.left=nodes[left[0]];
			node.right=nodes[right[0]];
		}
	}
	
	public static void setSymbols(Node[][] nodes){
		Node[] symbols=new Node[nodes[0].length];
		for (int i=0;i<symbols.length;i++){
			symbols[i]=new Node(i);
		}
		for (int i=0;i<nodes.length;i++){
			for (int j=0;j<nodes[i].length;j++){
				nodes[i][j].symbol=symbols[j];
			}
		}
	}
	
	public static void setConstraints(Node[][] nodes){
		Node[] constraints=new Node[nodes.length];
		for (int i=0;i<constraints.length;i++){
			constraints[i]=new Node(i);
		}
		for (int i=0;i<nodes.length;i++){
			for (int j=0;j<nodes[i].length;j++){
				nodes[i][j].constraint=constraints[i];
			}
		}
	}
	
	/********************************************************* REDUCE SUDOKU and sub parts ***********************************************/
	//TODO REDUCE SUDOKU
	public static void reduceSudoku(Node[][] nodes,String s){
		for (int i=0;i<s.length();i++){
			int symbol=Character.getNumericValue(s.charAt(i));
			if (symbol==0){
				continue;
			}
			symbol--;
			removeRC(nodes,i,symbol);
			removeR(nodes,i,symbol);
			removeC(nodes,i,symbol);
			removeB(nodes,i,symbol);
			addRC(nodes,i,symbol);
			addR(nodes,i,symbol);
			addC(nodes,i,symbol);
			addB(nodes,i,symbol);
		}
	}
	
	public static void removeRC(Node[][] nodes,int index,int symbol){
		int index2=index;
		for (int i=0;i<nodes[index2].length;i++){
			nodes[index2][i].value=0;
		}
	}
	
	public static void removeR(Node[][] nodes,int index,int symbol){
		int row=index/9;
		int index2=row*9+symbol+81;
		for (int i=0;i<nodes[index2].length;i++){
			nodes[index2][i].value=0;
		}
	}
	
	public static void removeC(Node[][] nodes,int index,int symbol){
		int col=index%9;
		int index2=col*9+symbol+81*2;
		for (int i=0;i<nodes[index2].length;i++){
			nodes[index2][i].value=0;
		}
	}

	public static void removeB(Node[][] nodes,int index,int symbol){
		int boxX=(index/9)/3;
		int boxY=((index%9)/3)%3;
		int index2=(boxX*3+boxY)*9+symbol+81*3;
		for (int i=0;i<nodes[index2].length;i++){
			nodes[index][i].value=0;
		}
	}
	
	public static void addRC(Node[][] nodes,int index,int symbol){
		int index2=index;
		int index3=index*9+symbol;
		nodes[index2][index3].value=1;
	}

	public static void addR(Node[][] nodes,int index,int symbol){
		int row=index/9;
		int col=index%9;
		int index2=row*9+symbol+81;
		int index3=row*81+col*9+symbol;
		nodes[index2][index3].value=1;
	}
	
	public static void addC(Node[][] nodes,int index,int symbol){
		int row=index/9;
		int col=index%9;
		int index2=col*9+symbol+81*2;
		int index3=row*81+col*9+symbol;
		nodes[index2][index3].value=1;
	}
	
	public static void addB(Node[][] nodes,int index,int symbol){
		int boxX=(index/9)/3;
		int boxY=((index%9)/3)%3;
		int index2=(boxX*3+boxY)*9+symbol+81*3;
		int row=index/9;
		int col=index%9;
		int index3=row*81+col*9+symbol;
		nodes[index2][index3].value=1;
	}
	
	/************************************************************ MISC *************************************************/
	//TODO MISC
	public static void printConnectivity(Node node)throws IOException{
		Node node2=node;
		String s="";
		
		s="";
		do{
			Node node3=node2;
			s+="\t"+node3.symbol.value;
			do{
				s+="\t"+node3.constraint.value;
				node3=node3.right;
			}while(!node2.equals(node3));
			s+=NEWLINE;
			out.write(s);
			s="";
			node2=node2.down;
		}while(!node.equals(node2));
		removeRow(node,0,0);
		out.write(s);
		out.flush();
		s="";
		do{
			Node node3=node2;
			s+="\t"+node3.symbol.value;
			do{
				s+="\t"+node3.constraint.value;
				node3=node3.right;
			}while(!node2.equals(node3));
			s+=NEWLINE;
			out.write(s);
			s="";
			node2=node2.down;
		}while(!node.equals(node2));
		out.write(s);
		out.flush();
		s="";
	}
	
	public static void test(Node node,Node[][] nodes)throws IOException{
		//Try removing some rows and print total connectivity
		int count=20;
		while(count-->0){
			node=node.down;
		}
		removeRow(node,0,0);
		node=node.down;
		removeRow(node,0,0);
		node=findConnectedNode(nodes);
		System.out.println(node.constraint.value);
		removeColumn(node,0,0);
		node=findConnectedNode(nodes);
		Node node2=node;
		do{
			node2=node2.right;
			Node node3=node2;
			String s="";
			s+="\t"+node3.constraint.value;
			out.write(s);
			do{
				s="";
				node3=node3.down;
				s+="\t"+node3.symbol.value;
				out.write(s);
			} while(!node3.equals(node2));
			s+=NEWLINE;
			out.write(s);
		} while(!node2.equals(node));
		
	}
	
	/************************************************************ NODE **************************************************/
	//TODO NODE
	public static class Node{
		int[] index=null;
		int value;
		Node constraint=null;
		Node sum=null;
		Node symbol=null;
		int[] leftIndex=null,rightIndex=null,upIndex=null,downIndex=null;
		Node left=null,right=null,up=null,down=null;
		boolean connected=false;
		public Node(int value){
			this.value=value;
		}
	}
}