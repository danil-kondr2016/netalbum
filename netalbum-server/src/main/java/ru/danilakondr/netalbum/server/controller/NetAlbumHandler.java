package ru.danilakondr.netalbum.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.danilakondr.netalbum.api.data.Change;
import ru.danilakondr.netalbum.api.data.ImageData;
import ru.danilakondr.netalbum.api.message.Request;
import ru.danilakondr.netalbum.api.message.Response;
import ru.danilakondr.netalbum.server.error.*;
import ru.danilakondr.netalbum.server.SessionIdProvider;
import ru.danilakondr.netalbum.server.db.NetAlbumService;
import ru.danilakondr.netalbum.server.model.NetAlbumSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import ru.danilakondr.netalbum.api.data.ImageInfo;

import static ru.danilakondr.netalbum.api.message.Response.Error.Status.*;

@Service
public class NetAlbumHandler extends TextWebSocketHandler {
    private NetAlbumService service;
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
        sb.append(message.getPayload());
        if (!message.isLast()) {
            return;
        }

        String msg = sb.toString();
        sb.setLength(0);

        Request req;

        try {
            req = mapper.readValue(msg, Request.class);
        }
        catch (JsonProcessingException e) {
            Response err = new Response.Error(INVALID_REQUEST);
            err.setProperty("message", e.getMessage());
            sendResponse(session, err);
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
                case ADD_IMAGE:
                    System.out.println("Message received: ADD_IMAGE");
                    handleAddImage(session, (Request.AddImage)req);
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
                    sendResponse(session, new Response.Error(INVALID_REQUEST));
                    break;
            }
        }
        catch (IllegalArgumentException e) {
            Response.Error err = new Response.Error(INVALID_REQUEST);
            err.setProperty("message", e.getMessage());
            sendResponse(session, err);
        } catch (NonExistentSession e) {
            Response.Error err = new Response.Error(NON_EXISTENT_SESSION);
            err.setProperty("sessionId", e.getMessage());
            sendResponse(session, err);
        }
        catch (NotAnInitiatorError e) {
            sendResponse(session, new Response.Error(NOT_AN_INITIATOR));
        }
        catch (NotAViewerError e) {
            sendResponse(session, new Response.Error(NOT_A_VIEWER));
        }
        catch (NotConnectedError e) {
            sendResponse(session, new Response.Error(CLIENT_NOT_CONNECTED));
        }
        catch (AlreadyConnectedError e) {
            sendResponse(session, new Response.Error(CLIENT_ALREADY_CONNECTED));
        }
        catch (FileNotFoundError e) {
            Response.Error err = new Response.Error(FILE_NOT_FOUND);
            err.setProperty("fileName", e.getMessage());
            sendResponse(session, err);
        }
        catch (FileAlreadyExistsError e) {
            Response.Error err = new Response.Error(FILE_ALREADY_EXISTS);
            err.setProperty("fileName", e.getMessage());
            sendResponse(session, err);
        }
    }

    private void putInitiator(WebSocketSession session, String sessionId) {
        this.sessionId = sessionId;
        this.initiator = true;

        initiators.put(sessionId, session);
        connected.put(session, sessionId);
    }

    private void putViewer(WebSocketSession session, String sessionId) {
        this.sessionId = sessionId;
        this.initiator = false;

        connected.put(session, sessionId);
    }

    private void removeClient(WebSocketSession session) {
        try {
            if (initiator)
                initiators.remove(sessionId);
            initiator = false;
            sessionId = null;

            connected.remove(session);
        }
        catch (Exception ignored) {}
    }

    private void handleRestoreSession(WebSocketSession session, Request.RestoreSession req) throws IOException {
        if (sessionId != null)
            throw new AlreadyConnectedError();

        String id = req.getSessionId();
        NetAlbumSession s = service.getSession(id);
        if (s == null)
            throw new NonExistentSession(id);

        putInitiator(session, id);
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
        Response changesResp = new Response.Synchronizing(changes);
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
        Response r = new Response.DirectoryInfo(s.getDirectoryName(), directorySize);
        sendResponse(session, r);
    }

    private void handleDownloadThumbnails(WebSocketSession session) throws IOException {
        if (sessionId == null)
            throw new NotConnectedError();

        byte[] zipFile = service.generateArchiveWithThumbnails(sessionId);
        Response resp = new Response.ThumbnailsArchive(zipFile);

        sendResponse(session, resp);
    }

    private void handleAddImage(WebSocketSession session, Request.AddImage req) throws IOException {
        if (sessionId == null)
            throw new NotConnectedError();

        if (!initiator)
            throw new NotAnInitiatorError();
        
        System.out.printf("Adding image: %s%n", req.getImage().getFileName());
        ImageData image = req.getImage();
        service.putImage(sessionId, image);

        ImageInfo imgInfo = new ImageInfo();
        imgInfo.setFileName(image.getFileName());
        imgInfo.setFileSize(image.getFileSize());
        imgInfo.setWidth(image.getWidth());
        imgInfo.setHeight(image.getHeight());
        sendResponse(session, new Response.ImageAdded(imgInfo));
    }

    private void handleCloseSession(WebSocketSession session) throws IOException {
        if (sessionId == null)
            throw new NotConnectedError();

        if (!initiator)
            throw new NotAnInitiatorError();

        for (WebSocketSession s: connected.keySet()) {
            if (Objects.equals(sessionId, connected.get(s)) && s != session) {
                sendResponse(s, new Response(Response.Type.SESSION_EXITS));
                s.close();
            }
        }

        sendResponse(session, new Response(Response.Type.SUCCESS));
        removeClient(session);
        session.close();
    }

    private void handleDisconnectFromSession(WebSocketSession session) throws IOException {
        if (sessionId == null)
            throw new NotConnectedError();

        if (initiator)
            initiators.remove(sessionId);

        connected.remove(session);
        sendResponse(session, Response.success());
        removeClient(session);
        session.close();
    }

    private void handleConnectToSession(WebSocketSession session, Request.ConnectToSession req) throws IOException {
        if (sessionId != null)
            throw new AlreadyConnectedError();

        String id = req.getSessionId();
        NetAlbumSession s = service.getSession(id);
        if (s == null)
            throw new NonExistentSession(id);

        putViewer(session, id);
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

        String id = SessionIdProvider.generateSessionId();
        String directoryName = req.getDirectoryName();
        service.initSession(id, directoryName);
        putInitiator(session, id);

        Response response = new Response.SessionCreated(id);
        sendResponse(session, response);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        exception.printStackTrace(System.out);

        Response resp = new Response.Error(EXCEPTION);
        resp.setProperty("message", exception.toString());
        sendResponse(session, resp);

        removeClient(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.printf("Closed session %s %d %s%n", session, status.getCode(), status.getReason());
        removeClient(session);
    }
}
