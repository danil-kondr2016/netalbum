package ru.danilakondr.netalbum.client.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.danilakondr.netalbum.api.message.Request;
import ru.danilakondr.netalbum.api.message.Response;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.*;

public class NetAlbumService implements WebSocket.Listener {
    private final BlockingQueue<Response> responses = new SynchronousQueue<>();
    private final CountDownLatch latch = new CountDownLatch(1);
    private final ExecutorService service;
    private volatile boolean connected = false;
    private WebSocket socket;

    public NetAlbumService() {
        this.service = Executors.newFixedThreadPool(50);
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
            latch.countDown();
        });
    }

    public void waitUntilConnected() throws InterruptedException {
        System.out.println("Start waiting");
        latch.await();
        System.out.println("Stop waiting");
    }

    public void putRequest(Request req) {
        service.execute(() -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String msg = mapper.writeValueAsString(req);
                socket.sendText(msg, true).join();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Response getResponse() throws InterruptedException {
        return responses.take();
    }

    private final StringBuilder sb = new StringBuilder();
    private CompletableFuture<?> accumulatedMessage = new CompletableFuture<>();

    @Override
    public void onOpen(WebSocket webSocket) {
        webSocket.request(1);
        this.socket = webSocket;
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
        connected = false;
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    private void processResponse(String strResponse) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Response resp = mapper.readValue(strResponse, Response.class);
            responses.put(resp);
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        service.shutdown();
    }
}
