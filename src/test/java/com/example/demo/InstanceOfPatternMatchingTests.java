package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SpringBootTest
public class InstanceOfPatternMatchingTests {

	@Test
	public void test() {
		var first = new SomeClass("first");
		var second = new SomeOtherClass("second");
		var firstObject = (Object)first;
		var secondObject = (Object)second;
		assertTrue(firstObject instanceof SomeIntf);
		assertTrue(firstObject instanceof BaseClass);
		assertTrue(firstObject instanceof SomeClass);
		assertFalse(secondObject instanceof SomeClass);

		String test = null;
		if (firstObject instanceof SomeIntf intfInst) {
			test = intfInst.getValue();
		}
		assertEquals("first", test);
		if (secondObject instanceof SomeOtherClass soClass && !soClass.getValue().isEmpty()) {
			test = soClass.getValue();
		}
		assertEquals("second", test);
		try {
			assertEquals("second", onlyForSomeIntf(secondObject));
		} catch (MyException e) {
			e.printStackTrace();
			assertFalse(e != null);
		}
		
	}
	
	private String onlyForSomeIntf(Object o) throws MyException {
	    if (!(o instanceof SomeIntf s)) {
	    	throw new MyException();
	    }
	    //Duplicate local variable s
//	    if (o instanceof SomeIntf s) {
//	    	
//	    }
	    return s.getValue();
	}
	
	private class MyException extends Exception {};
	
	private static interface SomeIntf {
		String getValue();
	}
	
	private static abstract class BaseClass implements SomeIntf {
		
	} 

	@RequiredArgsConstructor @Getter
	private static class SomeClass extends BaseClass implements SomeIntf {
		private final String value;
	}

	@RequiredArgsConstructor @Getter
	private static class SomeOtherClass extends BaseClass implements SomeIntf {
		private final String value;
	}

	
}
