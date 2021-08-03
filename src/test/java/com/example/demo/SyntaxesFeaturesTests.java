package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SyntaxesFeaturesTests {
	
	private static final String textBlock1 = """
			World
			""";

	private static final String textBlock2 = """
			World""";

	private static final String textBlock3 = """ 
			"World"
""";
	
	private static final String textBlock4 = """
World""";

	@Test
	public void testTextBlocks() {
		assertEquals("HelloWorld\n!", "Hello" + textBlock1 + "!");
		assertEquals("HelloWorld!", "Hello" + textBlock2 + "!");
		assertEquals("Hello			\"World\"\n!", "Hello" + textBlock3 + "!");
		assertEquals("HelloWorld!", "Hello" + textBlock4 + "!");
	}

	@Test
	public void testNewSwitchFeatures() {
		int a = 5;
		int b = 0;
		//classic old
		switch (a) {
			case 1: b = 10;
				break;
			case 5: b = 25;
				break;
			default: b = 28;
				break;
		}
		assertEquals(25, b);
		//new, expression
		b = switch (a) {
			case 1 -> 18;
			case 5 -> 45;
			default -> 58;
		};
		assertEquals(45, b);
		//new, blocks with yield
		int c = 1;
		b = switch(a) {
			case 1 -> {
				c = 7;
				yield c;
				}
			case 5 -> {
				c = 14;
				yield c;
				}
			default -> {
				c = 28;
				yield c;
			}
		};
		assertEquals(14, b);
	}
	
	
	
}
