package com.example.demo.web.mvc;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.record.Test2;


@RestController
@RequestMapping("/testmvc")
public class TestController {

	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public Test2 testRecordInRequest(@RequestParam Integer age) {
		return new Test2("Tets name", age);
	} 
	
}
