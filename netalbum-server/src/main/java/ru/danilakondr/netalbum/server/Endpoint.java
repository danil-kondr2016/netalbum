package ru.danilakondr.netalbum.server;

import java.io.IOException;
import java.util.Map;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import ru.danilakondr.netalbum.api.request.Request;
import ru.danilakondr.netalbum.api.response.Responses;

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
		try {
			session.getBasicRemote().sendObject(Responses.message("sorry, but server has not been implemented"));
		} catch (IOException e) {
			e.printStackTrace();
			session.getBasicRemote().sendText("thrown "+e);
		} catch (EncodeException e) {
			e.printStackTrace();
			session.getBasicRemote().sendText("thrown "+e);
		}
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
    	try {
			session.getBasicRemote().sendText("error "+throwable);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
