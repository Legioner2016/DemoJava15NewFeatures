package com.example.demo.sealed;


public class SealedUsage {
	SecondChild second;
	FirstChild first;
	ThirdChild third;

	public SealedUsage() {
		second = new SecondChild();
		first = (FirstChild)second;
		third = new ThirdChild();
	}
	
	public String getFirstValue() {
		return first.getValue();
	} 
	
	public String getSecondValue() {
		return second.getValue();
	} 

	public String getThirdValue() {
		return third.getValue();
	} 

	

}
