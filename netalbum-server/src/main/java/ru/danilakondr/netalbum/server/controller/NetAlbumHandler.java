package ru.danilakondr.netalbum.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.danilakondr.netalbum.api.data.Change;
import ru.danilakondr.netalbum.api.data.ImageData;
import ru.danilakondr.netalbum.api.request.Request;
import ru.danilakondr.netalbum.api.response.Response;
import ru.danilakondr.netalbum.api.response.Status;
import ru.danilakondr.netalbum.server.error.*;
import ru.danilakondr.netalbum.server.SessionIdProvider;
import ru.danilakondr.netalbum.server.db.NetAlbumService;
import ru.danilakondr.netalbum.server.model.NetAlbumSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class NetAlbumHandler extends TextWebSocketHandler {
    private String sessionId;
    private NetAlbumService service;
    private boolean initiator = false;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, WebSocketSession> initiators = new HashMap<>();
    private static final Map<WebSocketSession, String> connected = new HashMap<>();
    private final StringBuilder sb = new StringBuilder();

    @Autowired
    public void setService(NetAlbumService service) {
        this.service = service;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Connection established");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if (!message.isLast()) {
            sb.append(message.getPayload());
            return;
        }

        String msg = sb.toString();
        sb.delete(0, sb.length()-1);

        Request req;

        try {
            req = mapper.readValue(msg, Request.class);
        }
        catch (JsonProcessingException e) {
            sendResponse(session, Response.invalidRequest(e.getMessage()));
            return;
        }

        try {
            switch (req.getMethod()) {
                case INIT_SESSION:
                    handleInitSession(session, (Request.InitSession)req);
                    break;
                case RESTORE_SESSION:
                    handleRestoreSession(session, (Request.RestoreSession)req);
                    break;
                case CONNECT_TO_SESSION:
                    handleConnectToSession(session, (Request.ConnectToSession)req);
                    break;
                case DISCONNECT_FROM_SESSION:
                    handleDisconnectFromSession(session);
                    break;
                case CLOSE_SESSION:
                    handleCloseSession(session);
                    break;
                case ADD_IMAGES:
                    handleAddImages(session, (Request.AddImages)req);
                    break;
                case DOWNLOAD_THUMBNAILS:
                    handleDownloadThumbnails(session);
                    break;
                case GET_DIRECTORY_INFO:
                    handleGetDirectoryInfo(session);
                    break;
                case SYNCHRONIZE:
                    handleSynchronize(session, (Request.Synchronize)req);
                    break;
                default:
                    sendResponse(session, Response.invalidMethod(""));
                    break;
            }
        }
        catch (InvalidRequestError e) {
            sendResponse(session, Response.invalidRequest(e.getMessage()));
        }
        catch (NonExistentSession e) {
            sendResponse(session, Response.nonExistentSession(e.getMessage()));
        }
        catch (IllegalArgumentException e) {
            sendResponse(session, Response.invalidArgument(e.getMessage()));
        }
        catch (NotAnInitiatorError e) {
            sendResponse(session, new Response(Status.NOT_AN_INITIATOR));
        }
        catch (NotAViewerError e) {
            sendResponse(session, new Response(Status.NOT_A_VIEWER));
        }
        catch (NotConnectedError e) {
            sendResponse(session, new Response(Status.CLIENT_NOT_CONNECTED));
        }
        catch (AlreadyConnectedError e) {
            sendResponse(session, new Response(Status.CLIENT_ALREADY_CONNECTED));
        }
        catch (FileNotFoundError e) {
            sendResponse(session, Response.fileNotFound(e.getMessage()));
        }
        catch (FileAlreadyExistsError e) {
            sendResponse(session, Response.fileAlreadyExists(e.getMessage()));
        }
    }

    private void handleRestoreSession(WebSocketSession session, Request.RestoreSession req) throws IOException {
        if (sessionId != null)
            throw new AlreadyConnectedError();

        String id = req.getSessionId();
        NetAlbumSession s = service.getSession(id);
        if (s == null)
            throw new NonExistentSession(id);

        this.sessionId = id;
        this.initiator = true;
        initiators.put(sessionId, session);
        sendResponse(session, Response.success());
    }

    private void handleSynchronize(WebSocketSession session, Request.Synchronize req) throws IOException {
        if (sessionId == null)
            throw new NotConnectedError();

        List<Change> changes = req.getChanges();
        // Первое. Записать изменения в базу данных.
        for (Change change: changes) {
            service.renameFile(sessionId, change.getOldName(), change.getNewName());
        }
        // Второе. Отправить изменения другим пользователям (в т.ч. инициатору).
        Response changesResp = Response.synchronizing(changes);
        for (Map.Entry<WebSocketSession, String> e : connected.entrySet()) {
            if (e.getValue().equals(sessionId)) {
                sendResponse(e.getKey(), changesResp);
            }
        }

        sendResponse(session, Response.success());
    }

    private void handleGetDirectoryInfo(WebSocketSession session) throws IOException {
        if (sessionId == null)
            throw new NotConnectedError();

        NetAlbumSession s = service.getSession(sessionId);
        long directorySize = service.getDirectorySize(sessionId);
        Response r = Response.directoryInfo(s.getDirectoryName(), directorySize);
        sendResponse(session, r);
    }

    private void handleDownloadThumbnails(WebSocketSession session) throws IOException {
        if (sessionId == null)
            throw new NotConnectedError();

        byte[] zipFile = service.generateArchiveWithThumbnails(sessionId);
        Response resp = new Response(Status.THUMBNAILS_ARCHIVE);
        resp.setProperty("thumbnailsZip", zipFile);

        sendResponse(session, resp);
    }

    private void handleAddImages(WebSocketSession session, Request.AddImages req) throws IOException {
        if (sessionId == null)
            throw new NotConnectedError();

        if (!initiator)
            throw new NotAnInitiatorError();

        List<ImageData> images = req.getImages();
        for (ImageData image : images) {
            service.putImage(sessionId, image);
        }

        sendResponse(session, Response.success());
    }

    private void handleCloseSession(WebSocketSession session) throws IOException {
        if (sessionId == null)
            throw new NotConnectedError();

        if (!initiator)
            throw new NotAnInitiatorError();

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
            throw new NotConnectedError();

        if (initiator)
            initiators.remove(sessionId);
        connected.remove(session);
        sendResponse(session, Response.success());
        session.close();
    }

    private void handleConnectToSession(WebSocketSession session, Request.ConnectToSession req) throws IOException {
        if (sessionId != null)
            throw new AlreadyConnectedError();

        String id = req.getSessionId();
        NetAlbumSession s = service.getSession(id);
        if (s == null)
            throw new NonExistentSession(id);

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

    private void handleInitSession(WebSocketSession session, Request.InitSession req) throws IOException {
        if (sessionId != null)
            throw new AlreadyConnectedError();

        sessionId = SessionIdProvider.generateSessionId();
        String directoryName = req.getDirectoryName();
        service.initSession(sessionId, directoryName);
        initiator = true;
        initiators.put(sessionId, session);
        connected.put(session, sessionId);

        Response response = new Response(Status.SESSION_CREATED);
        response.setProperty("sessionId", sessionId);
        sendResponse(session, response);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace(System.out);
        sendResponse(session, Response.withMessage(Status.EXCEPTION, exception.toString()));
        if (initiator)
            initiators.remove(sessionId);
        connected.remove(session);
    }
}
