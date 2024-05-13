package ru.danilakondr.netalbum.client.connect;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;

public class NetAlbumServerConnector extends SwingWorker<WebSocket, Void> {
    private final URI uri;
    private final NetAlbumListener listener;

    public NetAlbumServerConnector(URI uri, NetAlbumListener listener) {
        super();
        this.uri = uri;
        this.listener = listener;
    }
    @Override
    public WebSocket doInBackground() throws Exception {
        CompletableFuture<WebSocket> cfWebSocket = HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(uri, listener);

        return cfWebSocket.get();
    }
}
