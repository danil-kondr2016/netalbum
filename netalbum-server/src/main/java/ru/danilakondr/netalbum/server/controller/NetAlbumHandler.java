package ru.danilakondr.netalbum.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.danilakondr.netalbum.api.Request;
import ru.danilakondr.netalbum.api.Response;
import ru.danilakondr.netalbum.api.Status;
import ru.danilakondr.netalbum.server.SessionIdProvider;
import ru.danilakondr.netalbum.server.db.NetAlbumService;

import java.io.IOException;
import java.util.Map;

@Service
public class NetAlbumHandler extends TextWebSocketHandler {
    private String sessionId;
    private NetAlbumService service;

    @Autowired
    public void setService(NetAlbumService service) {
        this.service = service;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Connection established");
        session.sendMessage(new TextMessage("Hello, world!"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload();
        ObjectMapper mapper = new ObjectMapper();
        Request req = mapper.readValue(msg, Request.class);

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
                sendResponse(session, Response.withMessage(Status.INVALID_METHOD,
                        req.getMethod().name() + " (not implemented)"));
                break;
        }
    }

    private void handleSynchronize(WebSocketSession session, Request req) {
    }

    private void handleGetDirectoryInfo(WebSocketSession session) {

    }

    private void handleDownloadContents(WebSocketSession session) {

    }

    private void handleAddImages(WebSocketSession session) {

    }

    private void handleCloseSession(WebSocketSession session) throws IOException {
        service.removeSession(sessionId);
        sendResponse(session, Response.success());
        session.close();
    }

    private void handleDisconnectFromSession(WebSocketSession session) {

    }

    private void handleConnectToSession(WebSocketSession session, Request req) {
    }

    private void sendResponse(WebSocketSession session, Response response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(response);
        TextMessage msg = new TextMessage(str);
        session.sendMessage(msg);
    }

    private void handleInitSession(WebSocketSession session, Request req) throws IOException {
        Map<String, Object> props = req.getProperties();
        if (!props.containsKey("directoryName"))
            throw new IllegalArgumentException("directory name has not been specified");

        sessionId = SessionIdProvider.generateSessionId();
        String directoryName = (String) props.get("directoryName");
        service.initSession(sessionId, directoryName);

        Response response = new Response(Status.SUCCESS);
        response.setProperty("sessionId", sessionId);
        sendResponse(session, response);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace(System.out);
    }
}
