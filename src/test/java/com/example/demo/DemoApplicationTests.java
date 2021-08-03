package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.record.Test2;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
		var test = new Test2("1", 18);
	}

}
