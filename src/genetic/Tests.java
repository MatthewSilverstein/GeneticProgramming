package genetic;

import genetic.function.GFunction;
import genetic.function.GModiNode;
import genetic.function.arithmetic.GAddition;
import genetic.function.arithmetic.GDivision;
import genetic.function.arithmetic.GMultiplication;
import genetic.function.arithmetic.GSquareRoot;
import genetic.function.arithmetic.GSubtraction;
import genetic.generator.Full;
import genetic.generator.Grow;
import genetic.generator.TreeGenerator;
import genetic.terminal.GNumber;
import genetic.terminal.GRandNumber;
import genetic.terminal.GTerminal;
import genetic.terminal.GVar;

import java.util.HashSet;
import java.util.Set;

import util.Complex;
import util.DoubleObj;
import util.GTreePrint;
import util.PolynomialComplex;

public class Tests {
	public static TreeGenerator fullGen=new Full();
	public static TreeGenerator growGen=new Grow();

	public static void test1c(){
		Complex a=new Complex(1,1);
		Complex b=new Complex(2,2);
		System.out.println("a = "+a);
		System.out.println("b = "+b);
		System.out.println("~a = "+a.conjugate());
		System.out.println(a.norm());
		System.out.println(a.divide(b));
	}

	public static void test1m(){
		int initHeightMax=3;
		DoubleObj[] outputVector=new DoubleObj[3];
		for (int i=0;i<outputVector.length;i++){
			outputVector[i]=new DoubleObj(0);
		}
		Set<GTerminal>terminals=new HashSet<GTerminal>();
		Set<GFunction>functions=new HashSet<GFunction>();
		terminals.add(new GNumber(-1));
		terminals.add(new GNumber(1));
		functions.add(new GMultiplication());
		functions.add(new GAddition());
		functions.add(new GSubtraction());
		functions.add(new GDivision());
		for (int i=0;i<outputVector.length;i++){
			functions.add(new GModiNode(outputVector[i],"o"+i));
		}
		GTree tree=new GTreeModi(terminals,functions);
		fullGen.create(tree,initHeightMax).copy();
//		growGen.create(tree, initHeightMax).copy();
		tree=tree.copy();
		GNode node;
		System.out.println(GTreePrint.printTVS(tree));
		System.out.println(GTreePrint.printParents(tree));
	}

	public static void test1aa(){
		DoubleObj var1=new DoubleObj(0,"x");
		Set<GTerminal>terminals=new HashSet<GTerminal>();
		Set<GFunction>functions=new HashSet<GFunction>();
		terminals.add(new GNumber(-1));
		terminals.add(new GNumber(1));
		terminals.add(new GVar(var1));
		functions.add(new GMultiplication());
		functions.add(new GAddition());
		GTree tree1=new GTree(terminals,functions);
		GTree tree2=new GTree(terminals,functions);
		TreeGenerator fullGen=new Full();
		fullGen.create(tree1,2);
		fullGen.create(tree2,2);
		tree1.crossover(tree2);
		System.out.println(tree1);
		System.out.println(tree2);
	}

