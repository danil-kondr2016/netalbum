package ru.danilakondr.netalbum.server;

import java.io.IOException;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import ru.danilakondr.netalbum.api.request.Request;

@ServerEndpoint(
		value="/",
		decoders= {GenericRequestDecoder.class},
		encoders= {GenericResponseEncoder.class})
public class Endpoint {
	@OnOpen
	public void onOpen(Session session) throws IOException {
		
	}
	
	@OnMessage
    public void onMessage(Session session, Request<?> req) throws IOException {
        // Handle new messages
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // WebSocket connection closes
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }
}
