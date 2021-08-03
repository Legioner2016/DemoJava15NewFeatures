package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorSpecies;

/**
 * AVX (SSE) in action... :)
 * 
 * I know that my cpu compatible only with AVX256 (in fact, it operates with pair of 128-bit registers)
 * So, especially interesting for me - how it would be work with 512-bits vectors   
 * 
 * @author legioner
 *
 */
@SpringBootTest
public class VectorsTest {
	
	static final VectorSpecies<Float> SPECIES_256 = FloatVector.SPECIES_256;
	static final VectorSpecies<Float> SPECIES_512 = FloatVector.SPECIES_512;

	
	@Test
	public void testVector256() {
		float[] a = new float[8 * 20000];
		float[] b = new float[8 * 20000];
		for (int i = 0; i < a.length; i++) {
			a[i] = (float)Math.random();
			b[i] = (float)Math.random();
		}
		
		float[] c = new float[8 * 20000];
		//classic
		long start = System.nanoTime();
		for (int i = 0; i < a.length; i++) {
		        c[i] = a[i] + b[i];
		}
		long end = System.nanoTime();
		float[] c_ = new float[8 * 20000];
		long start_ = System.nanoTime();
	    for (int i = 0; i < a.length; i += SPECIES_256.length()) {
	        var m = SPECIES_256.indexInRange(i, a.length);
			// FloatVector va, vb, vc;
	        var va = FloatVector.fromArray(SPECIES_256, a, i, m);
	        var vb = FloatVector.fromArray(SPECIES_256, b, i, m);
	        var vc = va.add(vb);
	        vc.intoArray(c_, i, m);
	    }
		
		long end_ = System.nanoTime();
		
		System.out.println((end_ - start_) +  " " + (end - start)); //Nice. 151 times faster
		
		assertTrue(1.5 <= ((double)(end_ - start_)) / ((double)(end - start))); //Some time ago write jni c library
																			//used avx; and it shown more than 1.5 times increase of calculation speed 
		assertEquals(c[100500], c_[100500], 0.00001f);
	} 

	//Good. It's work on my cpu 
	@Test
	public void testVector512() {
		float[] a = new float[16 * 10000];
		float[] b = new float[16 * 10000];
		
		for (int i = 0; i < a.length; i++) {
			a[i] = (float)Math.random();
			b[i] = (float)Math.random();
		}

		
		float[] c = new float[16 * 10000];
		//classic
		long start = System.nanoTime();
		for (int i = 0; i < a.length; i++) {
		        c[i] = a[i] + b[i];
		}
		long end = System.nanoTime();
		float[] c_ = new float[16 * 10000];
		long start_ = System.nanoTime();
	    for (int i = 0; i < a.length; i += SPECIES_512.length()) {
	        var m = SPECIES_512.indexInRange(i, a.length);
			// FloatVector va, vb, vc;
	        var va = FloatVector.fromArray(SPECIES_512, a, i, m);
	        var vb = FloatVector.fromArray(SPECIES_512, b, i, m);
	        var vc = va.add(vb);
	        vc.intoArray(c_, i, m);
	    }
		
		long end_ = System.nanoTime();
		assertTrue(1.5 <= ((double)(end_ - start_)) / ((double)(end - start))); //Some time ago wrote jni c library
																			//used avx; and it shown more than 1.5 times increase of calculation speed 
		assertEquals(c[100500], c_[100500], 0.00001f);
		
	} 

	
	//To do: rewrite all my maths to vectors :))
	
}
