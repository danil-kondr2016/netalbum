/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.contents;

import java.util.Arrays;
import java.util.Objects;
import javax.swing.tree.DefaultMutableTreeNode;
import ru.danilakondr.netalbum.api.data.FileInfo;

/**
 *
 * @author danko
 */
public abstract class FolderContentNode extends DefaultMutableTreeNode {
    private FileInfo fileInfo;
    
    private static String getLastElement(String path) {
        if (path.isEmpty())
            return "";

        String[] fragments = path.split("/");
        return fragments[fragments.length - 1];
    }
    
    protected FolderContentNode(Object userObject) {
        super(userObject);
    }
    
    protected FolderContentNode(FileInfo info) {
        super(getLastElement(info.getFileName()));
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public String[] getCurrentPath() {
        return 
                Arrays.stream(getPath())
                .filter(n -> !Objects.equals(n, getRoot()))
                .map(n -> Objects.toString(n))
                .toArray(String[]::new);
    }
    
    public abstract boolean isDirectory();
    
    public abstract boolean isImage();
    
    @Override
    public abstract boolean isLeaf();
    
    @Override
    public abstract boolean getAllowsChildren();
    
    private static final class Image extends FolderContentNode {
        public Image(FileInfo info) {
            super(info);
        }
        
        public Image(String fileName) {
            super(fileName);
        }
        
        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public boolean isImage() {
            return true;
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

        @Override
        public boolean getAllowsChildren() {
            return false;
        }
    }
    
    private static final class Directory extends FolderContentNode {
        public Directory(String name) {
            super(name);
        }

        @Override
        public boolean isDirectory() {
            return true;
        }

        @Override
        public boolean isImage() {
            return false;
        }

        @Override
        public boolean isLeaf() {
            return false;
        }

        @Override
        public boolean getAllowsChildren() {
            return true;
        }
    }

    public static FolderContentNode createImage(FileInfo info) {
        return new Image(info);
    }
    
    public static FolderContentNode createImage(String fileName) {
        return new Image(fileName);
    }
    
    public static FolderContentNode createDirectory(String name) {
        return new Directory(name);
    }
}
