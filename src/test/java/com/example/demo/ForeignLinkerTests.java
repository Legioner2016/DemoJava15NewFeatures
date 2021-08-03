package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.LibraryLookup;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

/**
 * Will don't test upcalls - rarely used
 * 
 * @author legioner
 *
 */
@SpringBootTest
public class ForeignLinkerTests {

	private static final String LIB_PATH = "/home/legioner/workspace_java16/demo/src/main/java/com/example/demo/foreign_linker/libctest.so.1.0";
	
	@Test
	public void testDowncall() {
		LibraryLookup lib = LibraryLookup.ofPath(Path.of(LIB_PATH));
		Optional<LibraryLookup.Symbol> square = lib.lookup("square");
		assertTrue(square.isPresent());
		
		MethodHandle call = CLinker.getInstance().downcallHandle(
				square.get(),
		        MethodType.methodType(int.class, int.class),
		        FunctionDescriptor.of(CLinker.C_INT, CLinker.C_INT)
		    );

		try {
			int test = (int) call.invokeExact(5);
			assertEquals(25, test);
		} catch (Throwable e) {
			e.printStackTrace();
			assertFalse(e != null);
		}
		
		Optional<LibraryLookup.Symbol> alloc = lib.lookup("allocmemory");
		assertTrue(alloc.isPresent());

		Optional<LibraryLookup.Symbol> free = lib.lookup("freeMemory");
		assertTrue(free.isPresent());
		
		MethodHandle allocMethod = CLinker.getInstance().downcallHandle(
				alloc.get(),
		        MethodType.methodType(MemoryAddress.class, int.class),
		        FunctionDescriptor.of(CLinker.C_POINTER, CLinker.C_INT)
		    );

		MethodHandle freeMethod = CLinker.getInstance().downcallHandle(
				free.get(),
		        MethodType.methodType(void.class, MemoryAddress.class),
		        FunctionDescriptor.ofVoid(CLinker.C_POINTER)
		    );
		
		try {
			MemoryAddress buff = (MemoryAddress) allocMethod.invokeExact(5);
			freeMethod.invokeExact(buff);
		} catch (Throwable e) {
			e.printStackTrace();
			assertFalse(e != null);
		}

		
	}
	
}
