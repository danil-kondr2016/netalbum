package ru.danilakondr.netalbum.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sessions")
public class NetAlbumSession {
	@Id
	@Column(name="sessionId")
	private String sessionId;
	
	@Column(name="directoryName")
	private String directoryName;
	
	public String getSessionId() {
		return sessionId;
	}
	
	public String getDirectoryName() {
		return directoryName;
	}
	
	public void setSessionId(String id) {
		this.sessionId = id;
	}
	
	public void setDirectoryName(String name) {
		this.directoryName = name;
	}
}
