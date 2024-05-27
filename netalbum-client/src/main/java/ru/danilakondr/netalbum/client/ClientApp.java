package ru.danilakondr.netalbum.client;

import ru.danilakondr.netalbum.client.data.NetAlbumPreferences;
import javax.swing.*;

import java.io.IOException;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import ru.danilakondr.netalbum.client.gui.SessionControlForm;

public class ClientApp {
    private final NetAlbumPreferences cfg;

    public ClientApp(NetAlbumPreferences cfg) {
        this.cfg = cfg;
    }

    public void run() {     
        sessionControl();
    }

    private static void guiDie(String message) {
        JOptionPane.showMessageDialog(null, message, "Ошибка", ERROR_MESSAGE);
        System.exit(-1);
    }


    public static void main(String[] args) throws IOException {
        NetAlbumPreferences cfg = new NetAlbumPreferences();
        ClientApp app = new ClientApp(cfg);
        app.run();
    }

    private void sessionControl() {
        SwingUtilities.invokeLater(() -> {
            SessionControlForm form = new SessionControlForm();
            form.setConfiguration(cfg);
            form.setVisible(true);
            
            if (cfg.hasInitiatedSessions()) {
                form.restoreSessions();
            }
        });
    }
}
