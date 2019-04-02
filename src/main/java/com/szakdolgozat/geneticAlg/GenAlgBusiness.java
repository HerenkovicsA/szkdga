package com.szakdolgozat.geneticAlg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szakdolgozat.domain.Order;

import org.springframework.data.util.Pair;

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
		log.info("Start genetic algorithms");
		long startTime = System.currentTimeMillis();
		
		List<Chromosome> bestsList = new ArrayList<Chromosome>();		
		ExecutorService executor = Executors.newCachedThreadPool();
		log.info("order size: " + orders.size());
		if(orders.size() == 2) {
			List<Order> easyList = new ArrayList<Order>();
			easyList.add(orders.get(0));
			easyList.add(orders.get(1));
			easyList.add(orders.get(0));
			return Pair.of((double) (citiesDistances[0][1]/1000), easyList);
		}
		for(int j = 0; j < 3; j++) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					runOnce(bestsList);
				}
			});
		}		
		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
			while(!executor.isTerminated()) {
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		long stopTime = System.currentTimeMillis();
		log.info("Genetic algorithms took: " + (stopTime - startTime) + " ms");
		Collections.sort(bestsList);
		System.out.println(bestsList);
		return Pair.of(bestsList.get(0).getChromValue()/1000, bestsList.get(0).getTour(orders));
	}
	
	private void runOnce(List<Chromosome> bestsList) {
		log.info("Thread has started");
		pop = new Population(this.popSize,citiesDistances);
		Population  newPop;
		Chromosome offspring1;
		
		pop.orgenise();
		pop.setPropability();
		for (int i = 0; i < iterationMax; i++) {
			newPop = new Population();
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
		addToList(bestsList, pop.getBestChrom());
		log.info("Thread has stopped");
	}
	
	private synchronized void addToList(List<Chromosome> list, Chromosome chrom) {
		list.add(chrom);
	}
}
