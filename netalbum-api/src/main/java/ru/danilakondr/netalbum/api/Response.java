package ru.danilakondr.netalbum.api;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Response {
    private Status status;
    private Map<String, Object> contents;

    public Response() {}

    public Response(Status status) {
        this.status = status;
        this.contents = new HashMap<>();
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
