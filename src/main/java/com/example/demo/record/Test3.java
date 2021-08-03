package com.example.demo.record;

public record Test3(String name, String lastName, Integer id) {
	
	public String fullName() {
		return (name == null ? "" : name + " ") + safeNull(lastName);
	} 
	
	private String  safeNull(String value) {
		return value == null ? "" : value;
	}
}
