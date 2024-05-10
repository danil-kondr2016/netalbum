package ru.danilakondr.netalbum.api.test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.danilakondr.netalbum.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RequestGenerateTest {
	private static final String TEST_DIRECTORY_NAME = "testDirectory";
	ObjectMapper mapper = new ObjectMapper();

	String objectToJson(Object o) throws JsonProcessingException {
		return mapper.writeValueAsString(o);
	}

	public <T> T jsonToObject(String json, Class<T> c) throws JsonProcessingException {
		return mapper.readValue(json, c);
	}
	
	@Test
	@DisplayName("Check single-argument request forming (initSession)")
	void initSession() throws JsonProcessingException {
		Request req = new Request(RequestType.INIT_SESSION);
		req.setProperty("directoryName", TEST_DIRECTORY_NAME);
		String x = objectToJson(req);
		Request req1 = jsonToObject(x, Request.class);

		assertEquals(String.format(Locale.ROOT, "{\"method\":\"INIT_SESSION\",\"directoryName\":\"%s\"}", TEST_DIRECTORY_NAME), x);
        assertSame(req1.getMethod(), RequestType.INIT_SESSION);
		assertEquals(TEST_DIRECTORY_NAME, req1.getProperties().get("directoryName"));
	}

	@Test
	@DisplayName("Check request without contents forming (closeSession)")
	void closeSession() throws JsonProcessingException {
		Request req = new Request(RequestType.CLOSE_SESSION);
		String x = objectToJson(req);
		
		assertEquals("{\"method\":\"CLOSE_SESSION\"}", x);
	}
	
	@Test
	@DisplayName("Check request with array of arguments (addImages)")
	void addSingleImage() throws JsonProcessingException {
		ImageData data = new ImageData();
		ArrayList<ImageData> list = new ArrayList<>();
		data.setFileName("test.raw");
		data.setFileSize(8);
		data.setWidth(1);
		data.setHeight(8);
		data.setThumbnail("THUMB1\r\n".getBytes(StandardCharsets.US_ASCII));
		list.add(data);
		
		Request req = new Request(RequestType.ADD_IMAGES);
		req.setProperty("images", list);
		String x = objectToJson(req);
		
		assertEquals("{\"method\":\"ADD_IMAGES\",\"images\":[{\"fileName\":\"test.raw\",\"fileSize\":8,\"width\":1,\"height\":8,\"thumbnail\":\"VEhVTUIxDQo=\"}]}", x);

		Request req1 = jsonToObject(x, Request.class);
		assertSame(req1.getMethod(), RequestType.ADD_IMAGES);

		Map<String, Object> contents = req1.getProperties();
		assertTrue(contents.containsKey("images"));
		assertInstanceOf(List.class, contents.get("images"));

		List<Object> images = (List<Object>) contents.get("images");
        assertFalse(images.isEmpty());
		assertInstanceOf(Map.class, images.get(0));

		Map<String, Object> image = (Map<String, Object>) images.get(0);
		assertEquals("test.raw", image.get("fileName"));
		assertEquals(8, image.get("fileSize"));
		assertEquals(1, image.get("width"));
		assertEquals(8, image.get("height"));
		assertEquals("VEhVTUIxDQo=", image.get("thumbnail"));
	}
	
	@Test
	@DisplayName("Check request with nullable fields (synchronize)")
	void synchronize() throws JsonProcessingException {
		Change first = new Change();
		first.setOldName("test1.png");
		first.setNewName("test/test1.png");
		
		Change second = new Change();
		second.setOldName("test2.png");
		second.setNewName(null);
		
		List<Change> changes = new ArrayList<>();
		changes.add(first);
		changes.add(second);

		Request req = new Request(RequestType.SYNCHRONIZE);
		req.setProperty("changes", changes);
		String x = objectToJson(req);
		
		assertEquals("{\"method\":\"SYNCHRONIZE\",\"changes\":[{\"oldName\":\"test1.png\",\"newName\":\"test/test1.png\"},{\"oldName\":\"test2.png\",\"newName\":null}]}", x);
	}
}
