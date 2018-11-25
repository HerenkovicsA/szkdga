package com.szakdolgozat.geneticAlg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

	public void main() {
		
		/*double start = System.currentTimeMillis();
		for (int i = 0; i < 3; i++) {
			GenAlgBusiness gag = new GenAlgBusiness();
			gag.go();
		}
		double end = System.currentTimeMillis();
		System.out.println(end-start);*/
				
		double start = System.currentTimeMillis();
		ExecutorService executor = Executors.newCachedThreadPool();
		
		for(int j = 0; j < 3; j++) {
			//GenAlgBusiness gag = new GenAlgBusiness(100,500,10,CandD.c2,CandD.cd2);
			executor.submit(new Runnable() {
				public void run() {
					//gag.go();
				}
			});
		}
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
			while(!executor.isTerminated()) {
				Thread.sleep(1);
			}
			double end = System.currentTimeMillis();
			System.out.println(end-start);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}
	
	

}
