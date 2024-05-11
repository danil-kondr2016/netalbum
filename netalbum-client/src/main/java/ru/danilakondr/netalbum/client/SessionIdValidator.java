package ru.danilakondr.netalbum.client;

import java.util.regex.Pattern;

public class SessionIdValidator {
    private static final Pattern VALID_SESSION_ID_PATTERN = Pattern.compile("[0-9A-Fa-f]{40}");

    public static boolean isSessionIdValid(String sessionId) {
        return (VALID_SESSION_ID_PATTERN.matcher(sessionId).matches());
    }
}
