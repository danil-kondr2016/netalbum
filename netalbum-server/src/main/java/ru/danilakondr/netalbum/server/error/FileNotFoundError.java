package ru.danilakondr.netalbum.server.error;

public class FileNotFoundError extends RuntimeException {
    public FileNotFoundError(String message) {
        super(message);
    }
}
