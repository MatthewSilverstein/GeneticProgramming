package genetic.generator;

import genetic.GTree;
import genetic.GTreeModi;

public abstract class TreeGenerator {
	
	public abstract GTree create(GTree tree,int initHeightMax);
	
	public abstract GTree create(GTreeModi tree,int initHeightMax);
}