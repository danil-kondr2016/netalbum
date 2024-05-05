package ru.danilakondr.netalbum.api.response;

import java.util.List;

import ru.danilakondr.netalbum.api.ImageData;

public class DirectoryContents {
	private List<ImageData> directoryContents;
	
	public void setDirectoryContents(List<ImageData> data) {
		this.directoryContents = data;
	}
	
	public List<ImageData> getDirectoryContents() {
		return directoryContents;
	}
}
