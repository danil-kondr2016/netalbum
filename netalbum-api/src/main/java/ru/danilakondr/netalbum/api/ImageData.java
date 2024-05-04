package ru.danilakondr.netalbum.api;

public class ImageData {
	private String fileName;
	private int width, height;
	private long fileSize;
	private byte[] thumbnail;
	
	public String getFileName() {
		return fileName;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public byte[] getThumbnail() {
		return thumbnail;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setFileSize(long size) {
		this.fileSize = size;
	}
	
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}
}
