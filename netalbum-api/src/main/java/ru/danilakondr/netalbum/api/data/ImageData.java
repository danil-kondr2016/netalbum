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
public class ImageData extends ImageInfo {
    private byte[] thumbnail;
    
    public byte[] getThumbnail() {
        return thumbnail;
    }
    
    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }
}
