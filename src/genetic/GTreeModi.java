package genetic;

import java.util.Set;

import genetic.function.GFunction;
import genetic.function.GModiNode;
import genetic.terminal.GTerminal;

public class GTreeModi extends GTree{
	
	public GTreeModi(Set<GTerminal> terminals, Set<GFunction> functions) {
		super(terminals,functions);
	}
	
	public GTree copy(){
		GTree copy=new GTreeModi(terminals,functions);
		if (root==null)
			return copy;
		
		copy.root=root.copy();
		copy.update();
		return copy;
	}
	
	@Override
	public void update(){
		if (root.getClass()!=GModiNode.class){
			if (Math.random()<0.5){
				TreeGenFull.create(this, mutationHeightMax);
			}else{
				TreeGenGrow.create(this, mutationHeightMax);
			}
		}
		super.update();
	}
}