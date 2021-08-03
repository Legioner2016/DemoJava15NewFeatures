package com.example.demo;

import java.lang.ref.WeakReference;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * тот еще из меня изобретатель тестов...
 * 
 * Мысль такая: ограничить память небольшим количеством, создавать небольшие объекты, например - строки, 
 * т.к. они небольшие - они попадут в young generation, т.к. их больше чем размер памяти - 
 * по ним пойдет gc, что позволит посмотреть на ReadBarrier (буду к некоторым из них обращаться,
 *  через weakMap, например)
 * В итоге посмотреть на:
 *  - число операций в секунду, если памяти достаточно +
 *  - число операций в секунду, если памяти недостаточно +
 *  - падает ли epsilon, если память кончилась + 
 * 
 * Еще надо не забыть выключить оптимизатор... а то он сократит все это безобразие до одной операции с результатом
 * -XX:-TieredCompilation -XnoOpt -client
 * 
 * Serial GC -XX:+UseSerialGC
 * Parallel GC -XX:+UseParallelGC
 * Garbage First (G1) GC -XX:+UseG1GC
 * Shenandoah GC -XX:+UseShenandoahGC
 * ZGC -XX:+UseZGC
 * Epsilon GC -XX:+UseEpsilonGC
 * 
 * CMS - deprecated, excluded 
 *   
 * И это я еще не учитываю возможность настройки каждого GC - результаты могут быть совсем другими
 *   -Xlog:gc - лог gc. Не думаю, впрочем - что это можно будет обоснованно смотреть
 * 
 * Ограничить прамять - Xmx160M
 * 
 * @author legioner
 *
 */
@SpringBootTest
public class GCTest {

	@Test
	public void test() {
		int repeatTimes = 100;
		int operationTimes = 50 * (1*1024*1024 / 20);
		long[] results = new long[repeatTimes];
		final String basePart = "123456789abcdefghij";
		final String basePartUpdated = "123456789abcdefghijk";
		
		Map<Integer, TestClass> map = new WeakHashMap<Integer, GCTest.TestClass>();
		for (int i = 0; i < repeatTimes; i++) {
			String result = "";
			long start = System.nanoTime();
			for (int j = 0; j < operationTimes; j++) {
				TestClass t = new TestClass(basePart + (char)((j % 100) + 1));
				map.put(j, t);
				//WeakReference<TestClass> weak = new WeakReference(t);
				t = null;
				if (j > 0 && j % 100 == 0) {
					TestClass t_ = map.get(j - 100);
					if (t_ != null) {
						try {
							result += t_.getLastSymbol();
							t_.setData(basePartUpdated);
							result += t_.getLastSymbol();
						} 
						catch (OutOfMemoryError er) {
							//Кстати - непонятно - почему я тут ее ловлю на t_.getLastSymbol();
							//Может быть, её выдает при удалении объекта вместо NPE - но это было бы странно 
						}
					}
				}
			}
			long end = System.nanoTime();
			results[i] = end - start;
			System.out.println("********************************* Clear to next round ****************************");
			result = null;
			map.clear(); //Clear all to next round
			System.gc(); 
		}
		LongSummaryStatistics stat = LongStream.of(results).summaryStatistics();
		System.out.println("Average operation time = " + (stat.getAverage() / ((double)operationTimes)));
		System.out.println("Min operation time = " + (((double)stat.getMin()) / ((double)operationTimes)));
		System.out.println("Max operation time = " + (((double)stat.getMax()) / ((double)operationTimes)));
	}
	
	@Getter  
	public static class TestClass {
		private String data;
		
		public TestClass(String data) {
			this.data = new String(data); //!!
		}
		
		public void setData(String data) {
			this.data = new String(data);
		}
		
		public Character getLastSymbol() {
			return data.charAt(data.length() - 1);
		}
	}
	
}

/*
 * Выводы:
 * - если памяти достаточно, побеждает предсказуемо Epsilon (ZGC тут показал лучше результат - но это какие-то флуктуации - самый медленный его результат в 3 раза ниже).
 * Неожиданно, serial проиграл (я бы ожидал - когда памяти достаточно он не будет вызыватся часто. 
 * Видимо - логика weakMap, да - она - без нее serial - второй)
 * Второй по результатам для такого случая - ZGC.
 * 
 * - неожиданно победил Parallel в случае, когда памяти недостаточно для хранения мусора.
 * Все же тест больше на пропускную способность с маленьким heap-ом, а не на минимальное и управляемое время паузы 
 * при большом heap-е - на что больше ориентированы новые GC: максимальное время паузы G1GC - 24,37 мс; parallel - 33,457 мс
 * (среднее время parallel лучше, но если памяти выделить больше (но все равно - недостаточно) - G1 - выигрывает)  
 * Второй по результатам - G1    
 *
 * Вывод2: для типовой админки с ее небольшим heap-ом и числом одновременных пользователей и терпимостью к 
 * паузам, подойдет G1 (как наиболее сбалансированный) или - даже parallel;
 * для api, реализованного микросервисом - при большом heap-е и нагрузке - ZGC (какое-нибудь апи синхронизации контента в много потоков), 
 * при малых heap-е и нагрузке (для наших content-api это более вероятно) - G1GC.    
 *
 * 1. Epsilon
 * iterations: 1
Average operation time = 273.54927939269095
Min operation time = 273.54927939269095
Max operation time = 273.54927939269095
[2,178s][info   ][gc     ] Heap: 4096M reserved, 3194M (77,98%) committed, 3111M (75,96%) used
 * 
 * 2. Serial
 * 2.1. Enough memory
Average operation time = 552.8144949263752
Min operation time = 465.15411688410774
Max operation time = 626.557252231632
 * 2.2. Not enough memory
Average operation time = 571.9795228923476
Min operation time = 438.7991962310216
Max operation time = 669.9785973144121
 * 
 * 3. Parallel
 * 3.1. Enough memory
Average operation time = 587.4497147287709
Min operation time = 458.35870870527197
Max operation time = 627.2543343251698
 * 3.2. Not enough memory
Average operation time = 484.83059563210503
Min operation time = 346.7193587396048
Max operation time = 623.0605802243076
 *
 * 4. G1
 * 4.1. Enough memory
Average operation time = 491.22144478141445
Min operation time = 343.2314999618524
Max operation time = 643.8226184481575
 * 4.2. Not enough memory
Average operation time = 535.3486267376212
Min operation time = 430.913213931487
Max operation time = 630.3176325627527
 * 
 * 5. Shenandoah
 * 5.1. Enough memory
Average operation time = 386.59065630960555
Min operation time = 316.75815556572826
Max operation time = 438.4220416571298
 * 5.2. Not enough memory
Average operation time = 883.6347055504692
Min operation time = 535.4716140230411
Max operation time = 1377.8023647669183
 *  
 * 6. ZGC
 * 6.1. Enough memory
Average operation time = 237.98793036545356
Min operation time = 123.59568894483864
Max operation time = 606.7751514457923
 * 6.2. Not enough memory
Average operation time = 851.8143828641183
Min operation time = 623.3111211566338
Max operation time = 934.8106511787595
 * 
*/