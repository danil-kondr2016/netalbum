package ru.danilakondr.netalbum.server.error;

public class FileAlreadyExistsError extends RuntimeException {
    public FileAlreadyExistsError(String message) {
        super(message);
    }
}
