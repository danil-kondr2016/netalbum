package ru.danilakondr.netalbum.server.error;

public class NonExistentSession extends RuntimeException {
    public NonExistentSession(String sessionId) {
        super(sessionId);
    }
}
