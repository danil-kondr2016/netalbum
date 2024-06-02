/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.connect;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import ru.danilakondr.netalbum.api.data.ChangeCommand;
import ru.danilakondr.netalbum.api.message.Message;
import static ru.danilakondr.netalbum.api.message.Message.Type.RESPONSE;
import ru.danilakondr.netalbum.api.message.Request;
import ru.danilakondr.netalbum.api.message.Response;
import ru.danilakondr.netalbum.client.errors.NotADirectoryError;

/**
 *
 * @author danko
 */
public class Session {

    public void requestDirectoryInfo() {
        Request req = new Request(Request.Method.GET_DIRECTORY_INFO);
        service.sendRequest(req);
    }

    public void synchronize(List<ChangeCommand> changes) {
        Request.Synchronize req = new Request.Synchronize();
        req.setChanges(changes);
        service.sendRequest(req);
    }

    public enum Type {
        INIT_SESSION("session.InitSession"),
        CONNECT_TO_SESSION("session.ConnectToSession"),
        ;
        
        private final String strId;

        private Type(String id) {
            this.strId = id;
        }

        public String getLocalizedName() {
            return ResourceBundle.getBundle("ru/danilakondr/"
                    + "netalbum/client/connect/Strings")
                    .getString(strId);
        }
    }

    public enum State {
        NOT_CONNECTED,
        CONNECTION_ESTABLISHED,
        SESSION_ESTABLISHED,
        SESSION_CLOSED
    };

    private final NetAlbumService service;
    private final PropertyChangeSupport pcs;
    private State state = State.NOT_CONNECTED;

    public CompletableFuture<HttpResponse<InputStream>> getThumbnails() {
        String id = getSessionId();

        HttpRequest httpReq = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(httpBaseUri.resolve("archive/"+id))
                .GET()
                .build();

        HttpClient client = HttpClient.newBuilder()
                .build();

        return client.sendAsync(httpReq, BodyHandlers.ofInputStream());
    }

    public Session() {
        super();
        pcs = new PropertyChangeSupport(this);
        service = new NetAlbumService();
        service.subscribe(new Flow.Subscriber<Message>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1);
            }

            @Override
            public void onNext(Message item) {
                switch (item.getType()) {
                    case CONNECTION_ESTABLISHED:
                        onConnectionEstablished(item);
                        break;
                    case RESPONSE:
                        onResponse(item);
                        break;
                }
                subscription.request(1);
            }

            private void onConnectionEstablished(Message item) {
                state = State.CONNECTION_ESTABLISHED;
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace(System.err);
            }

            @Override
            public void onComplete() {
            }

