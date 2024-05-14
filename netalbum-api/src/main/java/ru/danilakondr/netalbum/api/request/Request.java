package ru.danilakondr.netalbum.api.request;

import com.fasterxml.jackson.annotation.*;
import ru.danilakondr.netalbum.api.data.Change;
import ru.danilakondr.netalbum.api.data.ImageData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="method", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value=Request.InitSession.class, name="INIT_SESSION"),
        @JsonSubTypes.Type(value=Request.Synchronize.class, name="SYNCHRONIZE"),
        @JsonSubTypes.Type(value=Request.AddImages.class, name="ADD_IMAGES"),
        @JsonSubTypes.Type(value=Request.ConnectToSession.class, name="CONNECT_TO_SESSION"),
        @JsonSubTypes.Type(value=Request.RestoreSession.class, name="RESTORE_SESSION"),
        @JsonSubTypes.Type(value=Request.class, name="CLOSE_SESSION"),
        @JsonSubTypes.Type(value=Request.class, name="DISCONNECT_FROM_SESSION"),
        @JsonSubTypes.Type(value=Request.class, name="GET_DIRECTORY_INFO"),
        @JsonSubTypes.Type(value=Request.class, name="DOWNLOAD_THUMBNAILS")
})
public class Request {
    private Type method;
    private final Map<String, Object> contents;

    public static class InitSession extends Request {
        private String directoryName;

        public InitSession() {
            super(Type.INIT_SESSION);
        }

        public String getDirectoryName() {
            return directoryName;
        }

        public void setDirectoryName(String dirName) {
            this.directoryName = dirName;
        }
    }

    public static class RestoreSession extends Request {
        private String sessionId;

        public RestoreSession() {
            super(Type.RESTORE_SESSION);
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    public static class ConnectToSession extends Request {
        private String sessionId;

        public ConnectToSession() {
            super(Type.CONNECT_TO_SESSION);
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    public static class Synchronize extends Request {
        private List<Change> changes;

        public Synchronize() {
            super(Type.SYNCHRONIZE);
        }

        public List<Change> getChanges() {
            return changes;
        }

        public void setChanges(List<Change> changes) {
            this.changes = changes;
        }
    }

    public static class AddImages extends Request {
        private List<ImageData> images;

        public AddImages() {
            super(Type.ADD_IMAGES);
        }

        @JsonSetter("images")
        public void setImages(List<ImageData> data) {
            this.images = data;
        }

        @JsonGetter("images")
        public List<ImageData> getImages() {
            return images;
        }
    }

    public Request() {
        this.contents = new HashMap<>();
    }

    public Request(Type method) {
        this.method = method;
        this.contents = new HashMap<>();
    }

    public Type getMethod() {
        return method;
    }

    public void setMethod(Type method) {
        this.method = method;
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return contents;
    }

    @JsonAnySetter
    public void setProperty(String prop, Object value) {
        this.contents.put(prop, value);
    }

    public enum Type {
          INIT_SESSION
        , RESTORE_SESSION
        , CONNECT_TO_SESSION
        , DISCONNECT_FROM_SESSION
        , CLOSE_SESSION
        , GET_DIRECTORY_INFO
        , ADD_IMAGES
        , DOWNLOAD_THUMBNAILS
        , SYNCHRONIZE
        ;
    }
}
