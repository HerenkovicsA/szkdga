package com.szakdolgozat.geneticAlg;

public class CandD {

	private int[][] distances;
	private String[] cities;

	public CandD(int[][] distances, String[] cities) {
		this.distances = distances;
		this.cities = cities;
	}
	
	public int[][] getDistances() {
		return distances;
	}
	
	public void setDistances(int[][] distances) {
		this.distances = distances;
	}
	
	public String[] getCities() {
		return cities;
	}
	
	public void setCities(String[] cities) {
		this.cities = cities;
	}
	
	

}
