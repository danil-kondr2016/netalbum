package ru.danilakondr.netalbum.client.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.danilakondr.netalbum.api.request.Request;
import ru.danilakondr.netalbum.api.response.Response;

import javax.swing.*;
import java.net.http.WebSocket;

public class SendRequestTask extends SwingWorker<Void, Void> {
    private final ResponseListener listener;
    private final WebSocket socket;
    private final Request request;

    public SendRequestTask(ResponseListener listener, WebSocket socket, Request request) {
        super();
        this.listener = listener;
        this.socket = socket;
        this.request = request;
    }

    @Override
    protected Void doInBackground() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String msg = mapper.writeValueAsString(request);

        socket.sendText(msg, true).get();
        return null;
    }
}
