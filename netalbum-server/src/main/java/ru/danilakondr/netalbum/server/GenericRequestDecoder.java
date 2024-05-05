package ru.danilakondr.netalbum.server;

import java.io.IOException;
import java.io.Reader;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.BinaryDataStrategy;
import ru.danilakondr.netalbum.api.request.Request;

public class GenericRequestDecoder implements Decoder.TextStream<Request<?>> {
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
		try {
			json.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	@Override
	public Request<?> decode(Reader reader) throws DecodeException, IOException {
		Request<?> object = json.fromJson(reader, Request.class);
		return object;
	}

}
