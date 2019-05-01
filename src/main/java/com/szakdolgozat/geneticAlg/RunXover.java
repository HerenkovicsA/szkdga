package com.szakdolgozat.geneticAlg;

public class RunXover implements Runnable{

	private Chromosome offspring1;
	private Crossover xover;
	private Population pop;
	private Population newPop;
	int[][] citiesDistances;
	int j = 0;
	

	public RunXover(Crossover xover, Population pop, Population newPop, int[][] citiesDistances, int j) {
		this.offspring1 = new Chromosome();
		this.xover = xover;
		this.pop = pop;
		this.newPop = newPop;
		this.citiesDistances = citiesDistances;
		this.j = j;
	}



	@Override
	public void run() {
		xover.hGreX(offspring1, citiesDistances, pop);
		newPop.addChromToPop(offspring1);

	}

	
}
