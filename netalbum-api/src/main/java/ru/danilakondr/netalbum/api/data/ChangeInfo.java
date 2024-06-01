/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.api.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Objects;

/**
 *
 * @author danko
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="type", visible=true)
@JsonSubTypes({
    @JsonSubTypes.Type(name="ADD_FOLDER", value=ChangeInfo.AddFolder.class),
    @JsonSubTypes.Type(name="RENAME", value=ChangeInfo.Rename.class),
})
@JsonPropertyOrder({"type"})
public abstract class ChangeInfo {
    private ChangeCommand.Type type;
    protected ChangeInfo() {}
    protected ChangeInfo(ChangeCommand.Type type) {
        this.type = type;
    }
    
    public ChangeCommand.Type getType() {
        return type;
    }

    protected void setType(ChangeCommand.Type type) {
        this.type = type;
    }
    
    public abstract String getOldName();
    
    public abstract void setOldName(String name);
    
    public abstract String getNewName();
    
    public abstract void setNewName(String name);
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj == this)
            return true;

        if (!(obj instanceof ChangeInfo))
            return false;

        ChangeInfo obj1 = (ChangeInfo)obj;
        if (getType() != obj1.getType())
            return false;
        if (Objects.equals(getOldName(), obj1.getOldName()))
            return false;
        if (Objects.equals(getNewName(), obj1.getNewName()))
            return false;

        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(getOldName());
        hash = 37 * hash + Objects.hashCode(getNewName());
        return hash;
    }
    
    @JsonPropertyOrder({"type", "oldName", "newName"})
    public static class Rename extends ChangeInfo { 
        private String oldName, newName;
        
        public Rename() {
            super(ChangeCommand.Type.RENAME);
        };
        
        public Rename(String oldName, String newName) {
            super(ChangeCommand.Type.RENAME);
            this.oldName = oldName;
            this.newName = newName;
        }

        @Override
        public String getOldName() {
            return oldName;
        }

        @Override
        public String getNewName() {
            return newName;
        }

        @Override
        public void setOldName(String oldName) {
            this.oldName = oldName;
        }

        @Override
        public void setNewName(String newName) {
            this.newName = newName;
        }

        @Override
        public String toString() {
            return "Rename[ " + getOldName() + " -> " + getNewName() + " ]";
        }
    }

    @JsonPropertyOrder({"type", "newName"})
    public static class AddFolder extends ChangeInfo {
        private String newName;
        
        public AddFolder() {
            super(ChangeCommand.Type.ADD_FOLDER);
        }
        
        public AddFolder(String newName) {
            super(ChangeCommand.Type.ADD_FOLDER);
            this.newName = newName;
        }
        
        @Override
        public String getOldName() {
            return null;
        }

        @Override
        public String getNewName() {
            return newName;
        }
        
        @Override
        public void setNewName(String newName) {
            this.newName = newName;
        }
        
        @Override
        public void setOldName(String newName) {
            
        }

        @Override
        public String toString() {
            return "AddFolder[" + getNewName() + "]";
        }
    }
}
