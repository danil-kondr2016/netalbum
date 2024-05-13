package ru.danilakondr.netalbum.client.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.danilakondr.netalbum.api.request.Request;
import ru.danilakondr.netalbum.api.response.Response;

import javax.swing.*;
import java.net.http.WebSocket;

public class NetAlbumRequestSender extends SwingWorker<Response, Void> {
    private final NetAlbumListener listener;
    private final WebSocket socket;
    private final Request request;

    public NetAlbumRequestSender(NetAlbumListener listener, WebSocket socket, Request request) {
        super();
        this.listener = listener;
        this.socket = socket;
        this.request = request;
    }

    @Override
    protected Response doInBackground() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String msg = mapper.writeValueAsString(request);

        socket.sendText(msg, true).get();
        return listener.getResponse();
    }
}
