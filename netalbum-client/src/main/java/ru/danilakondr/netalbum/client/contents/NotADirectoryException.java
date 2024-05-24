package ru.danilakondr.netalbum.client.contents;

import java.nio.file.Path;

/**
 * Thrown when an operation that should be executed on folders is attempted
 * on a file {@link FileSystemNode FileSystemNode}.
 */
public class NotADirectoryException extends RuntimeException {
    private final Path offendingFile;

    public NotADirectoryException(Path offendingFile) {
        this.offendingFile = offendingFile;
    }

    public NotADirectoryException(String message, Path offendingFile) {
        super(message);
        this.offendingFile = offendingFile;
    }

    public NotADirectoryException(Throwable cause, Path offendingFile) {
        super(cause);
        this.offendingFile = offendingFile;
    }

    public NotADirectoryException(String message,
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

        buffer.append("Unable to get child of Path: [");
        buffer.append(getOffendingFile());
        buffer.append(']');
        return buffer.toString();
    }
}
