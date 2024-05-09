package ru.danilakondr.netalbum.api;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private RequestType method;
    private Map<String, Object> contents;

    public Request() {}

    public Request(RequestType method) {
        this.method = method;
        this.contents = new HashMap<>();
    }

    @JsonGetter("method")
    public RequestType getMethod() {
        return method;
    }

    @JsonSetter("method")
    public void setMethod(RequestType method) {
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
}
