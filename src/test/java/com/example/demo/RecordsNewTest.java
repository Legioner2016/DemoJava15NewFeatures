package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.record.Test2;
import com.example.demo.record.Test3;
import com.example.demo.record.Test4;
import com.example.demo.record.TestWithRecord;
import com.example.demo.record.UserEntity;
import com.example.demo.record.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

/**
 * These records for me seems similar to kotlin data classes more, than c++ record ...
 * 
 * @author legioner
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class RecordsNewTest {

  @TestConfiguration
  static class MyTestConfiguration implements WebMvcConfigurer {

  	@Bean
  	public RestTemplate restTemplate() {
  		return new RestTemplate();
  	} 
  	
      @Override
      public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
          MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

          converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));        
          messageConverters.add(converter);
          restTemplate().setMessageConverters(messageConverters); 
      }
      

  }

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	UserRepository	repository;
	
	@Autowired
    EntityManager entityManager;
  
	
	@Test
	public void test() {
		var test2 = new Test2("some name", 45);
		record inMethodRecord(Integer id) {};
		var test3 = new inMethodRecord(8);
		assertEquals(45, test2.age());
		assertEquals(8, test3.id());
		assertThrows(IllegalArgumentException.class, () -> new Test2(null, -5));
		var test4 = new Test2(null, 5);
		assertNull(test4.name());
		var test5 = new Test3("first", "last", 1);
		assertEquals("first last", test5.fullName());
		var test6 = new Test4();
		test6.setInnerProperty(17);
		var test7 = new Test4(15);
		test7.setInnerProperty(7);
		assertEquals(7, test6.getInnerProperty());
		assertEquals(7, test7.getInnerProperty());
		assertEquals(0, test6.id());
		assertEquals(15, test7.id());
		var testObj = new TestWithRecord();
		testObj.setUserData(test5);
		assertEquals(test5, testObj.getUserData());
		assertEquals(test5, testObj.getUserData());
		assertEquals("1 first first last", testObj.test());
	}
	
	@Test
	public void testReflect() {
		var testRecord = new Test3("first", "last", 1);
		var testObj = new TestWithRecord();
		testObj.setUserData(testRecord);

		RecordComponent[] recordFileds = Test3.class.getRecordComponents();
		assertEquals(3, recordFileds.length);
		
		Field[] allFields = TestWithRecord.class.getDeclaredFields();
		allFields[0].setAccessible(true);
		try {
			assertEquals(testRecord, allFields[0].get(testObj));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			assertFalse(e != null);
			e.printStackTrace();
		}
		
		assertTrue(Test3.class.isRecord());
		assertTrue(testRecord.getClass().isRecord());
		assertEquals(testRecord.getClass(), allFields[0].getType());
		assertTrue(allFields[0].getType().isRecord());

		
	}
	
	//Don't work - No default constructor, no setter for id, etc...
	@Test
	public void testUserRecordInJpa() {
		var saved = new UserEntity(2, "test");
		//repository.save(saved);
		assertThrows(JpaSystemException.class, () -> {
			repository.save(saved);
		});
		//Integer id = saved.id();
		//var loaded = repository.findById(id);
		//assertEquals(saved, loaded);

		//var loaded2 = entityManager.find(UserEntity.class, 1);
		//assertEquals("Test", loaded2.login());
		assertThrows(PersistenceException.class, () -> {
			entityManager.find(UserEntity.class, 1);
		});
	}
	
	
	
	
	//@Test
	//Don't work - 2021-07-30 16:07:08.126  WARN 41966 --- [nio-8080-exec-1] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.http.converter.HttpMessageNotWritableException: No converter for [class com.example.demo.record.Test2] with preset Content-Type 'null']
	//https://adambien.blog/roller/abien/entry/java_14_java_record_json
	public void testUseRecordInRest() {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<Test2> response = null;
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
			      .scheme("http").host("127.0.0.1:8080").path("/testmvc/").queryParam("age", 5).build();
		response = restTemplate.getForEntity(uriComponents.toUriString(), Test2.class);
		Test2 result = response.getBody();
		assertEquals("Tets name", result.name());
		assertEquals(5, result.age());
		
	}
	
	//So - for now we have to not use jackson; or define custom serialization / deserialization
	//In web application, i think, use records would be great possibility 
	@Test
	public void testJson() {
		var testRecord = new Test3("first", "last", 1);
		var testObj = new TestWithRecord();
		testObj.setUserData(testRecord);
		ObjectMapper mapper = new ObjectMapper();
		assertThrows(InvalidDefinitionException.class, () -> {
			mapper.writeValue(System.out, testObj);
		});
		assertThrows(InvalidDefinitionException.class, () -> {
			mapper.writeValue(System.out, testRecord);
		});
	}
	
	
	
}
