package ru.danilakondr.netalbum.client;

import ru.danilakondr.netalbum.client.gui.StartDialog;

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
        StartDialog startDlg = new StartDialog(null, true);
        startDlg.setVisible(true);
        
        switch (startDlg.getSessionType()) {
            case INIT_SESSION:
                sessionControl();
                break;
            case CONNECT_TO_SESSION:
                break;
            default:
                System.exit(0);
        }
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
        });
    }

    public enum SessionType {
        INIT_SESSION,
        CONNECT_TO_SESSION
    }
}
