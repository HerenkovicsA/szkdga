package com.szakdolgozat.geneticAlg;

public class Gen implements Comparable{
	//value is the city where salesman is
	//in a 3*3 mtx 7 is [2,0]
	private int value;
	
	public Gen() {
		this.value = -1;
	}

	public Gen(int value) {
		this.value = value;
	}

	public int getAllele() {
		return value;
	}

	public void setAllele(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Gen [value=" + value + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return this.getAllele() == ((Gen)obj).getAllele();
	}

	@Override
	public int compareTo(Object o) {
		if(this.value > ((Gen) o).getAllele()) {
			return 1;
		}else if(this.value == ((Gen) o).getAllele()){
			return 0;
		}else {
			return -1;
		}
	}
	
	
}
