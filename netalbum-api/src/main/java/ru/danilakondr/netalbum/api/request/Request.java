package ru.danilakondr.netalbum.api.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private Type method;
    private Map<String, Object> contents;

    public Request() {
        this.contents = new HashMap<>();
    }

    public Request(Type method) {
        this.method = method;
        this.contents = new HashMap<>();
    }

    @JsonGetter("method")
    public Type getMethod() {
        return method;
    }

    @JsonSetter("method")
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
