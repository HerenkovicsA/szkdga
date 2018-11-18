
package com.szakdolgozat.geneticAlg;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.szakdolgozat.domain.Order;

public class Chromosome implements Comparable{

	private double fitness;
	private int value;
	private ArrayList<Gen> chrom;
	private double propability;
	private double propBoundary;
	private DecimalFormat df = new DecimalFormat("#.#########");
	
	
	public Chromosome(){
		this.value=-1;
		this.fitness = -1;
		this.chrom = new ArrayList<Gen>();
	}

	public Chromosome(ArrayList<Gen> chrom, int ar[][]) {
		this.chrom = chrom;
		calculateChromValue(ar);
		calculateFitness();
	}
	
	
	public Chromosome(double fitness, int value, ArrayList<Gen> chrom) {
		this.fitness = fitness;
		this.value = value;
		this.chrom = chrom;
	}
	
	public double getPropBoundary() {
		return propBoundary;
	}

	public void setPropBoundary(double propBoundary) {
		this.propBoundary = propBoundary;
	}

	public double getPropability() {
		return propability;
	}

	public void setPropability(double propability) {
		this.propability = propability;
	}

	public double getFitness() {
		return fitness;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public void calculateFitness() {
		this.fitness = (double)1/this.getChromValue();
	}
	
	public double getChromValue() {
		return this.value;
	}
	
	public void calculateChromValue(int ar[][]) {
		int val = 0;
		int i;
		for (i = 1; i < this.chrom.size(); i++) {
			val += ar[chrom.get(i-1).getAllele()][chrom.get(i).getAllele()];
		}
		val += ar[chrom.get(i-1).getAllele()][chrom.get(0).getAllele()];
		this.value = val;
	}
	
	public void setChromValue(int value) {
		this.value = value;
	}
	
	public ArrayList<Gen> getChrom() {
		return chrom;
	}
	
	public void setChrom(ArrayList<Gen> chrom) {
		this.chrom = chrom;
	}

	public void addGenToChrom(Gen gen) {
		this.chrom.add(gen);
	}
	
	
	@Override
	public String toString() {
		return "Chromosome [fitness=" + df.format(fitness) + ", value=" + value + ", prop=" + propability + ", pB= " + propBoundary + ", chrom=" + chrom + "]\n";
	}
	
	public static Chromosome randomChromGenerator(int citiesAndDistances[][]) {
		int cityNumber = citiesAndDistances[0].length ;
		ArrayList<Gen> genList = new ArrayList<Gen>();
		Set<Integer> citySet = new HashSet<Integer>();
		//filling up city hash with numbers from 0 to the of cites
		for (int i = 0; i < cityNumber; i++) {
			citySet.add(i);			
		}
		genList.add(new Gen(0));
		citySet.remove(0);
		while(!citySet.isEmpty()) {
			int randomNum = ThreadLocalRandom.current().nextInt(cityNumber);
			if(citySet.contains(randomNum)) {
				genList.add(new Gen(randomNum));
				citySet.remove(randomNum);
			}
		}
		
		Chromosome newChrom = new Chromosome(genList,citiesAndDistances);
		
		
		return newChrom;
	}

	@Override
	public int compareTo(Object o) {
		if(this.fitness > ((Chromosome) o).fitness) {
			return -1;
		}else if(this.fitness == ((Chromosome) o).fitness){
			return 0;
		}else {
			return 1;
		}
	}
	
	public List<Order> getTour(List<Order> orders) {
		List<Order> result = new ArrayList<Order>();
		for (Iterator iterator = chrom.iterator(); iterator.hasNext();) {
			Gen gen = (Gen) iterator.next();
			result.add(orders.get(gen.getAllele()));
		}
		result.add(orders.get(0));
		return result;
	}
	
}
