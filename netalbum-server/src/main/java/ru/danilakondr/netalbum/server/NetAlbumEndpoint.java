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

import ru.danilakondr.netalbum.api.Request;
import ru.danilakondr.netalbum.api.Response;
import ru.danilakondr.netalbum.api.Status;
import ru.danilakondr.netalbum.db.Database;
import ru.danilakondr.netalbum.db.NetAlbumDAO;

@ServerEndpoint(
		value="/",
		decoders= {RequestDecoder.class},
		encoders= {ResponseEncoder.class})
public class NetAlbumEndpoint {
	private String sessionId = null;
	private NetAlbumDAO dao = null;
	
	@OnOpen
	public void onOpen(Session session) throws IOException {
		dao = new NetAlbumDAO();
		dao.setFactory(Database.getInstance().getSessionFactory());
	}
	
	@OnMessage
    public void onMessage(Session session, Request req) throws IOException, EncodeException {
		try {
			handleRequest(session, req);
		}
		catch (IllegalArgumentException e) {
			session.getBasicRemote().sendObject(Response.withMessage(Status.INVALID_ARGUMENT, e.toString()));
		}
    }
	
	private void handleRequest(Session session, Request req) throws IOException, EncodeException {
		switch (req.getMethod()) {
		case INIT_SESSION:
			handleInitSession(session, req);
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
			session.getBasicRemote().sendObject(Response.withMessage(Status.INVALID_METHOD, req.getMethod().name() + " (not implemented)"));
			break;
		}
	}

	private void handleSynchronize(Session session, Request req) throws EncodeException, IOException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		session.getBasicRemote().sendObject(Response.withMessage(Status.INVALID_REQUEST, "synchronization has not been implemented"));
	}

	private void handleGetDirectoryInfo(Session session) throws EncodeException, IOException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		session.getBasicRemote().sendObject(Response.directoryInfo("", 0));
	}

	private void handleDownloadContents(Session session) throws EncodeException, IOException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		Response resp = new Response(Status.SUCCESS);
		resp.setProperty("directoryContents", new ArrayList<>());
		session.getBasicRemote().sendObject(resp);
	}

	private void handleAddImages(Session session) throws IOException, EncodeException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		session.getBasicRemote().sendObject(Response.success());
	}

	private void handleCloseSession(Session session) throws IOException, EncodeException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		dao.removeSession(dao.getSession(sessionId));
		session.getBasicRemote().sendObject(Response.success());
		session.close();
	}

	private void handleDisconnectFromSession(Session session) throws IOException, EncodeException {
		if (sessionId == null)
			throw new RuntimeException("Client has not connected to a session");

		session.getBasicRemote().sendObject(Response.success());
		session.close();
	}

	private void handleInitSession(Session session, Request request) throws EncodeException, IOException {
		if (sessionId != null)
			throw new RuntimeException("Session already initiated");

		String dirName = (String) request.getProperties().get("directoryName");
		String id = SessionIdProvider.generateSessionId();
		Response resp = new Response(Status.SUCCESS);
		resp.setProperty("sessionId", id);
		session.getBasicRemote().sendObject(resp);
		this.sessionId = id;

		dao.initSession(id, dirName);
	}

	private void handleConnectToSession(Session session, Request req) throws EncodeException, IOException {
		Map<String, Object> contents = req.getProperties();
		if (!contents.containsKey("sessionId"))
			throw new IllegalArgumentException("session id has not been specified");

		Object oSessionId = contents.get("sessionId");
		if (oSessionId.getClass() != String.class)
			throw new IllegalArgumentException("session id is not a string");

		this.sessionId = (String)oSessionId;
		session.getBasicRemote().sendObject(Response.success());
	}


    @OnClose
    public void onClose(Session session) throws IOException {
        
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException, EncodeException {
    	/*
		if (throwable.getClass() == JsonbException.class) {
    		String msg = throwable.getMessage();
    		if (msg.contains("No enum constant")) {
    			String className = RequestType.class.getCanonicalName().replace(".", "\\.");
    			String absentConstant = msg.replaceAll(
    					"^.*: No enum constant " + className + "\\.(.*?)$", "$1");
    			
    			session.getBasicRemote().sendObject(Responses.invalidMethod(absentConstant));
    		}
    	}
    	else */session.getBasicRemote().sendObject(Response.withMessage(Status.EXCEPTION, throwable.toString()));
    }
}
