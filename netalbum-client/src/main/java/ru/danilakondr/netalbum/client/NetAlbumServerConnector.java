package ru.danilakondr.netalbum.client;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;

public class NetAlbumServerConnector extends SwingWorker<WebSocket, Void> {
    private final URI uri;

    public NetAlbumServerConnector(URI uri) {
        super();
        this.uri = uri;
    }
    @Override
    public WebSocket doInBackground() throws Exception {
        CompletableFuture<WebSocket> cfWebSocket = HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                // TODO listener
                .buildAsync(uri, new WebSocket.Listener(){});

        return cfWebSocket.get();
    }
}
