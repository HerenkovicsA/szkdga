package com.szakdolgozat.geneticAlg;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szakdolgozat.domain.Order;

import javafx.util.Pair;

public class GenAlgBusiness {
	
	private Population pop;
	private int popSize;
	private int iterationMax;
	private int elitism;
	private List<Order> orders;
	private int[][] citiesDistances;
	
	private Crossover xover = new Crossover();
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	public GenAlgBusiness(int popSize, int iterationMax, int elitism, List<Order> orders, int[][] citiesDistances) {
		this.popSize = popSize;
		this.iterationMax = iterationMax;
		this.elitism = elitism;
		this.orders = orders;
		this.citiesDistances = citiesDistances;
	}

	public Pair<Double, List<Order>> go() {
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
		long stopTime = System.currentTimeMillis();
		log.info("Genetic algorithm took: " + (stopTime - startTime) + " ms");
		
		return new Pair<Double, List<Order>>(pop.getBestChrom().getChromValue()/1000, pop.getBestChrom().getTour(orders));
	}
	
}
