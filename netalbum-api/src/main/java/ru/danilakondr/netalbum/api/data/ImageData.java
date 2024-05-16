package ru.danilakondr.netalbum.api.data;

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
public class ImageData extends ImageInfo {
	private byte[] thumbnail;
	
	public byte[] getThumbnail() {
		return thumbnail;
	}
	
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}
}
