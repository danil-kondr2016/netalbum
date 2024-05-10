package ru.danilakondr.netalbum.api.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.danilakondr.netalbum.api.response.Response;
import ru.danilakondr.netalbum.api.response.Status;

public class ResponseGenerateTest {
	String objectToJson(Object o) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(o);
	}
	@Test
	@DisplayName("Check success response with empty body")
	void success() throws JsonProcessingException {
		Response resp = new Response(Status.SUCCESS);
		String x = objectToJson(resp);
		
		assertEquals("{\"status\":\"SUCCESS\"}", x);
	}
	
	@Test
	@DisplayName("Check error message response with some reason")
	void sqlError() throws JsonProcessingException {
		Response resp = new Response(Status.ERROR);
		resp.setProperty("message", "Database has not been designed");
		String x = objectToJson(resp);
		
		assertEquals("{\"status\":\"ERROR\",\"message\":\"Database has not been designed\"}", x);
	}
	
	@Test
	@DisplayName("Check success response with some body (directoryInfo)")
	void directoryInfo() throws JsonProcessingException {
		Response resp = new Response(Status.SUCCESS);
		resp.setProperty("directoryName", "testDirectory");
		resp.setProperty("directorySize", 8);

		String x = objectToJson(resp);
		
		assertTrue(x.contains("\"directoryName\":\"testDirectory\""));
		assertTrue(x.contains("\"directorySize\":8"));
	}
}
