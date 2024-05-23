package ru.danilakondr.fstreemodel;

import java.io.File;
import java.nio.file.Path;

/**
 * Thrown when an operation that should be executed on folders is attempted
 * on a file {@link FileSystemTreeNode FileSystemTreeNode}.
 */
public class NotAFolderException extends RuntimeException {
    private final Path offendingFile;

    public NotAFolderException(Path offendingFile) {
        this.offendingFile = offendingFile;
    }

    public NotAFolderException(String message, Path offendingFile) {
        super(message);
        this.offendingFile = offendingFile;
    }

    public NotAFolderException(Throwable cause, Path offendingFile) {
        super(cause);
        this.offendingFile = offendingFile;
    }

    public NotAFolderException(String message,
                               Throwable cause,
                               Path offendingFile) {
        super(message, cause);
        this.offendingFile = offendingFile;
    }

    public Path getOffendingFile() {
        return offendingFile;
    }

    @Override
    public String getMessage() {
        StringBuilder buffer = new StringBuilder();
        if (null != super.getMessage()) {
            buffer.append(super.getMessage());
            buffer.append(": ");
        }

        buffer.append("Unable to get child of File: [");
        buffer.append(getOffendingFile());
        buffer.append(']');
        return buffer.toString();
    }
}
