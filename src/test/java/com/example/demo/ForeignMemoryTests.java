package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jdk.incubator.foreign.MemoryHandles;
import jdk.incubator.foreign.MemorySegment;

/**
 * Simple tests - byte array (buffer) of 5000000000 (5 billions) length
 * (that's impossible in pure Java)
 * 
 * @author legioner
 *
 */
@SpringBootTest
public class ForeignMemoryTests {

	@Test
	public void testForiegnBuffer() {
		boolean result = false;
		
		VarHandle byteHandle = MemoryHandles.varHandle(byte.class, ByteOrder.nativeOrder());
		long length = 5L * 1024L * 1024L * 1024L;
		
		try (MemorySegment segment = MemorySegment.allocateNative(length)) {
		    for (long i = 0; i < length; i++) {
		    	byteHandle.set(segment, i, (byte)(i & 0xff));
		    }
		    byte test = (byte) byteHandle.get(segment, 25);
		    assertEquals(25, test);
		    result = true;
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		assertTrue(result);
	}

	//Double to byte array test
	@Test
	public void testForiegnMemory() {
		boolean result = false;
		
		VarHandle doubleHandle = MemoryHandles.varHandle(double.class, ByteOrder.nativeOrder());
		VarHandle byteHandle = MemoryHandles.varHandle(byte.class, ByteOrder.nativeOrder());
		long length = 5L * 1024L * 1024L * 1024L;
		
		try (MemorySegment segment = MemorySegment.allocateNative(length)) {
			double first = 25d; //01000000 00111001 00000000 00000000 00000000 00000000 00000000 00000000
			                    //40       39       00        00      00       00       00       00      
		    for (long i = 0; i < length/8L; i+=8L) {
		    	if (i == 0) doubleHandle.set(segment, i * 8L, first);
		    	else doubleHandle.set(segment, i * 8L, Math.random());
		    }
		    byte[] dbRepresentation = new byte[8];
		    for (long i = 0; i < 8; i++) {
		    	dbRepresentation[(int)i] = (byte) byteHandle.get(segment, i); 
		    }
		    assertEquals("39", String.format("%02X", dbRepresentation[6]));
		    result = true;
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		assertTrue(result);
	}
	

}
