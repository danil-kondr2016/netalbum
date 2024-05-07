package ru.danilakondr.netalbum.api;

import java.util.Map;

public class ImageData extends ImageInfo {
	private byte[] thumbnail;
	
	public byte[] getThumbnail() {
		return thumbnail;
	}
	
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public static ImageData fromMap(Map<String, Object> imageData) {
		if (!imageData.containsKey("fileName"))
			throw new IllegalArgumentException("file name has not been specified");
		if (!imageData.containsKey("fileSize"))
			throw new IllegalArgumentException("file size has not been specified");
		if (!imageData.containsKey("width"))
			throw new IllegalArgumentException("image width has not been specified");
		if (!imageData.containsKey("height"))
			throw new IllegalArgumentException("image height has not been specified");
		if (!imageData.containsKey("thumbnail"))
			throw new IllegalArgumentException("thumbnail has not been specified");

		ImageData result = new ImageData();
		try {
			result.setFileName((String) imageData.get("fileName"));
		}
		catch (ClassCastException e) {
			throw new IllegalArgumentException("file name is not a string");
		}

		try {
			result.setFileSize((Long) imageData.get("fileSize"));
		}
		catch (ClassCastException e) {
			throw new IllegalArgumentException("file size is not a number");
		}

		try {
			result.setWidth((Integer) imageData.get("width"));
		}
		catch (ClassCastException e) {
			throw new IllegalArgumentException("width is not a number");
		}

		try {
			result.setHeight((Integer) imageData.get("height"));
		}
		catch (ClassCastException e) {
			throw new IllegalArgumentException("height is not a number");
		}

		try {
			result.setThumbnail((byte[]) imageData.get("thumbnail"));
		}
		catch (ClassCastException e) {
			throw new IllegalArgumentException("thumbnail is not a string");
		}

		return result;
	}
}
