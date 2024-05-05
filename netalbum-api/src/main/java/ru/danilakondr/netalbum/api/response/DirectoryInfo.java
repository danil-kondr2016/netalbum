package ru.danilakondr.netalbum.api.response;

public class DirectoryInfo {
	private String directoryName;
	private long directorySize;
	
	public DirectoryInfo() {}
	
	public String getDirectoryName() {
		return directoryName;
	}
	
	public void setDirectoryName(String name) {
		this.directoryName = name;
	}
	
	public long getDirectorySize() {
		return directorySize;
	}
	
	public void setDirectorySize(long size) {
		this.directorySize = size;
	}
}
