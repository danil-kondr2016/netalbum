/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.contents;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author danko
 */
public abstract class FileSystemTreeNode implements TreeNode, MutableTreeNode {
    protected FileSystemTreeNode parent;
    protected Path path;
    
    protected FileSystemTreeNode(FileSystemTreeNode parent, Path path) {
        this.parent = parent;
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof FileSystemTreeNode)) {
            return false;
        }

        FileSystemTreeNode other = (FileSystemTreeNode) obj;
        return path.equals(other.path);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.path);
        return hash;
    }

    @Override
    public String toString() {
        if (path == null)
            return "";
        
        Path fileName = path.getFileName();
        if (fileName == null)
            return "";
        
        return fileName.toString();
    }
    
    public void add(FileSystemTreeNode node) {
        insert(node, getChildCount());
    }

    @Override
    public abstract void insert(MutableTreeNode child, int index);

    @Override
    public abstract void remove(int index);

    @Override
    public abstract void remove(MutableTreeNode node);

    @Override
    public void setUserObject(Object object) {
        if (object == null)
            this.path = null;
        
        if (!(object instanceof Path))
            throw new IllegalArgumentException("object is not an instance of Path");
        
        this.path = (Path)object;
    }
    
    public void setPathObject(Path path) {
        setUserObject(path);
    }
    
    public Path getPathObject() {
        return this.path;
    }
    
    public abstract boolean isFile();
    
    public abstract boolean isDirectory();
    
    public String getFileName() {
        return path.getFileName().toString();
    }

    @Override
    public void removeFromParent() {
        if (parent != null)
            parent.remove(this);
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        if (newParent == null)
            this.parent = null;
        
        if (!(newParent instanceof FileSystemTreeNode))
            throw new IllegalArgumentException("new parent is not a FileSystemNode");
        
        this.parent = (FileSystemTreeNode) newParent;
    }

    @Override
    public abstract TreeNode getChildAt(int childIndex);

    @Override
    public abstract int getChildCount();

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public abstract int getIndex(TreeNode node);

    @Override
    public abstract boolean getAllowsChildren();

    @Override
    public abstract boolean isLeaf();

    @Override
    public abstract Enumeration<? extends TreeNode> children();
    
    private static final class File extends FileSystemTreeNode {
        public File(FileSystemTreeNode parent, Path path) {
            super(parent, path);
        }

        @Override
        public void insert(MutableTreeNode child, int index) {
            throw new NotADirectoryException(path);
        }

        @Override
        public void remove(int index) {
            throw new NotADirectoryException(path);
        }

        @Override
        public void remove(MutableTreeNode node) {
            throw new NotADirectoryException(path);
        }

        @Override
        public boolean isFile() {
            return true;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            throw new NotADirectoryException(path);
        }

        @Override
        public int getChildCount() {
            throw new NotADirectoryException(path);
        }

        @Override
        public int getIndex(TreeNode node) {
            throw new NotADirectoryException(path);
        }

        @Override
        public boolean getAllowsChildren() {
            return false;
        }

        @Override
        public boolean isLeaf() {
            return true;
        }

        @Override
        public Enumeration<? extends TreeNode> children() {
            throw new NotADirectoryException(path);
        }
        
    }
    
    private static final class FileComparator implements Comparator<FileSystemTreeNode> {
        public static final FileComparator INSTANCE = new FileComparator();

        @Override
        public int compare(FileSystemTreeNode p1, FileSystemTreeNode p2) {
            if (null == p1 && null == p2) {
                return 0;
            } else if (null == p1) {
                return -1;
            } else if (null == p2) {
                return 1;
            }

            if (p1.isDirectory() && !p2.isDirectory()) {
                return -1;
            } else if (!p1.isDirectory() && p2.isDirectory()) {
                return 1;
            } else {
                return p1.toString().compareToIgnoreCase(p2.toString());
            }  
        }
    }

    private static final class Directory extends FileSystemTreeNode {
        private final List<FileSystemTreeNode> children;
        
        public Directory(FileSystemTreeNode parent, Path path) {
            super(parent, path);
            children = new ArrayList<>();
        }

        @Override
        public void insert(MutableTreeNode child, int index) {
            Objects.requireNonNull(child);
            
            FileSystemTreeNode file = (FileSystemTreeNode)child;
            file.setParent(this);
            children.add(index, file);
            Collections.sort(children, FileComparator.INSTANCE);
        }

        @Override
        public void remove(int index) {
            FileSystemTreeNode file = children.get(index);
            
            file.setParent(null);
            children.remove(index);
            Collections.sort(children, FileComparator.INSTANCE);
        }

        @Override
        public void remove(MutableTreeNode node) {
            FileSystemTreeNode file = (FileSystemTreeNode)node;
            
            file.setParent(null);
            children.remove((FileSystemTreeNode)node);
            Collections.sort(children, FileComparator.INSTANCE);
        }

        @Override
        public boolean isFile() {
            return false;
        }

        @Override
        public boolean isDirectory() {
            return true;
        }

        @Override
        public TreeNode getChildAt(int childIndex) {
            return children.get(childIndex);
        }

        @Override
        public int getChildCount() {
            return children.size();
        }

        @Override
        public int getIndex(TreeNode node) {
            return children.indexOf((FileSystemTreeNode)node);
        }

        @Override
        public boolean getAllowsChildren() {
            return true;
        }

        @Override
        public boolean isLeaf() {
            return children.isEmpty();
        }

        @Override
        public Enumeration<? extends TreeNode> children() {
            return Collections.enumeration(children);
        }
    }

    public static FileSystemTreeNode create(FileSystemTreeNode parent, Path path) {
        if (Files.isDirectory(path))
            return new Directory(parent, path);
        else
            return new File(parent, path);
    }
    
    public static FileSystemTreeNode create(Path path) {
        return create(null, path);
    }

    public static FileSystemTreeNode buildTree(Path path) {
        FileSystemTreeNode root = FileSystemTreeNode.create(path);

        try {
            Files.walkFileTree(path, Set.of(), 1, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isDirectory())
                        root.add(FileSystemTreeNode.buildTree(file));
                    else
                        root.add(FileSystemTreeNode.create(file));
                    
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.TERMINATE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null)
                        return FileVisitResult.TERMINATE;

                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException ignored) {}
        finally {
            return root;
        }
    }
}
