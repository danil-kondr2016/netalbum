package ru.danilakondr.netalbum.api.test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.danilakondr.netalbum.api.data.Change;
import ru.danilakondr.netalbum.api.data.ImageData;
import ru.danilakondr.netalbum.api.message.Request;

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
	@DisplayName("Check single-argument message forming (initSession)")
	void initSession() throws JsonProcessingException {
		Request req = new Request(Request.Method.INIT_SESSION);
		req.setProperty("directoryName", TEST_DIRECTORY_NAME);
		String x = objectToJson(req);
		Request req1 = jsonToObject(x, Request.class);

		assertEquals(String.format(Locale.ROOT, "{\"type\":\"REQUEST\",\"method\":\"INIT_SESSION\",\"directoryName\":\"%s\"}", TEST_DIRECTORY_NAME), x);
                assertSame(req1.getMethod(), Request.Method.INIT_SESSION);

		Request.InitSession req2 = (Request.InitSession)req1;
		assertEquals(TEST_DIRECTORY_NAME, req2.getDirectoryName());
	}

	@Test
	@DisplayName("Check message without contents forming (closeSession)")
	void closeSession() throws JsonProcessingException {
		Request req = new Request(Request.Method.CLOSE_SESSION);
		String x = objectToJson(req);
		
		assertEquals("{\"type\":\"REQUEST\",\"method\":\"CLOSE_SESSION\"}", x);
	}
	
	@Test
	@DisplayName("Check message with array of arguments (addImages)")
	void addSingleImage() throws JsonProcessingException {
		ImageData original = new ImageData();
		original.setFileName("test.raw");
		original.setFileSize(8);
		original.setWidth(1);
		original.setHeight(8);
		original.setThumbnail("THUMB1\r\n".getBytes(StandardCharsets.US_ASCII));

		Request.AddImage req = new Request.AddImage();
		req.setImage(original);
		String x = objectToJson(req);
		
		assertEquals("{\"type\":\"REQUEST\",\"method\":\"ADD_IMAGE\",\"image\":{\"fileName\":\"test.raw\",\"fileSize\":8,\"width\":1,\"height\":8,\"thumbnail\":\"VEhVTUIxDQo=\"}}", x);

		Request req1 = jsonToObject(x, Request.class);
		assertSame(req1.getMethod(), Request.Method.ADD_IMAGE);
		assertSame(req1.getClass(), Request.AddImage.class);

		Request.AddImage req2 = (Request.AddImage) req1;
		ImageData received = req2.getImage();
                assertNotNull(received);

		assertEquals(original.getFileName(), received.getFileName());
		assertEquals(original.getFileSize(), received.getFileSize());
		assertEquals(original.getWidth(), received.getWidth());
		assertEquals(original.getHeight(), received.getHeight());
		assertArrayEquals(original.getThumbnail(), received.getThumbnail());
	}
	
	@Test
	@DisplayName("Check message with nullable fields (synchronize)")
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

		Request req = new Request(Request.Method.SYNCHRONIZE);
		req.setProperty("changes", changes);
		String x = objectToJson(req);
		
		assertEquals("{\"type\":\"REQUEST\",\"method\":\"SYNCHRONIZE\",\"changes\":[{\"oldName\":\"test1.png\",\"newName\":\"test/test1.png\"},{\"oldName\":\"test2.png\",\"newName\":null}]}", x);
	}
}
