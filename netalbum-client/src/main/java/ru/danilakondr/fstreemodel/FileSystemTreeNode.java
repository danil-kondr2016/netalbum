package ru.danilakondr.fstreemodel;

import java.io.File;
import java.util.Arrays;

/**
 * A tree node that is based on the underlying file system.
 */
public abstract class FileSystemTreeNode {
    protected final File location;

    private FileSystemTreeNode(File location) {
        this.location = location;
    }

    /**
     * Returns the {@link File File} instance behind this node.
     *
     * @return The {#link File File} instance behind this node.
     */
    public File getFile() {
        return location;
    }

    /**
     * Returns the number of child {@link File File} instances under this
     * node.
     *
     * @return The number of child folders and files under this node.
     * @throws NotAFolderException If this node returns <code>true</code> for
     *                             {@link #isFile()}.
     */
    public abstract int getChildCount() throws NotAFolderException;

    /**
     * Returns the node at the specified index.
     * <p>The child {@link File File} instances are returned in order,
     * according to {@link FileComparator FileComparator}.</p>
     *
     * @param index Must be <code>0 <= index <= {@link #getChildCount() getChildCount() - 1}</code>
     * @return A {@link FileSystemTreeNode FileSystemTreeNode} instance
     *         representing the node at the specified index.
     * @throws NotAFolderException If this node returns <code>true</code> for
     *                             {@link #isFile()}.
     */
    public abstract FileSystemTreeNode getChildAt(int index)
            throws NotAFolderException;

    public abstract boolean isFile();

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    /**
     * Two nodes are equal if their underlying {@link File File} instances are
     * equal.
     *
     * @param obj
     * @return <code>true</code> if the two nodes represent the same
     *         file system location, <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof FileSystemTreeNode)) {
            return false;
        }

        FileSystemTreeNode other = (FileSystemTreeNode) obj;
        return location.equals(other.location);
    }

    @Override
    public String toString() {
        return location.getName();
    }

    /**
     * Public factory for creating
     * {@link FileSystemTreeNode FileSystemTreeNode}s.
     *
     * @param location The file system location to which the node should be
     *                 attached.
     * @return A {@link FileSystemTreeNode FileSystemTreeNode} instance.
     */
    public static FileSystemTreeNode create(File location) {
        if (location.isDirectory()) {
            return new DirectoryTreeNode(location);
        } else {
            return new FileTreeNode(location);
        }
    }

    private static final class DirectoryTreeNode extends FileSystemTreeNode {
        private File[] children;

        public DirectoryTreeNode(File location) {
            super(location);
        }

        @Override
        public boolean isFile() {
            return false;
        }

        @Override
        public FileSystemTreeNode getChildAt(int index) {
            loadChildren();
            return FileSystemTreeNode.create(children[index]);
        }

        @Override
        public int getChildCount() {
            loadChildren();
            return children.length;
        }

        private synchronized void loadChildren() {
            if (null != children) return;

            children = location.listFiles();
            Arrays.sort(children, FileComparator.INSTANCE);
        }
    }

    private static final class FileTreeNode extends FileSystemTreeNode {
        public FileTreeNode(File location) {
            super(location);
        }

        @Override
        public boolean isFile() {
            return true;
        }

        @Override
        public FileSystemTreeNode getChildAt(int index) {
            throw new NotAFolderException(location);
        }

        @Override
        public int getChildCount() {
            throw new NotAFolderException(location);
        }
    }
}