            private void onResponse(Message item) {
                Response resp = (Response) item;
                switch (resp.getAnswerType()) {
                    case SESSION_CREATED:
                        state = State.SESSION_ESTABLISHED;
                        Response.SessionCreated sc = (Response.SessionCreated) resp;
                        setSessionId(sc.getSessionId());
                        setSessionType(Type.INIT_SESSION);
                        break;
                    case SESSION_RESTORED:
                        state = State.SESSION_ESTABLISHED;
                        setSessionId(Objects.toString(resp.getProperty("sessionId")));
                        setSessionType(Type.INIT_SESSION);
                        break;
                    case VIEWER_CONNECTED:
                        state = State.SESSION_ESTABLISHED;
                        setSessionId(Objects.toString(resp.getProperty("sessionId")));
                        setSessionType(Type.CONNECT_TO_SESSION);
                        break;
                    case CLIENT_DISCONNECTED:
                    case SESSION_CLOSED:
                        state = State.SESSION_CLOSED;
                        setSessionType(null);
                        break;
                }
            }
        });
    }

    public void init(String baseUri, String directoryName) {
        setUrl(baseUri);
        service.connectTo(wsApiUri);

        this.addOnConnectionEstablishedListener((s) -> {
            Request.InitSession req = new Request.InitSession();
            req.setDirectoryName(directoryName);
            service.sendRequest(req);
        }, true);
    }

    public void restore(String baseUri, String sessionId) {
        setUrl(baseUri);
        service.connectTo(wsApiUri);

        this.addOnConnectionEstablishedListener((s) -> {
            Request.RestoreSession req = new Request.RestoreSession();
            req.setSessionId(sessionId);
            service.sendRequest(req);
        }, true);
    }

    public void connect(String baseUri, String sessionId) {
        setUrl(baseUri);
        service.connectTo(wsApiUri);

        this.addOnConnectionEstablishedListener((s) -> {
            Request.ConnectToSession req = new Request.ConnectToSession();
            req.setSessionId(sessionId);
            service.sendRequest(req);
        }, true);
    }

    public void disconnect() {
        Request req = new Request(Request.Method.DISCONNECT_FROM_SESSION);
        service.sendRequest(req);
    }

    public void close() {
        Request req = new Request(Request.Method.CLOSE_SESSION);
        service.sendRequest(req);
    }

    public void loadImages(File directory) {
        if (!directory.isDirectory()) {
            throw new NotADirectoryError(directory);
        }

        setPath(directory.getAbsolutePath());
        ImageLoader loader = new ImageLoader(service, directory);
        loader.execute();
    }

    private String url, sessionId, path;
    private URI wsApiUri, httpBaseUri;
    private Type type;

    public String getUrl() {
        return url;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPath() {
        return path;
    }

    public Type getSessionType() {
        return type;
    }

    public void setUrl(String url) {
        pcs.firePropertyChange("url", this.url, url);
        this.url = url;

        try {
            String[] tokens = url.split("/", 2);
            if (tokens.length == 2) {
                this.wsApiUri = new URI("ws", tokens[0], "/" + tokens[1] + "/api", null, null);
                this.httpBaseUri = new URI("http", tokens[0], "/" + tokens[1] + "/", null, null);
            } else {
                this.wsApiUri = new URI("ws", tokens[0], "/api", null, null);
                this.httpBaseUri = new URI("http", tokens[0], "/", null, null);
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setSessionId(String sessionId) {
        pcs.firePropertyChange("sessionId", this.sessionId, sessionId);
        this.sessionId = sessionId;
    }

    public void setPath(String path) {
        pcs.firePropertyChange("path", this.path, path);
        this.path = path;
    }

    public void setSessionType(Type type) {
        this.type = type;
    }

    public boolean isConnected() {
        if (state == State.NOT_CONNECTED || state == State.SESSION_CLOSED) {
            return false;
        }

        return true;
    }

    public State getState() {
        return state;
    }

    private static class ResponseListener implements Flow.Subscriber<Message> {

        private Flow.Subscription subscription;
        private final Session session;
        private final BiConsumer<Session, Response> consumer;
        private final boolean oneShot;
        private final Response.Type respType;

        public ResponseListener(Session s, ru.danilakondr.netalbum.api.message.Response.Type respType, BiConsumer<Session, Response> consumer) {
            this.session = s;
            this.respType = respType;
            this.consumer = consumer;
            this.oneShot = false;
        }

        public ResponseListener(Session s, ru.danilakondr.netalbum.api.message.Response.Type respType, BiConsumer<Session, Response> consumer, boolean oneShot) {
            this.session = s;
            this.respType = respType;
            this.consumer = consumer;
            this.oneShot = oneShot;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(Message item) {
            if (item.getType() != RESPONSE) {
                subscription.request(1);
                return;
            }
            
            Response resp = (Response)item;
            if (respType == null || (respType != null && resp.getAnswerType() == respType)) {
                consumer.accept(session, resp);
                if (oneShot) {
                    subscription.cancel();
                    return;
                }
            }
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace(System.err);
        }

        @Override
        public void onComplete() {
        }
    }

    private static class MessageListener implements Flow.Subscriber<Message> {

        private Flow.Subscription subscription;
        private final Session session;
        private final BiConsumer<Session, Message> consumer;
        private final boolean oneShot;
        private final Message.Type msgType;

        public MessageListener(Session s, Message.Type msgType, BiConsumer<Session, Message> consumer) {
            this.session = s;
            this.msgType = msgType;
            this.consumer = consumer;
            this.oneShot = false;
        }

        public MessageListener(Session s, Message.Type msgType, BiConsumer<Session, Message> consumer, boolean oneShot) {
            this.session = s;
            this.msgType = msgType;
            this.consumer = consumer;
            this.oneShot = oneShot;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(Message item) {
            if (msgType == null || (msgType != null && item.getType() == msgType)) {
                consumer.accept(session, item);
                if (oneShot)
                    subscription.cancel();
            }
            
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace(System.err);
        }

        @Override
        public void onComplete() {
        }
    }
    
    public void addOnSessionClosedListener(Consumer<Session> listener) {
        service.subscribe(new MessageListener(this, Message.Type.RESPONSE, (s, item) -> {
            Response resp = (Response)item;
            switch (resp.getAnswerType()) {
                case CLIENT_DISCONNECTED:
                case SESSION_CLOSED:
                    listener.accept(Session.this);
                    break;
            }
        }));
    }

    public void addOnConnectionEstablishedListener(Consumer<Session> listener, boolean oneShot) {
        service.subscribe(new MessageListener(this, Message.Type.CONNECTION_ESTABLISHED, (s, item) -> {
            switch (item.getType()) {
                case CONNECTION_ESTABLISHED:
                    listener.accept(this);
                    break;
            }
        }, true));
    }
    
    public void addOnSessionEstablishedListener(Consumer<Session> listener) {
        service.subscribe(new MessageListener(this, Message.Type.RESPONSE, (s, item) -> {
            Response resp = (Response)item;
            switch (resp.getAnswerType()) {
                case SESSION_CREATED:
                case SESSION_RESTORED:
                case VIEWER_CONNECTED:
                    listener.accept(Session.this);
                    break;
            }
        }));
    }

    public void addOnConnectionFailedListener(BiConsumer<Session, Message> listener) {
        service.subscribe(new MessageListener(this, Message.Type.CONNECTION_FAILED, listener));
    }
    
    public void addOnConnectionBrokenListener(BiConsumer<Session, Message> listener) {
        service.subscribe(new MessageListener(this, Message.Type.CONNECTION_BROKEN, listener));
    }
    
    public void addOnResponseListener(Response.Type type, Consumer<Session> listener) {
        addOnResponseListener(type, listener, false);
    }

    public void addOnResponseListener(Response.Type type, BiConsumer<Session, Response> listener) {
        addOnResponseListener(type, listener, false);
    }

    public void addOnResponseListener(Response.Type type, Consumer<Session> listener, boolean oneShot) {
        service.subscribe(new ResponseListener(this, type, (s, item) -> listener.accept(s), oneShot));
    }

    public void addOnResponseListener(Response.Type type, BiConsumer<Session, Response> listener, boolean oneShot) {
        service.subscribe(new ResponseListener(this, type, listener, oneShot));
    }

    public void addOnConnectionClosedListener(BiConsumer<Session, Message> listener) {
        service.subscribe(new MessageListener(this, Message.Type.CONNECTION_CLOSED, listener));
    }
}
