/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.connect;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Flow;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import ru.danilakondr.netalbum.api.message.Message;
import static ru.danilakondr.netalbum.api.message.Message.Type.RESPONSE;
import ru.danilakondr.netalbum.api.message.Request;
import ru.danilakondr.netalbum.api.message.Response;
import ru.danilakondr.netalbum.client.LocalizedMessages;

/**
 *
 * @author danko
 */
public class Session {

    public void requestDirectoryInfo() {
        Request req = new Request(Request.Method.GET_DIRECTORY_INFO);
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
            return ResourceBundle.getBundle("ru/danilakondr/netalbum/client/connect/Strings").getString(strId);
        }
    }
    
    private final NetAlbumService service;
    private final PropertyChangeSupport pcs;
    private boolean connected = false;
    
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
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace(System.err);
            }

            @Override
            public void onComplete() {
            }

            private void onResponse(Message item) {
                Response resp = (Response)item;
                switch (resp.getAnswerType()) {
                    case SESSION_CREATED:
                        connected = true;
                        Response.SessionCreated sc = (Response.SessionCreated)resp;
                        setSessionId(sc.getSessionId());
                        setSessionType(Type.INIT_SESSION);
                        break;
                    case SESSION_RESTORED:
                        connected = true;
                        setSessionId(Objects.toString(resp.getProperty("sessionId")));
                        setSessionType(Type.INIT_SESSION);
                        break;
                    case VIEWER_CONNECTED:
                        connected = true;
                        setSessionId(Objects.toString(resp.getProperty("sessionId")));
                        setSessionType(Type.CONNECT_TO_SESSION);
                        break;
                    case CLIENT_DISCONNECTED:
                    case SESSION_CLOSED:
                        connected = false;
                        setSessionType(null);
                        break;
                }
            }
        });
    }
    
    public void init(URI uri, String directoryName) {
        setUrl(uri.toString());
        service.connectTo(uri);
        
        Request.InitSession req = new Request.InitSession();
        req.setDirectoryName(directoryName);
        service.sendRequest(req);
    }
    
    public void restore(URI uri, String sessionId) {
        setUrl(uri.toString());
        service.connectTo(uri);
        
        Request.RestoreSession req = new Request.RestoreSession();
        req.setSessionId(sessionId);
        service.sendRequest(req);
    }
    
    public void connect(URI uri, String sessionId) {
        setUrl(uri.toString());
        service.connectTo(uri);
        
        Request.ConnectToSession req = new Request.ConnectToSession();
        req.setSessionId(sessionId);
        service.sendRequest(req);
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
        if (!directory.isDirectory())
            throw new IllegalArgumentException(LocalizedMessages.notADirectoryError(directory));
        
        setPath(directory.getAbsolutePath());
        ImageLoader loader = new ImageLoader(service, directory);
        loader.execute();
    }
    
    private String url, sessionId, path;
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
        return connected;
    }

    private static class Listener implements Flow.Subscriber<Message> {
        private Flow.Subscription subscription;
        private final Session session;
        private final BiConsumer<Session, Message> consumer;
        private final boolean oneShot;
        
        public Listener(Session s, BiConsumer<Session, Message> consumer) {
            this.session = s;
            this.consumer = consumer;
            this.oneShot = false;
        }
        
        public Listener(Session s, BiConsumer<Session, Message> consumer, boolean oneShot) {
            this.session = s;
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
            consumer.accept(session, item);
            if (!oneShot)
                subscription.request(1);
            else
                subscription.cancel();
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace(System.err);
        }

        @Override
        public void onComplete() {
        }
    }
    
    public void addOnCloseListener(Consumer<Session> listener) {
        service.subscribe(new Listener(this, (s, item) -> {
            switch (item.getType()) {
                case RESPONSE:
                    Response resp = (Response)item;
                    switch (resp.getAnswerType()) {
                        case CLIENT_DISCONNECTED:
                        case SESSION_CLOSED:
                            listener.accept(Session.this);
                            break;
                    }
                    break;
            }
        }));
    }
    
    public void addOnConnectedListener(Consumer<Session> listener) {
        service.subscribe(new Listener(this, (s, item) -> {
            switch (item.getType()) {
                case RESPONSE:
                    Response resp = (Response)item;
                    switch (resp.getAnswerType()) {
                        case SESSION_CREATED:
                        case SESSION_RESTORED:
                        case VIEWER_CONNECTED:
                            listener.accept(Session.this);
                            break;
                    }
                    break;
            }
        }));
    }

    public void addOnResponseListener(Response.Type type, Consumer<Session> listener) {
        service.subscribe(new Listener(this, (s, item) -> {
            switch (item.getType()) {
                case RESPONSE:
                    Response resp = (Response)item;
                    if (resp.getAnswerType() == type)
                        listener.accept(Session.this);
                    break;
            }
        }));
    }
    
    public void addOnResponseListener(Response.Type type, Consumer<Session> listener, boolean oneShot) {
        service.subscribe(new Listener(this, (s, item) -> {
            switch (item.getType()) {
                case RESPONSE:
                    Response resp = (Response)item;
                    if (resp.getAnswerType() == type)
                        listener.accept(Session.this);
                    break;
            }
        }, oneShot));
    }
}
