package ru.danilakondr.netalbum.server.model;

import javax.persistence.*;

@Entity
@Table(name="contents")
public class ImageFile {
    public enum Type {
        FILE,
        DIRECTORY;
    }
    
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
    
    @Column(name="fileType")
    @Enumerated(value = EnumType.STRING)
    private Type fileType;

    public long getFileSize() {
        return fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getFileId() {
        return fileId;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public Type getFileType() {
        return fileType;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public void setFileType(Type fileType) {
        this.fileType = fileType;
    }
}
