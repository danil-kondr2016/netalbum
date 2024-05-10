package ru.danilakondr.netalbum.server.error;

public class InvalidMethodError extends IllegalArgumentException {
    public InvalidMethodError(String method) {
        super(method);
    }
}
