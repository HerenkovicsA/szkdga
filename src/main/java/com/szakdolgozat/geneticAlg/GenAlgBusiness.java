package com.szakdolgozat.geneticAlg;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GenAlgBusiness {
	
	private Population pop;
	private int popSize;
	private int iterationMax;
	private int elitism;
	private String cities[];
	private int[][] citiesDistances;
	
	private Crossover xover = new Crossover();
	/**
	 * GenAlgBusiness
	 * Create default object, only for testing.
	 * For normal use, use GenAlgBusiness(int popSize, int iterationMax, int elitism, String[] cities, int[][] citiesDistances)
	 */
	public GenAlgBusiness() {
		this.popSize = 10;
		this.iterationMax = 50;
		this.elitism = 2;
		this.cities = CandD.c2;
		this.citiesDistances = CandD.cd2;
	}
	
	public GenAlgBusiness(int popSize, int iterationMax, int elitism, String[] cities, int[][] citiesDistances) {
		this.popSize = popSize;
		this.iterationMax = iterationMax;
		this.elitism = elitism;
		this.cities = cities;
		this.citiesDistances = citiesDistances;
	}

	public void go() {
		long startTime = System.currentTimeMillis();
		pop = new Population(this.popSize,citiesDistances);
		Population  newPop;
		Chromosome offspring1;
		Chromosome offspring2;
		
		pop.orgenise();
		pop.setPropability();
		for (int i = 0; i < iterationMax; i++) {
			
			newPop = new Population();
			
			/*for(int j = 0; j < this.popSize/2 - 2; j++) {
				offspring1 = new Chromosome();
				offspring2 = new Chromosome();
				//xover.PMX(offspring1, offspring2, citiesDistances, pop, false);
				//xover.cycleCrossover(offspring1, offspring2, citiesDistances, pop);
				xover.alternatingEdgesCrossover(offspring1, offspring2, citiesDistances, pop);
				newPop.addChromToPop(offspring1);
				newPop.addChromToPop(offspring2);
			}*/
			for(int j = 0; j < this.popSize - elitism; j++) {
				offspring1 = new Chromosome();
				xover.hGreX(offspring1, citiesDistances, pop);
				newPop.addChromToPop(offspring1);
			}
						
			for(int j = 0; j < this.elitism; j++) {
				newPop.addChromToPop(pop.getPop().get(j));
			}
			
			newPop.orgenise();
			newPop.setPropability();
			pop = newPop;
			
		}
		System.out.print(pop.getBestChrom().getChromValue()+" km : ");
		System.out.println(pop.getBestChrom().getTour(cities));
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		wTF(pop.getBestChrom().getChromValue() + "\t" + elapsedTime);
	}
	
	private void wTF(String result) {
		String text = "";
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream("C:\\Users\\user\\Desktop\\ga.txt"));
			int i = 0;
			while( (i = bis.read()) != -1 ) {
				text += (char) i;
			}
			bis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream("C:\\Users\\user\\Desktop\\ga.txt"));
			byte[] textB = (text + System.lineSeparator() + result).getBytes();
			
			bos.write(textB);
			
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
