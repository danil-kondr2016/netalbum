package ru.danilakondr.netalbum.api.data;

public class ImageData extends ImageInfo {
	private byte[] thumbnail;
	
	public byte[] getThumbnail() {
		return thumbnail;
	}
	
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}
}