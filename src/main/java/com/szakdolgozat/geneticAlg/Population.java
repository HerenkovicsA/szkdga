package com.szakdolgozat.geneticAlg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Population {
	
	private static final Log LOG = LogFactory.getLog(Population.class);
	
	private ArrayList<Chromosome> pop;
	private double fitnessSum = 0;
	
	public ArrayList<Chromosome> getPop() {
		return pop;
	}

	public void setPop(ArrayList<Chromosome> pop) {
		this.pop = pop;
	}
	
	public synchronized void addChromToPop(Chromosome newChrom) {
		this.pop.add(newChrom);
	}
	
	public Population() {
		this.pop = new ArrayList<Chromosome>();
	}

	public Population(int size,int[][] citiesDistances) {
		
		this.pop = new ArrayList<Chromosome>();
		for (int i = 0; i < size; i++) {
			pop.add(Chromosome.randomChromGenerator(citiesDistances));
		}
	}
	//sort the chromosomes by it's fitness
	public void orgenise() {
		try {
			Collections.sort(this.pop);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		
	}
	//summ the overall fitness in the population
	private void summFitness() {
		for (Iterator iterator = pop.iterator(); iterator.hasNext();) {
			Chromosome chromosome = (Chromosome) iterator.next();
			this.fitnessSum += chromosome.getFitness();
		}
	}
	
	public void setPropability() {
		double propBoundary = 0;
		this.summFitness();
		for (Iterator iterator = pop.iterator(); iterator.hasNext();) {
			Chromosome chromosome = (Chromosome) iterator.next();
			chromosome.setPropability(chromosome.getFitness()/this.fitnessSum);
			
			propBoundary += chromosome.getPropability();
			chromosome.setPropBoundary(propBoundary);
		}
	}
	
	public Chromosome getBestChrom() {
		Chromosome best = pop.get(0);
		for (Chromosome chromosome : pop) {
			if(best.getChromValue() > chromosome.getChromValue()) {
				best = chromosome;
			}
		}
		return best;
	}

	@Override
	public String toString() {
		return "Population " + pop;
	}
	
}
