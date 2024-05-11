package ru.danilakondr.netalbum.client;

import org.ini4j.Ini;
import org.ini4j.Wini;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class Configuration implements Closeable {
    private static final String SESSIONS_SECTION = "sessions";
    private File f;
    private Ini ini;

    public Configuration(File f) throws IOException {
        this.f = f;
        initIniFile();
    }

    private void initIniFile() throws IOException {
        this.ini = new Wini(f);
        this.ini.getConfig().setPathSeparator(File.pathSeparatorChar);
    }

    public void addSession(String sessionId, String path) {
        ini.add(SESSIONS_SECTION, sessionId, path);
    }

    public void removeSession(String sessionId) {
        ini.remove(SESSIONS_SECTION, sessionId);
    }

    public String getSessionDirectoryPath(String sessionId) {
        return ini.get(SESSIONS_SECTION, sessionId);
    }

    public void save() throws IOException {
        ini.store(f);
    }

    @Override
    public void close() throws IOException {
        save();
    }
}
