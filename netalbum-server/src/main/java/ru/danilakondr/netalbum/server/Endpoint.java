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

import ru.danilakondr.netalbum.api.SessionId;
import ru.danilakondr.netalbum.api.request.Request;
import ru.danilakondr.netalbum.api.response.Response;
import ru.danilakondr.netalbum.api.response.Responses;

@ServerEndpoint(
		value="/",
		decoders= {GenericRequestDecoder.class},
		encoders= {GenericResponseEncoder.class})
public class Endpoint {
	private String sessionId;
	
	@OnOpen
	public void onOpen(Session session) throws IOException {
		
	}
	
	@OnMessage
    public void onMessage(Session session, Request<?> req) throws IOException, EncodeException {
		switch (req.getMethod()) {
		case INIT_SESSION:
			String id = SessionIdProvider.generateSessionId();
			Response<?> resp = Responses.sessionId(id);
			session.getBasicRemote().sendObject(resp);
			this.sessionId = id;
			
			break;
		case CONNECT_TO_SESSION:
			Request<SessionId> req_sessionId = (Request<SessionId>)req;
			this.sessionId = req_sessionId.getContents().getSessionId();
			break;
		case DISCONNECT_FROM_SESSION:
			session.getBasicRemote().sendObject(Responses.success());
			session.close();
			break;
		case CLOSE_SESSION:
			session.getBasicRemote().sendObject(Responses.success());
			session.close();
			break;
		case ADD_IMAGES:
			session.getBasicRemote().sendObject(Responses.success());
			break;
		case DOWNLOAD_CONTENTS:
			break;
		case GET_DIRECTORY_INFO:
			break;
		case SYNCHRONIZE:
			break;
		default:
			break;
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
