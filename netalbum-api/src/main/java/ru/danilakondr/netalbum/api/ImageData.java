package ru.danilakondr.netalbum.api;

import java.util.Base64;
import java.util.Map;

public class ImageData extends ImageInfo {
	private byte[] thumbnail;
	
	public byte[] getThumbnail() {
		return thumbnail;
	}
	
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}
}
