package ru.danilakondr.netalbum.server.model;

import javax.persistence.*;

@Entity
@Table(name="contents")
public class ImageFile {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="fileId")
	private long fileId;
	
	@Column(name="sessionId")
	private String sessionId;
	
	@Column(name="fileName")
	private String fileName;
	
	@Column(name="fileSize")
	private long fileSize;
	
	@Column(name="imgWidth")
	private int imgWidth;
	
	@Column(name="imgHeight")
	private int imgHeight;
	
	@Column(name="thumbnail")
	private byte[] thumbnail;
}
