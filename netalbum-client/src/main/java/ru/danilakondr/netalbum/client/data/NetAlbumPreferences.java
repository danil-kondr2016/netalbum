package ru.danilakondr.netalbum.client.data;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import ru.danilakondr.netalbum.client.data.SessionInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author danko
 */
public class NetAlbumPreferences {
    private final Preferences prefs;
    
    private final Preferences thumbnails;
    private final Preferences initiatedSessions;
    private final Preferences server;
    
    public NetAlbumPreferences() { 
        prefs = Preferences.userNodeForPackage(NetAlbumPreferences.class);
        thumbnails = prefs.node("thumbnails");
        initiatedSessions = prefs.node("initiated_sessions");
        server = prefs.node("server");
    }
    
    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        prefs.addPreferenceChangeListener(pcl);
    }
    
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        prefs.removePreferenceChangeListener(pcl);
    }
    
    private void sync(Preferences p) {
         try {
            p.sync();
        } catch (BackingStoreException ex) {
            Logger.getLogger(NetAlbumPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getThumbnailWidth() {
        return thumbnails.getInt("width", 640);
    }
    
    public int getThumbnailHeight() {
        return thumbnails.getInt("height", 480);
    }
    
    public String getServerAddress() {
        return server.get("address", "");
    }
    
    public void setThumbnailWidth(int width) {
        thumbnails.putInt("width", width);
        sync(thumbnails);
    }
    
    public void setThumbnailHeight(int height) {
        thumbnails.putInt("height", height);
        sync(thumbnails);
    }
    
    public void setServerAddress(String address) {
        server.put("address", address);
        sync(server);
    }

    public void addInitiatedSession(String url, String id, String path) {
        String urlHash = DigestUtils.md5Hex(url);
        initiatedSessions.node("servers").put(urlHash, url);
        initiatedSessions.node(urlHash).put(id, path);
        
        int n = initiatedSessions.node(urlHash).getInt("count", 0);
        n++;
        initiatedSessions.node(urlHash).putInt("count", n);
        
        n = initiatedSessions.getInt("count", 0);
        initiatedSessions.putInt("count", n + 1);
        
        sync(initiatedSessions);
    }
    
    public void removeInitiatedSession(String url, String id) {
        String urlHash = DigestUtils.md5Hex(url);
        initiatedSessions.node(urlHash).remove(id);
        
        int n = initiatedSessions.node(urlHash).getInt("count", 0);
        n--;
        initiatedSessions.node(urlHash).putInt("count", n > 0 ? n : 0);
        if (n == 0) {
            try {
                initiatedSessions.node(urlHash).removeNode();
            } catch (BackingStoreException ex) {
                Logger.getLogger(NetAlbumPreferences.class.getName()).log(Level.SEVERE, null, ex);
            }
            initiatedSessions.node("servers").remove(urlHash);
        }
        
        n = initiatedSessions.getInt("count", 0);
        n--;
        initiatedSessions.putInt("count", n > 0 ? n : 0);
        
        sync(initiatedSessions);
    }
    
    public List<SessionInfo> getInitiatedSessions() {
        ArrayList<SessionInfo> lstSessions = new ArrayList();

        try {
            String[] servers = initiatedSessions.node("servers").keys();
            for (String server: servers) {
                String[] sessions = initiatedSessions.node(server).keys();
                for (String session: sessions) {
                    if ("count".equals(session))
                        continue;
                    
                    SessionInfo info = new SessionInfo();
                    info.setUrl(initiatedSessions.node("servers").get(server, ""));
                    info.setSessionId(session);
                    info.setPath(initiatedSessions.node(server).get(session, ""));
                    lstSessions.add(info);
                }
            }
            
        } catch (BackingStoreException ex) {
            Logger.getLogger(NetAlbumPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return lstSessions;
    }
    
    public boolean hasInitiatedSessions() {
        return initiatedSessions.getInt("count", 0) > 0;
    }

    public boolean isNotConfigured() {
        int width = thumbnails.getInt("width", -1);
        int height = thumbnails.getInt("height", -1);
        String address = server.get("server", null);
        
        if (address == null || width == -1 || height == -1)
            return true;
        
        return false;
    }
}
