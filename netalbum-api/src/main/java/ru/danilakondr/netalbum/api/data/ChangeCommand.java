package ru.danilakondr.netalbum.api.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Класс-держатель параметра изменения. Содержит поле {@code type}, которое
 * обозначает тип изменения.
 * <p>Типы изменений:
 * <ul>
 * <li>{@code ADD_FOLDER}: создать папку</li>
 * <li>{@code RENAME}: переименовать</li>
 * </ul>
 * 
 * @author Данила А. Кондратенко
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="type", visible=true)
@JsonSubTypes({
    @JsonSubTypes.Type(name="ADD_FOLDER", value=ChangeCommand.AddFolder.class),
    @JsonSubTypes.Type(name="RENAME", value=ChangeCommand.Rename.class),
})
public class ChangeCommand {
    public enum Type {
        ADD_FOLDER,
        RENAME,
    }
    private Type type;
    
    public ChangeCommand() {}
    
    public ChangeCommand(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @JsonPropertyOrder({"type", "fileId", "folderName"})
    public static class AddFolder extends ChangeCommand {
        private long fileId;
        private String folderName;
        
        public AddFolder() {
            super(Type.ADD_FOLDER);
        }

        public String getFolderName() {
            return folderName;
        }

        public void setFolderName(String folderName) {
            this.folderName = folderName;
        }

        public long getFileId() {
            return fileId;
        }

        public void setFileId(long fileId) {
            this.fileId = fileId;
        }
    }
    
    @JsonPropertyOrder({"type", "fileId", "newName"})
    public static class Rename extends ChangeCommand {
        private long fileId;
        private String newName;
        
        public Rename() {
            super(Type.RENAME);
        }

        public void setNewName(String name) {
            this.newName = name;
        }

        public void setFileId(long fileId) {
            this.fileId = fileId;
        }
        
        public String getNewName() {
            return newName;
        }

        public long getFileId() {
            return fileId;
        }
        
    }
}
