package ru.danilakondr.netalbum.api.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import jakarta.json.bind.*;
import jakarta.json.bind.config.PropertyOrderStrategy;
import ru.danilakondr.netalbum.api.*;
import ru.danilakondr.netalbum.api.request.*;

public class RequestGenerateTest {
	private static final JsonbConfig JSONB_CONFIG;
	
	private static final String TEST_DIRECTORY_NAME = "testDirectory";
	private static final String TEST_SESSION_ID = "0123456789012345678901234567890123456789";
	
	static {
		JSONB_CONFIG = new JsonbConfig()
				.withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL)
				;
	}
	
	String objectToJson(Object o) {
		Jsonb b = JsonbBuilder.create(JSONB_CONFIG);
		return b.toJson(o);
	}
	
	@Test
	void initSession() {
		Request<DirectoryName> req = Requests.initSession(TEST_DIRECTORY_NAME);
		String x = objectToJson(req);

		assertEquals(x, String.format(Locale.ROOT, "{\"contents\":{\"directoryName\":\"%s\"},\"method\":\"initSession\"}", TEST_DIRECTORY_NAME));
	}
	
	@Test
	void connectToSession() {
		Request<SessionId> req = Requests.connectToSession(TEST_SESSION_ID);
		String x = objectToJson(req);
		
		assertEquals(x, String.format(Locale.ROOT, "{\"contents\":{\"sessionId\":\"%s\"},\"method\":\"connectToSession\"}", TEST_SESSION_ID));
	}
}
