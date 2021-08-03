package com.example.demo.record;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TestWithRecord {
	Test3 	userData;
	
	public String test() {
		record TestInner(Integer id) {};
		var test = new TestInner(userData.id());
		return test.id() + " " + userData.name() + " " +  userData.fullName();
	}
}