	//Polynomial finding roots with Modi Trees
	public static void test3(){
		Complex []a={new Complex(1,0),new Complex(2,0),new Complex(1,0)};
		int populationSize=500;
		int initHeightMax=4;
		int genMax=50;
		int maxAttempt=1;
		int runs=0;
		double mutationRate=0.15;
		double pReproduce=0.1;
		double pCrossover=0.9;
		boolean stop=false;
		DoubleObj[] outputVector=new DoubleObj[a.length*2];
		GTree best=null;
		double bestFitness=Double.MAX_VALUE;
		for (int i=0;i<outputVector.length;i++){
			outputVector[i]=new DoubleObj(0);
		}
		PolynomialComplex f=new PolynomialComplex(a);
		Set<GTerminal>terminals=new HashSet<GTerminal>();
		Set<GFunction>functions=new HashSet<GFunction>();
		double[] popRawFitness=new double[populationSize];
		double[] popStandardFitness=new double[populationSize];
		double[] popAdjustedFitness=new double[populationSize];
		double[] popNormalizedFitness=new double[populationSize];
		double[] popFitness=new double[populationSize];
		terminals.add(new GNumber(-1));
		terminals.add(new GNumber(1));
		terminals.add(new GRandNumber(0,10));
		functions.add(new GMultiplication());
		functions.add(new GAddition());
		functions.add(new GSubtraction());
		functions.add(new GDivision());
		functions.add(new GSquareRoot());
		for (int i=0;i<outputVector.length;i++){
			functions.add(new GModiNode(outputVector[i],"o"+i));
		}

		//Create initial Population
		GTree[] population=new GTree[populationSize];
		for (int attempt = 0; attempt < maxAttempt; attempt++){
//		while(bestFitness>=0.0001){
			runs++;
			System.out.println(runs);
			for (int i=0;i<populationSize/2;i++){
				GTree tree=new GTreeModi(terminals,functions);
				fullGen.create(tree,initHeightMax);
				tree.update();
				population[i]=tree;
			}
			for (int i=populationSize/2;i<populationSize;i++){
				GTree tree=new GTreeModi(terminals,functions);
				growGen.create(tree,initHeightMax);
				tree.update();
				population[i]=tree;			
			}

			if (stop)
				return;
			//Run Generations
			for (int gen=0;gen<genMax;gen++){
				//Get Raw,Standard,Adjusted Fitness
				for (int i=0;i<population.length;i++){
					GTree tree;
					double rawFitness;
					double standardFitness;
					double adjustedFitness;
					tree=population[i];
					rawFitness=Main.getFitness(tree,f,outputVector);
					if (Double.isNaN(rawFitness)){
						Main.getFitness(tree,f,outputVector);
					}
					standardFitness=rawFitness;
					adjustedFitness=1/(1+standardFitness);
					popRawFitness[i]=rawFitness;
					popStandardFitness[i]=standardFitness;
					popAdjustedFitness[i]=adjustedFitness;
					if (rawFitness<bestFitness){
						bestFitness=rawFitness;
						best=population[i].copy();
						int b=1;
					}
				}
				
				//Get Normalized Fitness
				double sumAdjustedFitness;
				sumAdjustedFitness=Main.sum(popAdjustedFitness);				
				for (int i=0;i<populationSize;i++){
					double normalizedFitness;
					double adjustedFitness;
					
					adjustedFitness=popAdjustedFitness[i];
					normalizedFitness=adjustedFitness/sumAdjustedFitness;
					popNormalizedFitness[i]=normalizedFitness;
				}
				
				for (int i=0;i<populationSize;i++){
					popFitness[i]=popNormalizedFitness[i];
				}
				
				//Create next generation
				GTree[] newPopulation=new GTree[populationSize];
				int numNewPop=0;
								
				int crossoverSize=(int)(populationSize*pCrossover);
				int reproductionSize=populationSize-crossoverSize;
				if (crossoverSize%2==1){
					crossoverSize--;
				}
				
				//C
				for (int i=0;i<crossoverSize;i+=2,numNewPop+=2){
					GTree tree1=null,tree2=null;
					tree1=Main.chooseOne(population,popFitness).copy();
					tree2=Main.chooseOne(population,popFitness).copy();
					tree1.crossover(tree2);
					newPopulation[numNewPop]=tree1;
					newPopulation[numNewPop+1]=tree2;
				}
				
				//R
				for (int i=0;i<reproductionSize;i++,numNewPop++){
					GTree tree=null;
					GTree newTree;
					tree=Main.chooseOne(population,popFitness);
					tree.reproduce();
					newTree=tree;
					newPopulation[numNewPop]=newTree;
				}
				
				//Mutation
				for (int i=0;i<populationSize;i++){
					double rand=Math.random();
					if (rand<mutationRate){
						GTree tree=newPopulation[i];
						tree.mutate();
					}
				}
				population=newPopulation;
			}
			int b=1;
		}
		//get best
		//Random 20 tests
		double fitness=0;
		fitness=Main.getFitness(best,f,outputVector);
		System.out.println(best);
		System.out.println("FITNESS "+fitness);
		for (int i=0;i<outputVector.length;i++){
			System.out.println("OV "+i+", "+outputVector[i].value);			
		}
		System.out.println(runs);
	}

}
