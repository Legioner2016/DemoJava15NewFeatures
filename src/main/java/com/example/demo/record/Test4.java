package com.example.demo.record;

public record Test4(Integer id) {
	static int innerProperty = 25;
	
	public Test4() {
		this(0);
	}
	
	public void setInnerProperty(int value) {
		innerProperty = value;
	}
	
	public int getInnerProperty() {
		return innerProperty;
	}

}
