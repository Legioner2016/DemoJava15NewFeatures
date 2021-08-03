package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.sealed.SealedUsage;
import com.example.demo.sealed.TestImplClass;
import com.example.demo.sealed.TestIntf;

@SpringBootTest
public class SealedTests {
	
	@Test
	public void test() {
		var test = new SealedUsage();
		assertEquals("second",  test.getFirstValue());
		assertEquals("second",  test.getSecondValue());
		assertEquals("third",  test.getThirdValue());
	}

	@Test
	public void reflectionTest() {
		assertEquals(1, TestIntf.class.getPermittedSubclasses().length);
		assertEquals(TestImplClass.class, TestIntf.class.getPermittedSubclasses()[0]);
		assertTrue(TestIntf.class.isSealed());
	} 
	
}
