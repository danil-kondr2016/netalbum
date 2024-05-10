package ru.danilakondr.netalbum.api.error;

public class NonExistentSession extends RuntimeException {
    public NonExistentSession(String sessionId) {
        super(sessionId);
    }
}
