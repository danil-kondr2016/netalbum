package ru.danilakondr.netalbum.api.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import static ru.danilakondr.netalbum.api.data.Change.Type.RENAME_DIR;
import static ru.danilakondr.netalbum.api.data.Change.Type.RENAME_FILE;

/**
 * Класс-держатель параметра изменения. Содержит два поля:
 * <ul>
 *     <li>{@code oldName}: старое имя</li>
 *     <li>{@code newName}: новое имя (значение null означает, что файл удалён)</li>
 * </ul>
 *
 * @author Данила А. Кондратенко
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="type", visible=true)
@JsonSubTypes({
    @JsonSubTypes.Type(name="ADD_FOLDER", value=Change.AddFolder.class),
    @JsonSubTypes.Type(name="RENAME_FILE", value=Change.RenameFile.class),
    @JsonSubTypes.Type(name="RENAME_DIR", value=Change.RenameDir.class)
})
public class Change {
    public enum Type {
        ADD_FOLDER,
        RENAME_FILE,
        RENAME_DIR,
    }
    private Type type;
    
    public Change() {}
    
    public Change(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    public static class AddFolder extends Change {
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
    }
    
    @JsonPropertyOrder({"type", "oldName", "newName"})
    public static abstract class Rename extends Change {
        private String oldName;
        private String newName;
        
        private static Type checkType(Type type) {
            if (type != RENAME_FILE && type != RENAME_DIR)
                throw new IllegalArgumentException("Invalid type " + type);
            
            return type;
        }
        
        protected Rename(Type type) {
            super(checkType(type));
        }

        public void setOldName(String name) {
            this.oldName = name;
        }

        public void setNewName(String name) {
            this.newName = name;
        }

        public String getOldName() {
            return oldName;
        }

        public String getNewName() {
            return newName;
        }
    }

    public static class RenameFile extends Rename {
        public RenameFile() {
            super(Type.RENAME_FILE);
        }
    }
    
    public static class RenameDir extends Rename {
        public RenameDir() {
            super(Type.RENAME_DIR);
        }
    }
}
