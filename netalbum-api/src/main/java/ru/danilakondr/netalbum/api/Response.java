package ru.danilakondr.netalbum.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

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

    public static Response withMessage(Status status, String message) {
        Response response = new Response(status);
        response.setProperty("message", message);
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
