package ru.danilakondr.netalbum.server;

import java.io.IOException;
import java.io.Writer;

import javax.websocket.EncodeException;
import javax.websocket.Encoder.TextStream;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.BinaryDataStrategy;

import javax.websocket.EndpointConfig;

import ru.danilakondr.netalbum.api.response.Response;

public class GenericResponseEncoder implements TextStream<Response<?>> {
	private Jsonb json;

	@Override
	public void init(EndpointConfig config) {
		JsonbConfig cfg = new JsonbConfig()
				.withBinaryDataStrategy(BinaryDataStrategy.BASE_64)
				;
		json = JsonbBuilder.create(cfg);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void encode(Response<?> object, Writer writer) throws EncodeException, IOException {
		json.toJson(object, writer);
	}

}
