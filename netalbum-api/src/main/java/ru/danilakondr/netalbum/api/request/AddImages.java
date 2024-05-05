package ru.danilakondr.netalbum.api.request;

import java.util.List;

import ru.danilakondr.netalbum.api.ImageData;

public class AddImages {
	private List<ImageData> images;
	
	public void setImages(List<ImageData> data) {
		this.images = data;
	}
	
	public List<ImageData> getImages() {
		return images;
	}
}
