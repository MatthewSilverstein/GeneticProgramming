package genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import util.Complex;
import util.DoubleObj;
import util.GTreePrint;
import util.InputHandler;
import util.Polynomial;
import util.PolynomialComplex;
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

public class Main {

	public static TreeGenerator fullGen=new Full();
	public static TreeGenerator growGen=new Grow();

	public static void main(String[] args) {
		factorPolynomial();
	}

	public static void helpPolynomialInput(){
		System.out.println("Enter the coefficients of the polynomial from lowest order to highest.");
		System.out.println("Example: 1 + 2*x + x^2 --> 1 2 1");
	}

	public static void helpCPolynomialInput(){
		System.out.println("Enter the coefficients of the polynomial from lowest order to highest.");
		System.out.println("Example: 1 + 2*i + (2 - i)*x + x^2 --> 1,2 2,-1 1,0");		
	}

	public static void factorPolynomial(){
		/*
		 * This function asks for a polynomial input and then attempts to find
		 * a solution using the genetic programming library.
		 * Overview: Using a ModiTree (outputs a vector rather than a single value),
		 * the algorithm tries to find a solution to the algorithm in this form:
		 * Given p=x^4 + 4*x^3 + 6*x^2 + 4*x + 1
		 * ---> a0*(x^2 + a1*x + a2)(x^2 + a3*x + a4)
		 * It finds quadratic roots (and one linear root if the polynomial is odd).
		 */
		System.out.println("Enter a polynomial to be factored, for more information on syntax see helpPolynomialInput()");
		Polynomial f=InputHandler.getPolynomial();
		if (f.getDegree()<0)
			return;
		int quadraticFactors;
		int linearFactors;
		if (f.getDegree()%2==0){
			quadraticFactors=f.getDegree()/2;
			linearFactors=0;
		}else{
			linearFactors=1;
			quadraticFactors=(f.getDegree()-1)/2;
		}
		int populationSize=500;
		int initHeightMax=4;
		int genMax=75;
		int maxRuns=5;
		int runs=0;
		double mutationRate=0.01;
		double pReproduce=0.1;
		double pCrossover=0.9;
		boolean stop=false;
		DoubleObj[] outputVector=new DoubleObj[1 + linearFactors + 2 * quadraticFactors];
		GTree best=null;
		double bestFitness=Double.MAX_VALUE;
		double endingThreshold = 0;
		for (int i=0;i<outputVector.length;i++){
			outputVector[i]=new DoubleObj(0);
		}
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
		for (; runs < maxRuns; runs++){
			if (bestFitness <= endingThreshold){
				break;
			}
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

			//Run Generations
			for (int gen=0;gen<genMax;gen++){
				//Get Raw,Standard,Adjusted Fitness
				for (int i=0;i<population.length;i++){
					GTree tree;
					double rawFitness;
					double standardFitness;
					double adjustedFitness;
					tree=population[i];
					rawFitness=Main.getFitness(tree,f,outputVector,linearFactors,quadraticFactors);
					if (Double.isNaN(rawFitness)){
						Main.getFitness(tree,f,outputVector,linearFactors,quadraticFactors);
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
		System.out.println("Best Genetic Program:");
		fitness=Main.getFitness(best,f,outputVector,linearFactors,quadraticFactors);
		System.out.println(best);
		String polynomial = "";
		polynomial += "( " + outputVector[0].value + " + " + outputVector[1].value + "*i )";
		for (int i=2;i<outputVector.length;i+=2){
			polynomial += "( x - " + outputVector[i].value + " - " + outputVector[i+1].value + "*i )";
		}
		System.out.println("Polynomial: " + polynomial);
		System.out.println("Fitness: "+fitness);
		System.out.println("Runs: "+runs);
	}
	
	public static void normalSelection(double[]popNormalized,double[]popFitness,GTree[] population,GTree[] newPopulation,double pCrossover){
		int numNewPop=0;
		int populationSize=population.length;
		int crossoverSize=(int)(populationSize*pCrossover);
		int reproductionSize=populationSize-crossoverSize;
		if (crossoverSize%2==1){
			crossoverSize--;
		}
		
		//C
		for (int i=0;i<crossoverSize;i+=2,numNewPop+=2){
			GTree tree1=null,tree2=null;
			tree1=chooseOne(population,popFitness).copy();
			tree2=chooseOne(population,popFitness).copy();
			tree1.crossover(tree2);
			newPopulation[numNewPop]=tree1;
			newPopulation[numNewPop+1]=tree2;
		}
		
		//R
		for (int i=0;i<reproductionSize;i++,numNewPop++){
			GTree tree=null;
			GTree newTree;
			tree=chooseOne(population,popFitness);
			tree.reproduce();
			newTree=tree;
			newPopulation[numNewPop]=newTree;
		}	
	}
	
	public static void overSelectionGreedy(double[]popNormalizedFitness,double[] popFitness,GTree[] population,GTree[] newPopulation,double pCrossover){
		//Get Fitness, useful for overselection, lets try 80 20
		int populationSize=popNormalizedFitness.length;
		int numNewPop=0;
		double[] betterFitness,worseFitness;
		GTree[] betterPop,worsePop;
		Map<Double,ArrayList<Integer>>map=new HashMap<Double,ArrayList<Integer>>();
		for (int i=0;i<populationSize;i++){
			ArrayList<Integer>set=map.get(popNormalizedFitness[i]);
			if (set==null){
				set=new ArrayList<Integer>();
				set.add(i);
				map.put(popNormalizedFitness[i], set);
			}else{
				set.add(i);
				map.put(popNormalizedFitness[i], set);
			}
		}
		Arrays.sort(popNormalizedFitness);
		double sum=0;
		int index=populationSize-1;
		for (int i=populationSize-1;i>=0&&sum<0.2;i--,index--){
			sum+=popNormalizedFitness[i];
		}
		
		worsePop=new GTree[index];
		betterPop=new GTree[populationSize-index];
		worseFitness=new double[index];
		betterFitness=new double[populationSize-index];
		
		for (int i=0;i<index;i++){
			double fitness=popNormalizedFitness[i];
			ArrayList<Integer>indices=map.get(fitness);
			
			worsePop[i]=population[indices.get(0)];
			indices.remove(0);
			worseFitness[i]=fitness;
			map.put(fitness, indices);
		}
		
		for (int i=index,j=0;i<populationSize;i++,j++){
			double fitness=popNormalizedFitness[i];
			ArrayList<Integer>indices=map.get(fitness);
			betterPop[j]=population[indices.get(0)];
			indices.remove(0);
			betterFitness[j]=fitness;
			map.put(fitness, indices);
		}
		
		int crossoverSize=(int)(populationSize*pCrossover);
		int reproductionSize=populationSize-crossoverSize;
		if (crossoverSize%2==1){
			crossoverSize--;
		}
		System.out.println(sum);
		//C
		for (int i=0;i<crossoverSize;i+=2,numNewPop+=2){
			GTree tree1=null,tree2=null;
			double rand=Math.random();
			if(rand<0.8){
				tree1=chooseOne(betterPop,betterFitness,1-sum).copy();
				tree2=chooseOne(betterPop,betterFitness,1-sum).copy();
			}else{
				tree1=chooseOne(worsePop,worseFitness,sum).copy();
				tree2=chooseOne(worsePop,worseFitness,sum).copy();
			}
			tree1.crossover(tree2);
			newPopulation[numNewPop]=tree1;
			newPopulation[numNewPop+1]=tree2;
		}
		
		//R
		for (int i=0;i<reproductionSize;i++,numNewPop++){
			GTree tree=null;
			GTree newTree;
			double rand=Math.random();
			if(rand<0.8){
				tree=chooseOne(betterPop,betterFitness,1-sum).copy();
			}else{
				tree=chooseOne(worsePop,worseFitness,sum).copy();
			}
			newTree=tree.reproduce();
			newPopulation[numNewPop]=newTree;
		}
		System.out.println(1111);

	}
	
	public static GTree chooseOne(GTree[] population, double[] popFitness){
		GTree tree=null;
		GTree newTree;
		double rand;
		double randAcc=0;
		rand=Math.random();
		for(int i=0;rand-randAcc>=0;i++){
			tree=population[i];
			randAcc+=popFitness[i];
		}
		return tree;
	}
	
	public static GTree chooseOne(GTree[] population, double[] popFitness,double randTotal){
		GTree tree=null;
		GTree newTree;
		double rand;
		double randAcc=0;
		rand=Math.random()*randTotal;
		int i=0;
		do{
			tree=population[i];
			randAcc+=popFitness[i];
		}while(rand-randAcc>=0);
		return tree;
	}
	
	public static double sum(double[] list){
		double sum=0;
		for (int i=0;i<list.length;i++){
//			System.out.println(list[i]);
			sum+=list[i];
		}
		return sum;
	}
	
	public static double getFitness(GTree tree,Polynomial f,DoubleObj[] outputVector,int linearFactors,int quadraticFactors){
		setAll(outputVector,0);
		Polynomial[] factors = new Polynomial[1 + linearFactors + quadraticFactors];
		tree.getValue();
		int j=0;
		double[] scalar={outputVector[j].value};
		factors[j]=new Polynomial(scalar);
		j++;
		for (int i=0;i<linearFactors;j++,i++){
			double[] root={outputVector[j].value,1};
			factors[j]=new Polynomial(root);
		}
		for (int k=j;k<outputVector.length;k+=2,j++){
			double[] root={outputVector[k].value,outputVector[k+1].value,1};
			factors[j]=new Polynomial(root);
		}
		double fitness=0;
		for (int i=0;i<20;i++){
			double rand=i*50-500;
			double goal=f.evaluate(rand);
			double value=1;
			for (int k=0;k<factors.length;k++){
				value*=factors[k].evaluate(rand);
			}
			fitness+=Math.pow(Math.abs(goal-value),2);
		}
		return fitness;	
	}
	
	public static double getFitness(GTree tree,PolynomialComplex f,DoubleObj[] outputVector){
		double fitness=0;
		for (int i=0;i<5;i++){
			for (int k=0;k<5;k++){
				setAll(outputVector,0);
				Complex rand=new Complex(i*10-25,k*10-25);
				Complex goal=f.evaluate(rand);
				tree.getValue();
				Complex value=new Complex(outputVector[0].value,outputVector[1].value);
				for (int j=2;j<outputVector.length;j+=2){
					Complex[] root={new Complex(-outputVector[j].value,-outputVector[j+1].value),new Complex(1,0)};
					PolynomialComplex x=new PolynomialComplex(root);
					value=value.multiply(x.evaluate(rand));
				}
				Complex error=goal.subtract(value);
				fitness+=error.norm();
			}
		}
		return fitness;	
	}
	
	public static void setAll(DoubleObj[] list,double value){
		for (int i=0;i<list.length;i++){
			list[i].value=value;
		}
	}
}