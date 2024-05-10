package ru.danilakondr.netalbum.server.error;

public class InvalidRequestError extends IllegalArgumentException {
    public InvalidRequestError(String reason) {
        super(reason);
    }
}
