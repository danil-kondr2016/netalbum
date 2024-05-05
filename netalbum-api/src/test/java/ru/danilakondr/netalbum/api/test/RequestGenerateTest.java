package ru.danilakondr.netalbum.api.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.List;
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

		assertEquals(String.format(Locale.ROOT, "{\"method\":\"initSession\",\"contents\":{\"directoryName\":\"%s\"}}", TEST_DIRECTORY_NAME), x);
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
		
		assertEquals("{\"method\":\"addImages\",\"contents\":{\"images\":[{\"fileName\":\"test.raw\",\"fileSize\":8,\"height\":8,\"width\":1,\"thumbnail\":\"VEhVTUIxDQo=\"}]}}", x);
	}
	
	@Test
	@DisplayName("Check request with nullable fields (synchronize)")
	void synchronize() {
		Change first = new Change();
		first.setOldName("test1.png");
		first.setNewName("test/test1.png");
		
		Change second = new Change();
		second.setOldName("test2.png");
		second.setNewName(null);
		
		List<Change> changes = List.of(first, second);
		Request<Synchronize> req = Requests.synchronize(changes);
		String x = objectToJson(req);
		
		assertEquals("{\"method\":\"synchronize\",\"contents\":{\"changes\":[{\"oldName\":\"test1.png\",\"newName\":\"test/test1.png\"},{\"oldName\":\"test2.png\",\"newName\":null}]}}", x);
	}
}
