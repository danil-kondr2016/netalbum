package ru.danilakondr.netalbum.server;

import java.security.SecureRandom;

public class SessionIdProvider {
    public static String generateSessionId() {
        SecureRandom random = new SecureRandom();
        
        byte[] rawId = new byte[20];
        random.nextBytes(rawId);
        
        StringBuilder sb = new StringBuilder();
        for (byte x : rawId)
            sb.append(String.format("%02x", x));
        
        return sb.toString();
    }
}
