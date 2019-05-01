package com.szakdolgozat.geneticAlg;

import java.util.ArrayList;
import java.util.Iterator;
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
		
		while(!chromForChild.contains(next = NextGenForHGreX(chromForChild, chosenNewByRandom, parent1, parent2, citiesDistances))) {
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
