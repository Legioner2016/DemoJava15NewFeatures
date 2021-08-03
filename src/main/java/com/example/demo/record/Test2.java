package com.example.demo.record;

public record Test2(String name, Integer age) {
	public Test2 {
		if (age == null || age <= 0) throw new IllegalArgumentException("Wrong age");
	}
}
