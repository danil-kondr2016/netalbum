/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.contents;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import ru.danilakondr.netalbum.api.data.ImageInfo;

/**
 *
 * @author danko
 */
public abstract class FolderContentNode extends DefaultMutableTreeNode {
    protected FolderContentNode(Object userObject) {
        super(userObject);
    }
    
    public abstract boolean isDirectory();
    
    public abstract boolean isImage();
    
    public abstract ImageInfo getImageInfo();
    
    public abstract void setImageInfo(ImageInfo info);
  
    private static final class Image extends FolderContentNode {
        private ImageInfo info;
        
        private static String getLastElement(String path) {
            if (path.isEmpty())
                return "";
            
            String[] fragments = path.split("/");
            return fragments[fragments.length - 1];
        }
        
        public Image(ImageInfo info) {
            super(getLastElement(info.getFileName()));
            this.info = info;
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
        public ImageInfo getImageInfo() {
            return info;
        }

        @Override
        public void setImageInfo(ImageInfo info) {
            this.info = info;
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
        public ImageInfo getImageInfo() {
            throw new UnsupportedOperationException("Not an image");
        }

        @Override
        public void setImageInfo(ImageInfo info) {
            throw new UnsupportedOperationException("Not an image");
        }
    }

    public static FolderContentNode createImage(ImageInfo info) {
        return new Image(info);
    }
    
    public static FolderContentNode createImage(String fileName) {
        return new Image(fileName);
    }
    
    public static FolderContentNode createDirectory(String name) {
        return new Directory(name);
    }
}
