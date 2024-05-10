package ru.danilakondr.netalbum.api.error;

public class FileNotFoundError extends RuntimeException {
    public FileNotFoundError(String message) {
        super(message);
    }
}
