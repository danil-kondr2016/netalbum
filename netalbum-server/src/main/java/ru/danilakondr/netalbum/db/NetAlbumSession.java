package ru.danilakondr.netalbum.db;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="sessions")
public class NetAlbumSession {
	@Id
	@Column(name="sessionId")
	private String sessionId;
	
	@Column(name="directoryName")
	private String directoryName;

	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="sessionId")
	private List<ImageFile> files;
	
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

	public List<ImageFile> getFiles() {
		return files;
	}

	public void setFiles(List<ImageFile> files) {
		this.files = files;
	}
}
