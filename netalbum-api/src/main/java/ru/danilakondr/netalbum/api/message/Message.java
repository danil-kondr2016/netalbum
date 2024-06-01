/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.api.message;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author danko
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value=Request.class, name="REQUEST"),
    @JsonSubTypes.Type(value=Response.class, name="RESPONSE"),
    @JsonSubTypes.Type(value=Message.class, name="CONNECTION_ESTABLISHED"),
    @JsonSubTypes.Type(value=Message.class, name="CONNECTION_CLOSED"),
    @JsonSubTypes.Type(value=Message.class, name="CONNECTION_FAILED"),
})
@JsonPropertyOrder({"type"})
public class Message {
    public enum Type {
        REQUEST,
        RESPONSE,
        CONNECTION_ESTABLISHED,
        CONNECTION_CLOSED,
        CONNECTION_FAILED,
    };
    
    private Type type;
    private final Map<String, Object> contents;

    public Message() {
        this.contents = new HashMap<>();
    }

    public Message(Type type) {
        this.type = type;
        this.contents = new HashMap<>();
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return contents;
    }
    
    public Object getProperty(String prop) {
        return contents.get(prop);
    }

    @JsonAnySetter
    public void setProperty(String prop, Object value) {
        this.contents.put(prop, value);
    }
}
