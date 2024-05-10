package ru.danilakondr.netalbum.api.error;

public class InvalidMethodError extends IllegalArgumentException {
    public InvalidMethodError(String method) {
        super(method);
    }
}
