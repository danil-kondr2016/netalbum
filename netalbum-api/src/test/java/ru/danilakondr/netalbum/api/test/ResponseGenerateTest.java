package ru.danilakondr.netalbum.api.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.danilakondr.netalbum.api.message.Response;

public class ResponseGenerateTest {
	String objectToJson(Object o) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(o);
	}
	@Test
	@DisplayName("Check success response with empty body")
	void success() throws JsonProcessingException {
		Response resp = new Response(Response.Type.SUCCESS);
		String x = objectToJson(resp);
		
		assertEquals("{\"type\":\"RESPONSE\",\"answer\":\"SUCCESS\"}", x);
	}
	
	@Test
	@DisplayName("Check error message response with some reason")
	void sqlError() throws JsonProcessingException {
		Response resp = new Response(Response.Type.ERROR);
		resp.setProperty("message", "Database has not been designed");
		String x = objectToJson(resp);
		
		assertEquals("{\"type\":\"RESPONSE\",\"answer\":\"ERROR\",\"message\":\"Database has not been designed\"}", x);
	}
	
	@Test
	@DisplayName("Check success response with some body (directoryInfo)")
	void directoryInfo() throws JsonProcessingException {
		Response resp = new Response(Response.Type.DIRECTORY_INFO);
		resp.setProperty("directoryName", "testDirectory");
		resp.setProperty("directorySize", 8);

		String x = objectToJson(resp);
		
		assertTrue(x.contains("\"directoryName\":\"testDirectory\""));
		assertTrue(x.contains("\"directorySize\":8"));
	}

	@Test
	@DisplayName("Check response with byte array")
	void thumbnails() throws JsonProcessingException {
		Response resp = new Response(Response.Type.THUMBNAILS_ARCHIVE);
		resp.setProperty("thumbnailsZip", new byte[]{'T', 'H', 'U', 'M', 'B', '1', '\r', '\n'});

		String x = objectToJson(resp);
		assertEquals("{\"type\":\"RESPONSE\",\"answer\":\"THUMBNAILS_ARCHIVE\",\"thumbnailsZip\":\"VEhVTUIxDQo=\"}", x);
	}
}
