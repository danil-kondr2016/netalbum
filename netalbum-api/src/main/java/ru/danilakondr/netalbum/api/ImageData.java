package ru.danilakondr.netalbum.api;

public class ImageData extends ImageInfo {
	private byte[] thumbnail;
	
	public byte[] getThumbnail() {
		return thumbnail;
	}
	
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}
}
