package ru.danilakondr.netalbum.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.danilakondr.netalbum.api.data.ChangeCommand;
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
import ru.danilakondr.netalbum.api.data.ChangeInfo;
import ru.danilakondr.netalbum.api.data.FileInfo;

import static ru.danilakondr.netalbum.api.message.Response.Error.Status.*;

@Service
public class NetAlbumHandler extends TextWebSocketHandler {
    private NetAlbumService service;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, WebSocketSession> initiators = new HashMap<>();
    private static final Map<WebSocketSession, String> connected = new HashMap<>();
    
    private final StringBuilder builder = new StringBuilder();

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
        builder.append(message.getPayload());
        if (!message.isLast())
            return;
        String msg = builder.toString();
        builder.setLength(0);
        
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
                case ADD_FILE:
                    handleAddFile(session, (Request.AddFile)req);
                    break;
                case ADD_DIRECTORY:
                    handleAddDirectory(session, (Request.AddDirectory)req);
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
        } 
        catch (NonExistentSession e) {
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
        catch (ClientAlreadyConnectedError e) {
            sendResponse(session, new Response.Error(CLIENT_ALREADY_CONNECTED));
        }
        catch (InitiatorAlreadyConnectedError e) {
            sendResponse(session, new Response.Error(INITIATOR_ALREADY_CONNECTED));
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
        catch (NotADirectoryError e) {
            Response.Error err = new Response.Error(NOT_A_DIRECTORY);
            err.setProperty("fileName", e.getMessage());
            sendResponse(session, err);
        }
    }

    private void putInitiator(WebSocketSession session, String sessionId) {
        if (initiators.containsKey(sessionId))
            throw new InitiatorAlreadyConnectedError();

        initiators.put(sessionId, session);
        connected.put(session, sessionId);
    }

    private void putViewer(WebSocketSession session, String sessionId) {
        connected.put(session, sessionId);
    }
    
    private boolean isConnected(WebSocketSession session) {
        return connected.containsKey(session);
    }
    
    private boolean isInitiator(WebSocketSession session) {
        boolean isConnected = this.isConnected(session);
        if (isConnected) {
            String sessionId = connected.get(session);
            return Objects.equals(session, initiators.get(sessionId));
        }
        return false;
    }
    
    private void removeClient(WebSocketSession session) {
        if (!isConnected(session))
            return;
        
        System.out.print("Removing client " + session + " ");
        
        if (isInitiator(session)) {
            String sessionId = connected.get(session);
            System.out.print("(initiator of " + sessionId + ")");
            initiators.remove(sessionId);
        }

        System.out.println();
        connected.remove(session);
    }

    private void handleRestoreSession(WebSocketSession session, Request.RestoreSession req) throws IOException {
        if (isConnected(session))
            throw new ClientAlreadyConnectedError();

        String id = req.getSessionId();
        NetAlbumSession s = service.getSession(id);
        if (s == null)
            throw new NonExistentSession(id);
        
        putInitiator(session, id);
        Response resp = new Response(Response.Type.SESSION_RESTORED);
        resp.setProperty("sessionId", id);
        sendResponse(session, resp);
        
        if (!service.isChangeQueueEmpty(id)) {
            List<ChangeInfo> changes = service.moveChanges(id);
            Response changesResp = new Response.Synchronizing(changes);
            sendResponse(session, changesResp);
        }
    }

    private void handleSynchronize(WebSocketSession session, Request.Synchronize req) throws IOException {
        if (!isConnected(session))
            throw new NotConnectedError();
        
        String sessionId = connected.get(session);

        List<ChangeCommand> changes = req.getChanges();
        
        for (ChangeCommand change: changes) {
            switch (change.getType()) {
                case ADD_FOLDER:
                    ChangeCommand.AddFolder mkdir = (ChangeCommand.AddFolder)change;
                    service.putDirectory(sessionId, mkdir.getFileId(), mkdir.getFolderName());
                    service.putChange(sessionId, change);
                    break;
                case RENAME:
                    service.putChange(sessionId, change);
                    ChangeCommand.Rename ren = (ChangeCommand.Rename)change;
                    service.rename(sessionId, ren.getFileId(), ren.getNewName());
                    break;
            }
        }
        
        if (initiators.containsKey(sessionId)) {
            Response changesResp = new Response.Synchronizing(service.moveChanges(sessionId));
            WebSocketSession initiator = initiators.get(sessionId);
            sendResponse(initiator, changesResp);
            if (initiator != session)
                sendResponse(session, new Response(Response.Type.SUCCESS));
        }
    }

    private void handleGetDirectoryInfo(WebSocketSession session) throws IOException {
        if (!isConnected(session))
            throw new NotConnectedError();

        String sessionId = connected.get(session);
        NetAlbumSession s = service.getSession(sessionId);
        long directorySize = service.getDirectorySize(sessionId);
        long imageCount = service.getImageCount(sessionId);
        
        Response r = new Response.DirectoryInfo(s.getDirectoryName(), directorySize, imageCount);
        sendResponse(session, r);
    }

    private void handleDownloadThumbnails(WebSocketSession session) throws IOException {
        if (!isConnected(session))
            throw new NotConnectedError();

        String sessionId = connected.get(session);
        byte[] zipFile = service.generateArchiveWithThumbnails(sessionId);
        Response resp = new Response.ThumbnailsArchive(zipFile);

        sendResponse(session, resp);
    }

    private void handleAddFile(WebSocketSession session, Request.AddFile req) throws IOException {
        if (!isConnected(session))
            throw new NotConnectedError();

        String sessionId = connected.get(session);
        if (!isInitiator(session))
            throw new NotAnInitiatorError();
        
        System.out.printf("Adding image: %s%n", req.getFile().getFileName());
        ImageData image = req.getFile();
        service.putImage(sessionId, image);

        FileInfo.Image imgInfo = new FileInfo.Image();
        imgInfo.setFileName(image.getFileName());
        imgInfo.setFileSize(image.getFileSize());
        imgInfo.setWidth(image.getWidth());
        imgInfo.setHeight(image.getHeight());
        sendResponse(session, new Response.FileAdded(imgInfo));
    }

    private void handleCloseSession(WebSocketSession session) throws IOException {
        if (!isConnected(session))
            throw new NotConnectedError();

        String sessionId = connected.get(session);
        if (!isInitiator(session))
            throw new NotAnInitiatorError();

        for (WebSocketSession s: connected.keySet()) {
            if (Objects.equals(sessionId, connected.get(s)) && s != session) {
                sendResponse(s, new Response(Response.Type.SESSION_CLOSED));
                s.close();
            }
        }

        service.removeSession(sessionId);
        sendResponse(session, new Response(Response.Type.SESSION_CLOSED));
        removeClient(session);
        session.close();
    }

    private void handleDisconnectFromSession(WebSocketSession session) throws IOException {
        if (!isConnected(session))
            throw new NotConnectedError();

        sendResponse(session, new Response(Response.Type.CLIENT_DISCONNECTED));
        removeClient(session);
        session.close();
    }

    private void handleConnectToSession(WebSocketSession session, Request.ConnectToSession req) throws IOException {
        if (isConnected(session))
            throw new ClientAlreadyConnectedError();

        String id = req.getSessionId();
        NetAlbumSession s = service.getSession(id);
        if (s == null)
            throw new NonExistentSession(id);

        putViewer(session, id);
        Response resp = new Response(Response.Type.VIEWER_CONNECTED);
        resp.setProperty("sessionId", id);
        sendResponse(session, resp);
    }

    private void sendResponse(WebSocketSession session, Response response) throws IOException {
        String str = mapper.writeValueAsString(response);
        TextMessage msg = new TextMessage(str);
        session.sendMessage(msg);
    }

    private void handleInitSession(WebSocketSession session, Request.InitSession req) throws IOException {
        if (isConnected(session))
            throw new ClientAlreadyConnectedError();

        String id = SessionIdProvider.generateSessionId();
        String directoryName = req.getDirectoryName();
        service.initSession(id, directoryName);
        putInitiator(session, id);

        Response response = new Response.SessionCreated(id);
        sendResponse(session, response);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        removeClient(session);

        System.out.println("Transport error");
        exception.printStackTrace(System.out);

        Response resp = new Response.Error(EXCEPTION);
        resp.setProperty("message", exception.toString());
        sendResponse(session, resp);
        
        session.close();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.printf("Closed session %s %d %s%n", session, status.getCode(), status.getReason());
        removeClient(session);
    }

    private void handleAddDirectory(WebSocketSession session, Request.AddDirectory req) throws IOException {
        if (!isConnected(session))
            throw new NotConnectedError();

        String sessionId = connected.get(session);
        service.putDirectory(sessionId, req.getFileId(), req.getDirectoryName());
        
        FileInfo dirInfo = new FileInfo(FileInfo.Type.DIRECTORY);
        dirInfo.setFileId(req.getFileId());
        dirInfo.setFileName(req.getDirectoryName());
        sendResponse(session, new Response.FileAdded(dirInfo));
    }
}
