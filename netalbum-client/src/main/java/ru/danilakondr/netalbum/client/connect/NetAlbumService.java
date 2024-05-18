package ru.danilakondr.netalbum.client.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.danilakondr.netalbum.api.message.Request;
import ru.danilakondr.netalbum.api.message.Response;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetAlbumService extends SubmissionPublisher<Response>
        implements WebSocket.Listener {
    private final CountDownLatch connectionLatch = new CountDownLatch(1);
    private final ExecutorService service;
    private volatile boolean connected = false;
    private WebSocket socket;

    public NetAlbumService() {
        super(Executors.newSingleThreadExecutor(), 1000);
        this.service = Executors.newSingleThreadExecutor();
    }

    public void connectTo(URI uri) {
        if (connected)
            throw new IllegalStateException("Already connected");

        service.execute(() -> {
            CompletableFuture<WebSocket> cfWebSocket = HttpClient
                    .newHttpClient()
                    .newWebSocketBuilder()
                    .buildAsync(uri, NetAlbumService.this);
            cfWebSocket.join();
            connectionLatch.countDown();
        });
    }

    public void waitUntilConnected() throws InterruptedException {
        System.out.println("Start waiting");
        connectionLatch.await();
        System.out.println("Stop waiting");
    }

    public void putRequest(Request req) {
        service.execute(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String msg = mapper.writeValueAsString(req);
                socket.sendText(msg, true).get();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException ex) {
                Response.Error err = new Response.Error(Response.Error.Status.EXCEPTION);
                err.setProperty("message", ex);
                submit(err);

                Logger.getLogger(NetAlbumService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Response.Error err = new Response.Error(Response.Error.Status.EXCEPTION);
                err.setProperty("message", ex);
                submit(err);

                Logger.getLogger(NetAlbumService.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private final StringBuilder sb = new StringBuilder();
    private CompletableFuture<?> accumulatedMessage = new CompletableFuture<>();

    @Override
    public void onOpen(WebSocket webSocket) {
        webSocket.request(1);
        this.socket = webSocket;
        this.connected = true;
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

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.printf("Closed %d %s%n", statusCode, reason);
        connected = false;
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    private void processResponse(String strResponse) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Response resp = mapper.readValue(strResponse, Response.class);
            submit(resp);
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public void disconnect() {
        service.shutdown();
    }
}
