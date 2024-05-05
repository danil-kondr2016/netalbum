package ru.danilakondr.netalbum.api.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.bind.config.PropertyOrderStrategy;
import ru.danilakondr.netalbum.api.response.Response;
import ru.danilakondr.netalbum.api.response.Responses;

public class ResponseGenerateTest {
	private static final JsonbConfig JSONB_CONFIG;
	
	static {
		JSONB_CONFIG = new JsonbConfig()
				.withBinaryDataStrategy(BinaryDataStrategy.BASE_64)
				.withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL)
				;
	}
	
	String objectToJson(Object o) {
		Jsonb b = JsonbBuilder.create(JSONB_CONFIG);
		return b.toJson(o);
	}
	
	@Test
	@DisplayName("Check success response with empty body")
	void success() {
		Response<Void> resp = Responses.success();
		String x = objectToJson(resp);
		
		assertEquals("{\"status\":{\"id\":\"SUCCESS\",\"message\":\"Success\"}}", x);
	}
	
	@Test
	@DisplayName("Check error message response with some reason")
	void sqlError() {
		Response<Void> resp = Responses.sqlError("Database has not been designed");
		String x = objectToJson(resp);
		
		assertEquals("{\"status\":{\"id\":\"SQL_ERROR\",\"message\":\"SQL request error: Database has not been designed\"}}", x);
	}
}
