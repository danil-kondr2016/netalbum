package ru.danilakondr.netalbum.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.danilakondr.netalbum.api.*;
import ru.danilakondr.netalbum.server.SessionIdProvider;
import ru.danilakondr.netalbum.server.db.NetAlbumService;
import ru.danilakondr.netalbum.server.model.NetAlbumSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class NetAlbumHandler extends TextWebSocketHandler {
    private String sessionId;
    private NetAlbumService service;
    private boolean initiator = false;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, WebSocketSession> initiators = new HashMap<>();
    private static final Map<WebSocketSession, String> connected = new HashMap<>();

    @Autowired
    public void setService(NetAlbumService service) {
        this.service = service;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Connection established");
        session.sendMessage(new TextMessage("Hello, world!"));
        session.sendMessage(new TextMessage(session.getAttributes().toString()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload();

        Request req = mapper.readValue(msg, Request.class);

        switch (req.getMethod()) {
            case INIT_SESSION:
                handleInitSession(session, req);
                break;
            case RESTORE_SESSION:
                handleRestoreSession(session, req);
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
                AddImagesRequest req1 = mapper.readValue(msg, AddImagesRequest.class);
                handleAddImages(session, req1);
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
                sendResponse(session, Response.withMessage(Status.INVALID_METHOD,
                        req.getMethod().name() + " (not implemented)"));
                break;
        }
    }

    private void handleRestoreSession(WebSocketSession session, Request req) throws IOException {
        Map<String, Object> props = req.getProperties();
        if (!props.containsKey("sessionId"))
            throw new IllegalArgumentException("session id has not been specified");

        String id = (String)props.get("sessionId");
        NetAlbumSession s = service.getSession(id);
        if (s == null)
            throw new IllegalArgumentException("non-existent session: " + id);

        this.sessionId = id;
        this.initiator = true;
        sendResponse(session, Response.success());
    }

    private void handleSynchronize(WebSocketSession session, Request req) throws IOException {
        if (sessionId == null)
            throw new IllegalArgumentException("client has not been connected to session");

        if (!initiator)
            throw new IllegalArgumentException("you cannot load images in session initiated by not you");

        sendResponse(session, Response.withMessage(Status.INVALID_REQUEST, "not implemented"));
    }

    private void handleGetDirectoryInfo(WebSocketSession session) throws IOException {
        if (sessionId == null)
            throw new IllegalArgumentException("client has not been connected to session");

        NetAlbumSession s = service.getSession(sessionId);
        Response r = Response.directoryInfo(s.getDirectoryName(), 0);
        sendResponse(session, r);
    }

    private void handleDownloadContents(WebSocketSession session) throws IOException {
        if (sessionId == null)
            throw new IllegalArgumentException("client has not been connected to session");

        sendResponse(session, Response.withMessage(Status.INVALID_REQUEST, "not implemented"));
    }

    private void handleAddImages(WebSocketSession session, AddImagesRequest req) throws IOException {
        if (sessionId == null)
            throw new IllegalArgumentException("client has not been connected to session");

        if (!initiator)
            throw new IllegalArgumentException("you cannot load images in session initiated by not you");

        List<ImageData> images = req.getImages();
        for (ImageData image : images) {
            service.putImage(sessionId, image);
        }

        sendResponse(session, Response.success());
    }

    private void handleCloseSession(WebSocketSession session) throws IOException {
        if (sessionId == null)
            throw new IllegalArgumentException("client has not been connected to session");

        if (!initiator)
            throw new IllegalArgumentException("you cannot close session which not initiated by you");

        initiators.remove(sessionId);
        connected.remove(session);

        for (WebSocketSession s: connected.keySet()) {
            if (Objects.equals(sessionId, connected.get(s))) {
                sendResponse(s, Response.quit());
                s.close();
            }
        }

        service.removeSession(sessionId);
        sendResponse(session, Response.success());
        session.close();
    }

    private void handleDisconnectFromSession(WebSocketSession session) throws IOException {
        if (sessionId == null)
            throw new IllegalArgumentException("client has not been connected to session");

        if (initiator)
            initiators.remove(sessionId);
        connected.remove(session);
        sendResponse(session, Response.success());
        session.close();
    }

    private void handleConnectToSession(WebSocketSession session, Request req) throws IOException {
        if (sessionId != null)
            throw new IllegalArgumentException("client has been connected to session");

        Map<String, Object> props = req.getProperties();
        if (!props.containsKey("sessionId"))
            throw new IllegalArgumentException("session id has not been specified");

        String id = (String)props.get("sessionId");
        NetAlbumSession s = service.getSession(id);
        if (s == null)
            throw new IllegalArgumentException("non-existent session: " + id);

        this.sessionId = id;
        this.initiator = false;
        connected.put(session, id);
        sendResponse(session, Response.success());
    }

    private void sendResponse(WebSocketSession session, Response response) throws IOException {
        String str = mapper.writeValueAsString(response);
        TextMessage msg = new TextMessage(str);
        session.sendMessage(msg);
    }

    private void handleInitSession(WebSocketSession session, Request req) throws IOException {
        if (sessionId != null)
            throw new IllegalArgumentException("client has been connected to session");

        Map<String, Object> props = req.getProperties();
        if (!props.containsKey("directoryName"))
            throw new IllegalArgumentException("directory name has not been specified");

        sessionId = SessionIdProvider.generateSessionId();
        String directoryName = (String) props.get("directoryName");
        service.initSession(sessionId, directoryName);
        initiator = true;
        initiators.put(sessionId, session);
        connected.put(session, sessionId);

        Response response = new Response(Status.SUCCESS);
        response.setProperty("sessionId", sessionId);
        sendResponse(session, response);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace(System.out);
    }
}
