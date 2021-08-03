package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.demo.log.MapAppender;

/**
 * synchronized on primitive wrapper classes with -XX:DiagnoseSyncOnValueBasedClasses=1  should throw an error
 * 
 * Fatal error has been thrown but i cannot find how to handle it.
 * Switch to -XX:DiagnoseSyncOnValueBasedClasses=2 and check in logs 
 * but still don't know how to get the system logs (jdk.internal)
 * 
 * @author legioner
 *
 */
@SpringBootTest
public class ValueBasedPrimitiveTests {
	
	@Test
	public void test() {
		var i = new Integer(5);
		var i2 = Integer.valueOf(5);
		var i3 = 5;
		synchronized (i2) {
				i3 = 10;
		}
		assertEquals(10, i3);
		//Still don't know how to do it in test.
		//But it works - error has been thrown, visible in log (which i can't intercept to check with junit)
		//MapAppender.eventMap.forEach((s, le) -> System.out.println(le.getMessage()));
		//[2,578s][info][valuebasedclasses] Synchronizing on object 0x00000007ffe81de0 of klass java.lang.Integer
		
	}
	
	
	
	
}
