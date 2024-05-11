package ru.danilakondr.netalbum.client;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class NetAlbumClientApp {
    private static final String NETALBUM_CONF_FALLBACK =
            System.getProperty("user.home")
                    + File.separator
                    + "/netalbum.ini";

    public static void main(String[] args) throws IOException {
        Configuration cfg = new Configuration(new File(NETALBUM_CONF_FALLBACK));
        String defaultURL = cfg.getDefaultURL();
        System.out.println("Default URL: " + defaultURL);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        StartDialog dialog = new StartDialog(defaultURL);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
