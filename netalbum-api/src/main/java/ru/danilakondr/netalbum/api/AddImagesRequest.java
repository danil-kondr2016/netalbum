package ru.danilakondr.netalbum.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public class AddImagesRequest extends Request {
    private List<ImageData> images;

    @JsonSetter("images")
    public void setImages(List<ImageData> data) {
        this.images = data;
    }

    @JsonGetter("images")
    public List<ImageData> getImages() {
        return images;
    }
}
