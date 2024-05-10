package ru.danilakondr.netalbum.api.error;

public class FileAlreadyExistsError extends RuntimeException {
    public FileAlreadyExistsError(String message) {
        super(message);
    }
}
