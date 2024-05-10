package ru.danilakondr.netalbum.api.error;

public class InvalidRequestError extends IllegalArgumentException {
    public InvalidRequestError(String reason) {
        super(reason);
    }
}
