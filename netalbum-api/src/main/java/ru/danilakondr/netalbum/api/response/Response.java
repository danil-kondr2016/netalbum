package ru.danilakondr.netalbum.api.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import ru.danilakondr.netalbum.api.data.Change;
import ru.danilakondr.netalbum.api.request.Request;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="status", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value=Response.DirectoryInfo.class, name="DIRECTORY_INFO"),
        @JsonSubTypes.Type(value=Response.Synchronizing.class, name="SYNCHRONIZING"),
        @JsonSubTypes.Type(value=Response.ThumbnailsArchive.class, name="THUMBNAILS_ARCHIVE"),
        @JsonSubTypes.Type(value=Response.SessionCreated.class, name="SESSION_CREATED"),
})
public class Response {
    private static Response SUCCESS = null;
    private Status status;
    private Map<String, Object> contents;

    public Response() {
        this.contents = new HashMap<>();
    }

    public Response(Status status) {
        this.status = status;
        this.contents = new HashMap<>();
    }

    public static Response success() {
        if (SUCCESS == null)
            SUCCESS = new Response(Status.SUCCESS);

        return SUCCESS;
    }

    public static class DirectoryInfo extends Response {
        private String directoryName;
        private long directorySize;

        public DirectoryInfo() {
            super(Status.DIRECTORY_INFO);
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
            super(Status.SYNCHRONIZING);
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
            super(Status.THUMBNAILS_ARCHIVE);
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
            super(Status.SESSION_CREATED);
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    public static Response withMessage(Status status, String message) {
        Response response = new Response(status);
        response.setProperty("message", message);
        return response;
    }

    public static Response invalidRequest(String reason) {
        Response response = new Response(Status.INVALID_REQUEST);
        response.setProperty("reason", reason);
        return response;
    }

    public static Response invalidArgument(String reason) {
        Response response = new Response(Status.INVALID_ARGUMENT);
        response.setProperty("reason", reason);
        return response;
    }

    public static Response invalidMethod(String method) {
        Response response = new Response(Status.INVALID_METHOD);
        response.setProperty("method", method);
        return response;
    }

    public static Response fileNotFound(String path) {
        Response response = new Response(Status.FILE_NOT_FOUND);
        response.setProperty("fileName", path);
        return response;
    }

    public static Response fileAlreadyExists(String path) {
        Response response = new Response(Status.FILE_ALREADY_EXISTS);
        response.setProperty("fileName", path);
        return response;
    }

    public static Response nonExistentSession(String sessionId) {
        Response response = new Response(Status.NON_EXISTENT_SESSION);
        response.setProperty("sessionId", sessionId);
        return response;
    }

    public static Response directoryInfo(String name, long size) {
        Response response = new Response(Status.SUCCESS);
        response.setProperty("directoryName", name);
        response.setProperty("directorySize", size);

        return response;
    }

    public static Response quit() {
        return new Response(Status.SESSION_EXITS);
    }

    public static Response synchronizing(List<Change> changes) {
        Response response = new Response(Status.SYNCHRONIZING);
        response.setProperty("changes", changes);

        return response;
    }

    @JsonGetter("status")
    public Status getStatus() {
        return status;
    }

    @JsonSetter("status")
    public void setStatus(Status status) {
        this.status = status;
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
