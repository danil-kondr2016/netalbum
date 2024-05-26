package ru.danilakondr.netalbum.api.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Класс-держатель данных изображения. Содержит поля:
 * <ul>
 *     <li>{@code fileName}: имя файла</li>
 *     <li>{@code fileSize}: размер файла</li>
 *     <li>{@code width}: ширина</li>
 *     <li>{@code height}: высота</li>
 *     <li>{@code thumbnail}: уменьшенная картинка</li>
 * </ul>
 *
 * @author Данила А. Кондратенко
 */
@JsonPropertyOrder({"fileName", "fileSize", "width", "height", "thumbnail"})
public class ImageData {
    private String fileName;
    private long fileSize;
    private int width;
    private int height;
    private byte[] thumbnail;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
    public byte[] getThumbnail() {
        return thumbnail;
    }
    
    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }
}
