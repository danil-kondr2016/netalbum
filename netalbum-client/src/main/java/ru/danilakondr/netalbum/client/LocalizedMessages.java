package ru.danilakondr.netalbum.client;

import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class LocalizedMessages {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    public static String notADirectoryError(File f) {
        return MessageFormat.format(bundle.getString("not_a_directory"), f);
    }

    public static String invalidSessionKey(String sessionId) {
        return MessageFormat.format(bundle.getString("invalid_session_key"), sessionId);
    }

    public static String getMessage(String id, Object... args) {
        return MessageFormat.format(bundle.getString(id), args);
    }
}
