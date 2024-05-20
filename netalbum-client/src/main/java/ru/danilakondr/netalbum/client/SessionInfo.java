/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client;

/**
 *
 * @author danko
 */
public class SessionInfo {
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
        this.url = url;
    }
   
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
}
