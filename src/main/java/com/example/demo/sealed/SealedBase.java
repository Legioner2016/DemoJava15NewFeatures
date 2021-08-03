package com.example.demo.sealed;

@SuppressWarnings("preview")
abstract sealed class SealedBase {
	public abstract String getValue();
}

@SuppressWarnings("preview")
non-sealed class FirstChild extends SealedBase {

	@Override
	public String getValue() {
		return "first";
	}
	
} 

class SecondChild extends FirstChild {

	@Override
	public String getValue() {
		return "second";
	}
	
} 


final class ThirdChild extends SealedBase {

	@Override
	public String getValue() {
		return "third";
	}
	
} 
