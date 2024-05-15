package ru.danilakondr.netalbum.api.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import ru.danilakondr.netalbum.api.data.Change;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value=Response.DirectoryInfo.class, name="DIRECTORY_INFO"),
        @JsonSubTypes.Type(value=Response.Synchronizing.class, name="SYNCHRONIZING"),
        @JsonSubTypes.Type(value=Response.ThumbnailsArchive.class, name="THUMBNAILS_ARCHIVE"),
        @JsonSubTypes.Type(value=Response.SessionCreated.class, name="SESSION_CREATED"),
        @JsonSubTypes.Type(value=Response.class, name="SUCCESS"),
        @JsonSubTypes.Type(value=Response.Error.class, name="ERROR"),
        @JsonSubTypes.Type(value=Response.class, name="SESSION_EXITS"),
})
public class Response {
    public enum Type {
        SUCCESS, ERROR,
        SESSION_EXITS,
        SESSION_CREATED,
        DIRECTORY_INFO,
        THUMBNAILS_ARCHIVE,
        SYNCHRONIZING,
    }
    private static Response SUCCESS = null;
    private Type type;
    private final Map<String, Object> contents;

    public Response() {
        this.contents = new HashMap<>();
    }

    public Response(Type type) {
        this.type = type;
        this.contents = new HashMap<>();
    }

    public static Response success() {
        if (SUCCESS == null)
            SUCCESS = new Response(Type.SUCCESS);

        return SUCCESS;
    }

    public static class DirectoryInfo extends Response {
        private String directoryName;
        private long directorySize;

        public DirectoryInfo() {
            super(Type.DIRECTORY_INFO);
        }

        public DirectoryInfo(String directoryName, long directorySize) {
            this();
            this.directoryName = directoryName;
            this.directorySize = directorySize;
        }

        public String getDirectoryName() {
            return directoryName;
        }

        public void setDirectoryName(String directoryName) {
            this.directoryName = directoryName;
        }

        public long getDirectorySize() {
            return directorySize;
        }

        public void setDirectorySize(long directorySize) {
            this.directorySize = directorySize;
        }
    }

    public static class Synchronizing extends Response {
        private List<Change> changes;

        public Synchronizing() {
            super(Type.SYNCHRONIZING);
        }

        public Synchronizing(List<Change> changes) {
            this();
            this.changes = changes;
        }

        public List<Change> getChanges() {
            return changes;
        }

        public void setChanges(List<Change> changes) {
            this.changes = changes;
        }
    }

    public static class ThumbnailsArchive extends Response {
        private byte[] thumbnailsZip;

        public ThumbnailsArchive() {
            super(Type.THUMBNAILS_ARCHIVE);
        }

        public ThumbnailsArchive(byte[] zip) {
            this();
            this.thumbnailsZip = zip;
        }

        public byte[] getThumbnailsZip() {
            return thumbnailsZip;
        }

        public void setThumbnailsZip(byte[] thumbnailsZip) {
            this.thumbnailsZip = thumbnailsZip;
        }
    }

    public static class SessionCreated extends Response {
        private String sessionId;

        public SessionCreated() {
            super(Type.SESSION_CREATED);
        }

        public SessionCreated(String sessionId) {
            this();
            this.sessionId = sessionId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    public static class Error extends Response {
        private Status status;

        public enum Status {
            INVALID_REQUEST,
            INVALID_METHOD,
            INVALID_ARGUMENT,
            FILE_NOT_FOUND,
            FILE_ALREADY_EXISTS,
            NON_EXISTENT_SESSION,
            NOT_AN_INITIATOR,
            NOT_A_VIEWER,
            CLIENT_NOT_CONNECTED,
            CLIENT_ALREADY_CONNECTED,
            EXCEPTION
        }

        public Error() {
            super(Type.ERROR);
        }

        public Error(Status status) {
            this();
            this.status = status;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status type) {
            this.status = type;
        }
    }

    @JsonGetter("type")
    public Type getType() {
        return type;
    }

    @JsonSetter("type")
    public void setType(Type type) {
        this.type = type;
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return contents;
    }

    @JsonAnySetter
    public void setProperty(String prop, Object value) {
        this.contents.put(prop, value);
    }
}
