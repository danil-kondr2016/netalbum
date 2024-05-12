package ru.danilakondr.netalbum.client;

import org.apache.commons.codec.digest.DigestUtils;
import org.ini4j.Ini;
import org.ini4j.Wini;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Configuration implements Closeable {
    private static final String SESSIONS_SECTION = "sessions";
    private static final String SERVER_SECTION = "server";
    public static final String THUMBNAILS_SECTION = "thumbnails";
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

    private String getUrlHash(String url) {
        return DigestUtils.sha256Hex(url.getBytes(StandardCharsets.UTF_8));
    }

    public String getDefaultURL() {
        return ini.get(SERVER_SECTION, "address");
    }

    public int getThumbnailWidth() {
        String sWidth = ini.get(THUMBNAILS_SECTION, "width");
        if (sWidth == null)
            return 640;
        return Integer.parseInt(sWidth);
    }

    public int getThumbnailHeight() {
        String sHeight = ini.get(THUMBNAILS_SECTION, "height");
        if (sHeight == null)
            return 480;
        return Integer.parseInt(sHeight);
    }

    public void addSession(String sessionId, String url, String path) {
        String extId = getUrlHash(url) + "-" + sessionId;

        ini.add(SESSIONS_SECTION, extId+".url", url);
        ini.add(SESSIONS_SECTION, extId+".path", path);
    }

    public void removeSession(String sessionId, String url) {
        String extId = getUrlHash(url) + "-" + sessionId;

        ini.remove(SESSIONS_SECTION, extId+".url");
        ini.remove(SESSIONS_SECTION, extId+".path");
    }

    public String getSessionDirectoryPath(String sessionId, String url) {
        String extId = getUrlHash(url) + "-" + sessionId;
        return ini.get(SESSIONS_SECTION, extId+".path");
    }

    public String getSessionURL(String sessionId, String url) {
        String extId = getUrlHash(url) + "-" + sessionId;
        return ini.get(SESSIONS_SECTION, extId+".url");
    }

    public void save() throws IOException {
        ini.store(f);
    }

    @Override
    public void close() throws IOException {
        save();
    }
}
