package ru.danilakondr.netalbum.client.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.WebSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import ru.danilakondr.netalbum.api.response.Response;

public class ResponseListener implements WebSocket.Listener {
    private BlockingQueue<Response> responseQueue;
    private final StringBuilder sb = new StringBuilder();
    private CompletableFuture<?> accumulatedMessage = new CompletableFuture<>();

    public void setResponseQueue(BlockingQueue<Response> responseQueue) {
        this.responseQueue = responseQueue;
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        sb.append(data);
        webSocket.request(1);

        if (last) {
            processResponse(sb.toString());
            sb.setLength(0);
            accumulatedMessage.complete(null);
            CompletionStage<?> cf = accumulatedMessage;
            accumulatedMessage = new CompletableFuture<>();
            return cf;
        }

        return accumulatedMessage;
    }

    private void processResponse(String strResponse) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Response resp = mapper.readValue(strResponse, Response.class);
            responseQueue.put(resp);
            /*
            switch (resp.getStatus()) {
                case SESSION_CREATED:
                    sessionId = ((Response.SessionCreated) resp).getSessionId();
                    break;
                case DIRECTORY_INFO:
                    directoryInfo = ((Response.DirectoryInfo) resp);
                    break;
                case SYNCHRONIZING:
                    changes = ((Response.Synchronizing) resp).getChanges();
                    break;
                case THUMBNAILS_ARCHIVE:
                    thumbnailsZip = ((Response.ThumbnailsArchive) resp).getThumbnailsZip();
                    break;
                case SUCCESS:
                    break;
                case FILE_NOT_FOUND:
                case FILE_ALREADY_EXISTS:
                    throw new IllegalArgumentException(LocalizedMessages
                            .getMessage(resp.getStatus().name(),
                                    resp.getProperties().get("fileName")));
                case NON_EXISTENT_SESSION:
                    throw new IllegalArgumentException(LocalizedMessages
                            .getMessage(resp.getStatus().name(),
                                    resp.getProperties().get("sessionId")));
                case CLIENT_NOT_CONNECTED:
                case CLIENT_ALREADY_CONNECTED:
                case NOT_AN_INITIATOR:
                case NOT_A_VIEWER:
                    throw new IllegalStateException(LocalizedMessages.getMessage(resp.getStatus().name()));
                default:
                    throw new RuntimeException(LocalizedMessages.getMessage(resp.getStatus().name()));
            }
             */

        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
