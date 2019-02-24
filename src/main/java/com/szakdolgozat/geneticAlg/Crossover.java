package com.szakdolgozat.geneticAlg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class Crossover {
	
	private double mutateChance = 0.25;
	
	//take random chromosome from the pop to use in xover
	private Chromosome runTheWheel(Population pop) {
		double randomNum = ThreadLocalRandom.current().nextDouble();
		Chromosome chosenOne = null;
		Iterator it = pop.getPop().iterator();
		while (it.hasNext() && ( chosenOne = ((Chromosome) it.next()) ).getPropBoundary()  < randomNum);
		
		return chosenOne;
	}
	
	private Chromosome tournamentRW(Population pop, int size) {
		ArrayList<Chromosome> racers = new ArrayList<Chromosome>();
		if(size > 0) {
			for (int i = 0; i < size; i++) {
				racers.add(runTheWheel(pop));
			}
			Collections.sort(racers);
		}else {System.err.println("size must be over 0");}
		return racers.get(0);
	}
	
	private Chromosome tournament(Population pop, int size) {
		ArrayList<Chromosome> racers = new ArrayList();
		
		if(size > 0) {
			for (int i = 0; i < size; i++) {
				int randomNum = ThreadLocalRandom.current().nextInt(pop.getPop().size());
				
				racers.add(pop.getPop().get(randomNum));
			}
			Collections.sort(racers);
		}else {System.err.println("size must be over 0");}
		return racers.get(0);
	}
	
	public int indexOfGenByVal(ArrayList<Gen> chrom,Gen genToFind) {
		int result = 0;
		for (Iterator iterator = chrom.iterator(); iterator.hasNext();) {
			Gen gen = (Gen) iterator.next();
			if(gen.equals(genToFind)) return result;
			result++;
		}
		return -1;
	}
	
	private void invertGens(int i1, int i2, ArrayList<Gen> chromForChild) {
		if(i1 > i2) {
			int temp = i2;
			i2 = i1;
			i1 = temp;
		}
		Gen tmp;
		for(int i = 0; i <= (i2 - i1)/2; i++) {
			tmp = chromForChild.get(i1 + i);
			chromForChild.set(i1 + i, chromForChild.get(i2 - i));
			chromForChild.set(i2 - i, tmp);
		}
	}
	
	//swap two randomly chosen gens
	private void mutate(Chromosome chromToMutate) {
		int index1 = ThreadLocalRandom.current().nextInt(chromToMutate.getChrom().size()-1)+1;
		int index2 = ThreadLocalRandom.current().nextInt(chromToMutate.getChrom().size()-1)+1;
		
		while(index1 == index2) {
			index2 = ThreadLocalRandom.current().nextInt(chromToMutate.getChrom().size()-1)+1;
		}
		this.invertGens(index1, index2, chromToMutate.getChrom());
	}
	
	public void PMX(Chromosome child1,Chromosome child2, int[][] citiesDistances, Population pop, boolean mappedOrMatched) {
		Chromosome parent1 = this.runTheWheel(pop);//this.tournamentRW(pop, 4);//this.tournament(pop, 2);
		Chromosome parent2 = this.runTheWheel(pop);//this.tournamentRW(pop, 4);//this.tournament(pop, 2);
		
		ArrayList<Gen> chromForChild1, chromForChild2;
		int genSize = parent1.getChrom().size();
		int cutPoint1 = ThreadLocalRandom.current().nextInt(genSize);
		int cutPoint2 = ThreadLocalRandom.current().nextInt(genSize);
		int indexToSwap1,indexToSwap2;
		
		while(cutPoint1 == cutPoint2) {
			cutPoint2 = ThreadLocalRandom.current().nextInt(genSize);
		}
		
		if (cutPoint2 < cutPoint1) {
			int tmp = cutPoint2;
			cutPoint2 = cutPoint1;
			cutPoint1 = tmp;
		}
		
		ArrayList<Gen> middleSegment1 = new ArrayList<Gen>(parent1.getChrom().subList(cutPoint1, cutPoint2));
		ArrayList<Gen> middleSegment2 = new ArrayList<Gen>(parent2.getChrom().subList(cutPoint1, cutPoint2));
		
		chromForChild1 = new ArrayList(parent1.getChrom());
		chromForChild2 = new ArrayList(parent2.getChrom());
		
		if(mappedOrMatched) {
			//mapped
			for(int i = cutPoint1, j = 0; i < cutPoint2; i++, j++ ) {
				chromForChild2.set(i, parent1.getChrom().get(i));
				if( middleSegment1.indexOf(middleSegment2.get(j)) == -1 ){
					Gen tmp = middleSegment1.get(j);
					while (middleSegment2.contains(tmp)) {
						tmp = middleSegment1.get(middleSegment2.indexOf(tmp));
					}
					chromForChild2.set(parent2.getChrom().indexOf(tmp), middleSegment2.get(j));
					 
				}
			}
			for(int i = cutPoint1, j = 0; i < cutPoint2; i++, j++) {
				chromForChild1.set(i, parent2.getChrom().get(i));
				if(  middleSegment2.indexOf(middleSegment1.get(j)) == -1 ){
					Gen tmp = middleSegment2.get(j);
					while (middleSegment1.contains(tmp)) {
						tmp = middleSegment2.get(middleSegment1.indexOf(tmp));
					}
					chromForChild1.set(parent1.getChrom().indexOf(tmp), middleSegment1.get(j));
					 
				}
			}
		}else {
			//matched
			for (int i = 0; i < middleSegment1.size(); i++) {
				indexToSwap1 = indexOfGenByVal(chromForChild1, middleSegment1.get(i));
				indexToSwap2 = indexOfGenByVal(chromForChild1, middleSegment2.get(i));
				invertGens(indexToSwap1, indexToSwap2, chromForChild1);
			}
			
			for (int i = 0; i < middleSegment2.size(); i++) {
				indexToSwap1 = indexOfGenByVal(chromForChild2, middleSegment2.get(i));
				indexToSwap2 = indexOfGenByVal(chromForChild2, middleSegment1.get(i));
				invertGens(indexToSwap1, indexToSwap2, chromForChild2);
			}
		}
		
		child1.setChrom(chromForChild1);
		if (ThreadLocalRandom.current().nextDouble() <= this.mutateChance) {
			this.mutate(child1);
		}
		child1.calculateChromValue(citiesDistances);
		child1.calculateFitness();
		
		child2.setChrom(chromForChild2);
		if (ThreadLocalRandom.current().nextDouble() <= this.mutateChance) {
			this.mutate(child2);
		}
		child2.calculateChromValue(citiesDistances);
		child2.calculateFitness();
	}
	
	public void cycleCrossover(Chromosome child1,Chromosome child2, int[][] citiesDistances, Population pop) {
		
		Chromosome parent1 = this.tournament(pop, 4);//this.tournamentRW(pop, 2);//this.runTheWheel(pop);//
		Chromosome parent2 = this.tournament(pop, 4);
				
		child1.setChrom(cX(parent1,parent2));
		if (ThreadLocalRandom.current().nextDouble() <= this.mutateChance) {
			this.mutate(child1);
		}
		child1.calculateChromValue(citiesDistances);
		child1.calculateFitness();
		
		child2.setChrom(cX(parent2,parent1));
		if (ThreadLocalRandom.current().nextDouble() <= this.mutateChance) {
			this.mutate(child2);
		}
		child2.calculateChromValue(citiesDistances);
		child2.calculateFitness();
	}

	private ArrayList<Gen> cX(Chromosome parent1, Chromosome parent2) {
		ArrayList<Gen> chromForChild = new ArrayList<Gen>(Collections.nCopies(parent1.getChrom().size(), new Gen()));
	
		chromForChild.set(0, parent1.getChrom().get(0));
		
		Gen next = parent2.getChrom().get(0);
		int placeForNext = parent1.getChrom().indexOf(next);
		while(chromForChild.indexOf(next) == -1) {
			chromForChild.set(placeForNext, next);
			next = parent2.getChrom().get(placeForNext);
			placeForNext = parent1.getChrom().indexOf(next);
		}
		for(int i = 0; i < chromForChild.size(); i++) {
			if(chromForChild.get(i).getAllele() == -1) {
				chromForChild.set(i, parent2.getChrom().get(i));
			}
		}
		
		return chromForChild;
	}
	
	public void alternatingEdgesCrossover(Chromosome child1,Chromosome child2, int[][] citiesDistances, Population pop) {
		ArrayList<Gen> parent1 = this.tournamentRW(pop, 4).getChrom();//this.runTheWheel(pop).getChrom();////this.tournament(pop, 2).getChrom();
		ArrayList<Gen> parent2 = this.tournamentRW(pop, 4).getChrom();
		ArrayList<Gen> chooseFrom;
		ArrayList<Gen> chromForChild = new ArrayList<Gen>();
		Gen next;
		boolean choseNewByRandom = false;
		
		chromForChild.add(parent1.get(0));
		chromForChild.add(parent1.get(1));
		chooseFrom = parent2;
		//first child
		while(chromForChild.size() != parent1.size()) {
			for(int i = 0; !chromForChild.contains(next = NextGenForAEX(chromForChild, chooseFrom, choseNewByRandom, new ArrayList<Gen>(parent1))); i++) {
				chromForChild.add(next);			
				chooseFrom = (i % 2 == 0)?parent1:parent2;
				choseNewByRandom = false;
			}
			choseNewByRandom = true;
		}
						
		child1.setChrom(chromForChild);
		if (ThreadLocalRandom.current().nextDouble() <= this.mutateChance) {
			this.mutate(child1);
		}
		child1.calculateChromValue(citiesDistances);
		child1.calculateFitness();
		
		//second child
		chromForChild = new ArrayList<Gen>();
		chromForChild.add(parent2.get(0));
		chromForChild.add(parent2.get(1));
		chooseFrom = parent1;
		choseNewByRandom = false;
		
		while(chromForChild.size() != parent2.size()) {
			for(int i = 0; !chromForChild.contains(next = NextGenForAEX(chromForChild, chooseFrom, choseNewByRandom, new ArrayList<Gen>(parent2))); i++) {
				chromForChild.add(next);			
				chooseFrom = (i % 2 == 0)?parent2:parent1;
				choseNewByRandom = false;
			}
			choseNewByRandom = true;
		}
		
		child2.setChrom(chromForChild);
		if (ThreadLocalRandom.current().nextDouble() <= this.mutateChance) {
			this.mutate(child2);
		}
		child2.calculateChromValue(citiesDistances);
		child2.calculateFitness();
	}
	
	private Gen NextGenForAEX(ArrayList<Gen> chromForChild,ArrayList<Gen> chooseFrom, boolean chosenNewByRandom, ArrayList<Gen> parent) {
		if(chosenNewByRandom) {
			parent.removeAll(chromForChild);
			return parent.get(ThreadLocalRandom.current().nextInt(parent.size()));
		}else {
			int indexOfActualInNextParent = chooseFrom.indexOf(chromForChild.get(chromForChild.size()-1));
			int indexForNext = (indexOfActualInNextParent == chooseFrom.size()-1) ? 0 : indexOfActualInNextParent + 1;
			return chooseFrom.get(indexForNext);	
		}
	}
	
	public void hGreX(Chromosome child1, int[][] citiesDistances, Population pop) {
		ArrayList<Gen> parent1 = this.runTheWheel(pop).getChrom();
		ArrayList<Gen> parent2 = this.runTheWheel(pop).getChrom();
		ArrayList<Gen> chromForChild = new ArrayList<Gen>();
		Gen next;
		boolean chosenNewByRandom = false;
		
		int starterIndex = ThreadLocalRandom.current().nextInt(parent1.size());
		//Chosen from p2
		int indexForNext1 = (starterIndex == parent1.size()-1) ? 0 : starterIndex + 1;
		int parent1Distance = citiesDistances[parent1.get(starterIndex).getAllele()][parent1.get(indexForNext1).getAllele()];
		
		//Chosen from p2
		int indexForNext2 = (starterIndex == parent2.size()-1) ? 0 : starterIndex + 1;
		int parent2Distance = citiesDistances[parent2.get(starterIndex).getAllele()][parent2.get(indexForNext2).getAllele()];
		
		if(parent1Distance >= parent2Distance) {
			chromForChild.add(parent2.get(0));
			chromForChild.add(parent2.get(1));
		}else {
			chromForChild.add(parent1.get(0));
			chromForChild.add(parent1.get(1));
		}
		
		for(int i = 0; !chromForChild.contains(next = NextGenForHGreX(chromForChild, chosenNewByRandom, parent1, parent2, citiesDistances)); i++) {
			chromForChild.add(next);
		}
						
		child1.setChrom(chromForChild);
		if (ThreadLocalRandom.current().nextDouble() <= this.mutateChance) {
			this.mutate(child1);
		}
		child1.calculateChromValue(citiesDistances);
		child1.calculateFitness();
	}

	private Gen NextGenForHGreX(ArrayList<Gen> chromForChild, boolean chosenNewByRandom, ArrayList<Gen> parent1, ArrayList<Gen> parent2, int[][] citiesDistances) {
		if(chosenNewByRandom) {
			ArrayList<Gen> parent = new ArrayList<Gen>(parent1);
			ArrayList<Gen> chosenOnes = new ArrayList<Gen>();
			Gen cheapest = new Gen(Integer.MAX_VALUE);
			int i;
			
			parent.removeAll(chromForChild);
			for(i = 0; i < 3 && !parent.isEmpty(); i++) {
				chosenOnes.add( parent.get(ThreadLocalRandom.current().nextInt(parent.size())) );
				parent.remove(chosenOnes.get(i));
			}
			if(i == 0) {
				return chromForChild.get(0);
			}
			for (i = i-1; i >= 0; i--) {
				if (cheapest.getAllele() > chosenOnes.get(i).getAllele())
					cheapest = chosenOnes.get(i);
			} 

			return cheapest;
		}else {
			int indexForNext1 = -1;
			int parent1Distance = -1;
			int indexOfActualInNextParent1 = -1;
			
			int indexForNext2 = -1;
			int parent2Distance = -1;
			int indexOfActualInNextParent2 = -1;
			//Chosen from p1
			indexOfActualInNextParent1 = parent1.indexOf(chromForChild.get(chromForChild.size() - 1));
			indexForNext1 = (indexOfActualInNextParent1 == parent1.size() - 1) ? 0 : indexOfActualInNextParent1 + 1;
			parent1Distance = citiesDistances[parent1.get(indexOfActualInNextParent1).getAllele()][parent1
					.get(indexForNext1).getAllele()];
			//Chosen from p2
			indexOfActualInNextParent2 = parent2.indexOf(chromForChild.get(chromForChild.size() - 1));
			indexForNext2 = (indexOfActualInNextParent2 == parent2.size() - 1) ? 0 : indexOfActualInNextParent2 + 1;
			parent2Distance = citiesDistances[parent2.get(indexOfActualInNextParent2).getAllele()][parent2
					.get(indexForNext2).getAllele()];

			if(chromForChild.contains(parent1.get(indexForNext1)) && chromForChild.contains(parent2.get(indexForNext2))) {
				return NextGenForHGreX(chromForChild, true, parent1, parent2, citiesDistances);
			}else if(chromForChild.contains(parent1.get(indexForNext1))) {
				return parent2.get(indexForNext2);
			}else if(chromForChild.contains(parent2.get(indexForNext2))) {
				return parent1.get(indexForNext1);
			}
			
			return (parent1Distance>=parent2Distance)?parent2.get(indexForNext2):parent1.get(indexForNext1);
		}
	}
}
