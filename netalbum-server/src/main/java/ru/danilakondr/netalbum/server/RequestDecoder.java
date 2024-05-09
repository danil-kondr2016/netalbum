package ru.danilakondr.netalbum.server;

import java.io.IOException;
import java.io.Reader;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.danilakondr.netalbum.api.Request;

public class RequestDecoder implements Decoder.TextStream<Request> {
	private ObjectMapper mapper;
	@Override
	public void init(EndpointConfig config) {
		mapper = new ObjectMapper();
	}

	@Override
	public void destroy() {

	}

	@Override
	public Request decode(Reader reader) throws IOException {
		Request request = mapper.readValue(reader, Request.class);
		return request;
	}

}
