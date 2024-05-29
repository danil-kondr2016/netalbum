/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import ru.danilakondr.netalbum.api.data.Change;

/**
 *
 * @author danko
 */
@Entity
@Table(name="change_queue")
public class ChangeQueueRecord {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="changeId")
    private long changeId;
    
    @Column(name="changeType")
    @Enumerated(EnumType.STRING)
    private Change.Type changeType;
    
    @Column(name="oldName")
    private String oldName;
    
    @Column(name="newName")
    private String newName;
    
    @Column(name="sessionId")
    private String sessionId;

    public long getChangeId() {
        return changeId;
    }

    public void setChangeId(long changeId) {
        this.changeId = changeId;
    }

    public Change.Type getChangeType() {
        return changeType;
    }

    public void setChangeType(Change.Type changeType) {
        this.changeType = changeType;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
