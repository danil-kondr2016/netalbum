package ru.danilakondr.netalbum.api;

public class ImageInfo {
	private String fileName;
	private int width;
	private int height;
	private long fileSize;

	public ImageInfo() {
		super();
	}

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

}