package ru.danilakondr.netalbum.server;

import java.io.IOException;
import java.io.Writer;

import javax.websocket.Encoder.TextStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.danilakondr.netalbum.api.Response;

import javax.websocket.EndpointConfig;

public class ResponseEncoder implements TextStream<Response> {
	private ObjectMapper mapper;
	@Override
	public void init(EndpointConfig config) {
		mapper = new ObjectMapper();
	}

	@Override
	public void destroy() {

	}

	@Override
	public void encode(Response response, Writer writer) throws IOException {
		mapper.writeValue(writer, response);
	}

}
