package ru.danilakondr.netalbum.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import jakarta.json.bind.JsonbException;
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
			handleInitSession(session);
			break;
		case CONNECT_TO_SESSION:
			handleConnectToSession(session, req);
			break;
		case DISCONNECT_FROM_SESSION:
			handleDisconnectFromSession(session);
			break;
		case CLOSE_SESSION:
			handleCloseSession(session);
			break;
		case ADD_IMAGES:
			handleAddImages(session);
			break;
		case DOWNLOAD_CONTENTS:
			handleDownloadContents(session);
			break;
		case GET_DIRECTORY_INFO:
			handleGetDirectoryInfo(session);
			break;
		case SYNCHRONIZE:
			handleSynchronize(session, req);
			break;
		default:
			session.getBasicRemote().sendObject(Responses.invalidMethod(req.getMethod().name() + " (not implemented)"));
			break;
		}
	}

	private void handleSynchronize(Session session, Request<Map<String, Object>> req) throws EncodeException, IOException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		session.getBasicRemote().sendObject(Responses.invalidRequest("synchronization has not been implemented"));
	}

	private void handleGetDirectoryInfo(Session session) throws EncodeException, IOException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		session.getBasicRemote().sendObject(Responses.directoryInfo("", 0));
	}

	private void handleDownloadContents(Session session) throws EncodeException, IOException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		session.getBasicRemote().sendObject(Responses.directoryContents(new ArrayList<>()));
	}

	private void handleAddImages(Session session) throws IOException, EncodeException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		session.getBasicRemote().sendObject(Responses.success());
	}

	private void handleCloseSession(Session session) throws IOException, EncodeException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		session.getBasicRemote().sendObject(Responses.success());
		session.close();
	}

	private void handleDisconnectFromSession(Session session) throws IOException, EncodeException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		session.getBasicRemote().sendObject(Responses.success());
		session.close();
	}

	private void handleInitSession(Session session) throws EncodeException, IOException {
		if (sessionId != null)
			throw new RuntimeException("Session already initiated");

		String id = SessionIdProvider.generateSessionId();
		Response<?> resp = Responses.sessionId(id);
		session.getBasicRemote().sendObject(resp);
		this.sessionId = id;
	}

	private void handleConnectToSession(Session session, Request<Map<String,Object>> req) throws EncodeException, IOException {
		Map<String, Object> contents = req.getContents();
		if (!contents.containsKey("sessionId"))
			throw new IllegalArgumentException("session id has not been specified");

		Object oSessionId = contents.get("sessionId");
		if (oSessionId.getClass() != String.class)
			throw new IllegalArgumentException("session id is not a string");

		this.sessionId = (String)oSessionId;
		session.getBasicRemote().sendObject(Responses.success());
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
