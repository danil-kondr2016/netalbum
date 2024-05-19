/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.connect;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import ru.danilakondr.netalbum.api.message.Message;
import ru.danilakondr.netalbum.api.message.Request;
import ru.danilakondr.netalbum.api.message.Response;
import static ru.danilakondr.netalbum.api.message.Response.Type.CLIENT_DISCONNECTED;
import static ru.danilakondr.netalbum.api.message.Response.Type.SESSION_CLOSED;
import ru.danilakondr.netalbum.client.LocalizedMessages;

/**
 *
 * @author danko
 */
public class Session {
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
                        break;
                    case SESSION_RESTORED:
                        connected = true;
                        setSessionId(Objects.toString(resp.getProperty("sessionId")));
                        break;
                    case VIEWER_CONNECTED:
                        connected = true;
                        setSessionId(Objects.toString(resp.getProperty("sessionId")));
                        break;
                    case CLIENT_DISCONNECTED:
                    case SESSION_CLOSED:
                        connected = false;
                        break;
                }
            }
        });
    }
    
    public void init(URI uri, String directoryName) {
        service.connectTo(uri);
        
        Request.InitSession req = new Request.InitSession();
        req.setDirectoryName(directoryName);
        service.putRequest(req);
    }
    
    public void restore(URI uri, String sessionId) {
        service.connectTo(uri);
        
        Request.RestoreSession req = new Request.RestoreSession();
        req.setSessionId(sessionId);
        service.putRequest(req);
    }
    
    public void connect(URI uri, String sessionId) {
        service.connectTo(uri);
        
        Request.ConnectToSession req = new Request.ConnectToSession();
        req.setSessionId(sessionId);
        service.putRequest(req);
    }
    
    public void disconnect() {
        Request req = new Request(Request.Method.DISCONNECT_FROM_SESSION);
        service.putRequest(req);
    }
    
    public void close() {
        Request req = new Request(Request.Method.CLOSE_SESSION);
        service.putRequest(req);
    }
    
    public void loadImages(File directory) {
        if (!directory.isDirectory())
            throw new IllegalArgumentException(LocalizedMessages.notADirectoryError(directory));
        
        ImageLoader loader = new ImageLoader(service, directory);
        loader.execute();
    }
    
    private String url, sessionId, path;
    
    public String getUrl() {
        return url;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public String getPath() {
        return path;
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
    
    public boolean isConnected() {
        return connected;
    }
    
    public void addOnCloseListener(Consumer<Session> listener) {
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
                subscription.request(1);
            }
            
            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
            }
        });
    }
}
