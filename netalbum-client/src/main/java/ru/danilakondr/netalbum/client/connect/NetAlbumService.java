package ru.danilakondr.netalbum.client.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.danilakondr.netalbum.api.message.Request;
import ru.danilakondr.netalbum.api.message.Response;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.danilakondr.netalbum.api.message.Message;

public class NetAlbumService extends SubmissionPublisher<Message>
        implements WebSocket.Listener {
    private final CountDownLatch connectionLatch = new CountDownLatch(1);
    private final ExecutorService service;
    private Thread pinger;

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
            try {
                CompletableFuture<WebSocket> cfWebSocket = HttpClient
                        .newHttpClient()
                        .newWebSocketBuilder()
                        .buildAsync(uri, NetAlbumService.this);
                WebSocket s = cfWebSocket.join();
                connectionLatch.countDown();

                Message msg = new Message(Message.Type.CONNECTION_ESTABLISHED);
                msg.setProperty("url", uri.toString());
                submit(msg);
            }
            catch (CompletionException ex) {
                Message msg = new Message(Message.Type.CONNECTION_FAILED);
                msg.setProperty("message", ex.toString());
                submit(msg);
            }
        });


    }

    public void waitUntilConnected() throws InterruptedException {
        connectionLatch.await();
    }

    public void sendRequest(Request req) {
        service.execute(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String msg = mapper.writeValueAsString(req);
                socket.sendText(msg, true).get();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException ex) {
                Message msg = new Message(Message.Type.CONNECTION_BROKEN);
                msg.setProperty("message", ex.toString());
                submit(msg);

                Logger.getLogger(NetAlbumService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException | CompletionException ex) {
                Message msg = new Message(Message.Type.CONNECTION_BROKEN);
                msg.setProperty("message", ex.toString());
                submit(msg);
                
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

        pinger = new Thread(() -> {
            try {
                while (true) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(bos);

                    dos.writeLong(System.currentTimeMillis());
                    dos.writeLong(System.nanoTime());

                    ByteBuffer buf = ByteBuffer.wrap(bos.toByteArray());
                    webSocket.sendPing(buf).join();
                    Thread.sleep(10000);
                }
            }
            catch (Exception ex) {
                Thread.currentThread().interrupt();
            }
        });
        pinger.start();
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
        
        if (statusCode == 1000) {
            Message msg = new Message(Message.Type.CONNECTION_CLOSED);
            msg.setProperty("statusCode", statusCode);
            msg.setProperty("reason", reason);
            submit(msg);
        }
        else {
            Message msg = new Message(Message.Type.CONNECTION_BROKEN);
            msg.setProperty("message", reason);
            submit(msg);
        }

        pinger.interrupt();

        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        WebSocket.Listener.super.onError(webSocket, error);
        System.out.println("Error: "+error);
        
        Message msg = new Message(Message.Type.CONNECTION_BROKEN);
        msg.setProperty("message", error.toString());
        submit(msg);

        pinger.interrupt();
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
        socket.sendClose(1000, "");
        service.shutdown();
        pinger.interrupt();
    }
}
