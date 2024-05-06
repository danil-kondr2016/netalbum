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

import jakarta.json.bind.JsonbException;
import ru.danilakondr.netalbum.api.SessionId;
import ru.danilakondr.netalbum.api.request.Request;
import ru.danilakondr.netalbum.api.request.RequestType;
import ru.danilakondr.netalbum.api.response.Response;
import ru.danilakondr.netalbum.api.response.Responses;

@ServerEndpoint(
		value="/",
		decoders= {GenericRequestDecoder.class},
		encoders= {GenericResponseEncoder.class})
public class Endpoint {
	private String sessionId = null;
	
	@OnOpen
	public void onOpen(Session session) throws IOException {
		
	}
	
	@OnMessage
    public void onMessage(Session session, Request<Map<String, Object>> req) throws IOException, EncodeException {
		try {
			handleRequest(session, req);
		}
		catch (IllegalArgumentException e) {
			session.getBasicRemote().sendObject(Responses.invalidArgument(e.getMessage()));
		}
		catch (RuntimeException e) {
			session.getBasicRemote().sendObject(Responses.invalidRequest(e.getMessage()));
		}
    }
	
	private void handleRequest(Session session, Request<Map<String, Object>> req) throws IOException, EncodeException {
		switch (req.getMethod()) {
		case INIT_SESSION:
			if (sessionId != null)
				throw new RuntimeException("Session already initiated");
			
			String id = SessionIdProvider.generateSessionId();
			Response<?> resp = Responses.sessionId(id);
			session.getBasicRemote().sendObject(resp);
			this.sessionId = id;
			
			break;
		case CONNECT_TO_SESSION:
			Map<String, Object> contents = req.getContents();
			if (!contents.containsKey("sessionId"))
				throw new IllegalArgumentException("session id has not been specified");
			
			Object oSessionId = contents.get("sessionId");
			if (oSessionId.getClass() != String.class)
				throw new IllegalArgumentException("session id is not a string");
			
			this.sessionId = (String)oSessionId;
			session.getBasicRemote().sendObject(Responses.success());
			break;
		case DISCONNECT_FROM_SESSION:
			if (sessionId == null)
				throw new RuntimeException("Client has not connected to a session");
			
			session.getBasicRemote().sendObject(Responses.success());
			session.close();
			break;
		case CLOSE_SESSION:
			if (sessionId == null)
				throw new RuntimeException("Client has not connected to a session");
			
			session.getBasicRemote().sendObject(Responses.success());
			session.close();
			break;
		case ADD_IMAGES:
			if (sessionId == null)
				throw new RuntimeException("Client has not connected to a session");
			
			session.getBasicRemote().sendObject(Responses.success());
			break;
		case DOWNLOAD_CONTENTS:
			if (sessionId == null)
				throw new RuntimeException("Client has not connected to a session");
			
			break;
		case GET_DIRECTORY_INFO:
			if (sessionId == null)
				throw new RuntimeException("Client has not connected to a session");
			
			break;
		case SYNCHRONIZE:
			if (sessionId == null)
				throw new RuntimeException("Client has not connected to a session");
			
			break;
		default:
			break;
		}
	}

    @OnClose
    public void onClose(Session session) throws IOException {
        
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException, EncodeException {
    	if (throwable.getClass() == JsonbException.class) {
    		String msg = throwable.getMessage();
    		if (msg.contains("No enum constant")) {
    			String className = RequestType.class.getCanonicalName().replace(".", "\\.");
    			String absentConstant = msg.replaceAll(
    					"^.*: No enum constant " + className + "\\.(.*?)$", "$1");
    			
    			session.getBasicRemote().sendObject(Responses.invalidMethod(absentConstant));
    		}
    	}
    	else session.getBasicRemote().sendObject(Responses.exception(throwable));
    }
}
