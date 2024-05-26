package ru.danilakondr.netalbum.api.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Класс-держатель данных о файле/папке. Содержит поля:
 * 
 * <ul>
 *  <li>{@code fileName}: имя файла</li>
 *  <li>{@code fileType}: тип файла</li>
 * </ul>
 * 
 * <p>Если тип файла равен {@link FileInfo.Type#FILE}, то добавляются следующие
 * поля:
 * <ul>
 *     <li>{@code fileSize}: размер файла</li>
 *     <li>{@code width}: ширина</li>
 *     <li>{@code height}: высота</li>
 * </ul>
 * 
 * @see FileInfo.Type
 */
@JsonPropertyOrder({"fileName", "fileType", "fileSize", "width", "height"})
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="fileType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(name="FILE", value=FileInfo.Image.class),
    @JsonSubTypes.Type(name="DIRECTORY", value=FileInfo.class)
})
public class FileInfo {
    /**
     * Тип файла. Возможны два значения: {@code FILE} и {@code DIRECTORY}.
     */
    public enum Type {
        /**
         * Файл (изображение).
         */
        FILE,
        /**
         * Папка.
         */
        DIRECTORY;
    }
    
    private String fileName;
    private Type fileType;

    public FileInfo() {
    }
    
    public FileInfo(Type type) {
        this.fileType = type;
    }

    public String getFileName() {
        return fileName;
    }

    public Type getFileType() {
        return fileType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileType(Type fileType) {
        this.fileType = fileType;
    }
    
    @JsonPropertyOrder({"fileName", "fileType", "fileSize", "width", "height"})
    public static class Image extends FileInfo {
        private long fileSize;
        private int width;
        private int height;
        
        public Image() {
            super(Type.FILE);
        }
        
        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setFileSize(long size) {
            this.fileSize = size;
        }
        
        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public long getFileSize() {
            return fileSize;
        }
    }
}