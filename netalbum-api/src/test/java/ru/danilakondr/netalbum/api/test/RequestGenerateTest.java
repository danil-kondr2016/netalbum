package ru.danilakondr.netalbum.api.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.json.bind.*;
import jakarta.json.bind.config.BinaryDataStrategy;
import jakarta.json.bind.config.PropertyOrderStrategy;
import ru.danilakondr.netalbum.api.*;
import ru.danilakondr.netalbum.api.request.*;

public class RequestGenerateTest {
	private static final JsonbConfig JSONB_CONFIG;
	
	private static final String TEST_DIRECTORY_NAME = "testDirectory";
	
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
	@DisplayName("Check single-argument request forming (initSession)")
	void initSession() {
		Request<DirectoryName> req = Requests.initSession(TEST_DIRECTORY_NAME);
		String x = objectToJson(req);

		assertEquals(String.format(Locale.ROOT, "{\"contents\":{\"directoryName\":\"%s\"},\"method\":\"initSession\"}", TEST_DIRECTORY_NAME), x);
	}

	@Test
	@DisplayName("Check request without contents forming (closeSession)")
	void closeSession() {
		Request<Void> req = Requests.closeSession();
		String x = objectToJson(req);
		
		assertEquals("{\"method\":\"closeSession\"}", x);
	}
	
	@Test
	@DisplayName("Check request with array of arguments (addImages)")
	void addSingleImage() {
		ImageData data = new ImageData();
		data.setFileName("test.raw");
		data.setFileSize(8);
		data.setWidth(1);
		data.setHeight(8);
		data.setThumbnail("THUMB1\r\n".getBytes(StandardCharsets.US_ASCII));
		
		Request<AddImages> req = Requests.addSingleImage(data);
		String x = objectToJson(req);
		
		assertEquals("{\"contents\":{\"images\":[{\"fileName\":\"test.raw\",\"fileSize\":8,\"height\":8,\"width\":1,\"thumbnail\":\"VEhVTUIxDQo=\"}]},\"method\":\"addImages\"}", x);
	}
}
