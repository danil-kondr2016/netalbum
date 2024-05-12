package ru.danilakondr.netalbum.client;

import org.ini4j.InvalidFileFormatException;
import ru.danilakondr.netalbum.client.gui.StartDialog;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class NetAlbumClientApp {
    private static final String NETALBUM_CONF_FALLBACK =
            System.getProperty("user.home")
                    + File.separator
                    + "/netalbum.ini";

    private static File getNetalbumIni() {
        // 1. Проверка системного свойства "netalbum.ini"
        String ini = System.getProperty("netalbum.ini", NETALBUM_CONF_FALLBACK);
        return new File(ini);
    }

    public static void main(String[] args) throws IOException {
        setSystemLookAndFeel();

        Configuration cfg = null;
        File f = getNetalbumIni();
        try {
            cfg = new Configuration(f);

            String defaultURL = cfg.getDefaultURL();
            System.out.println("Default URL: " + defaultURL);

            StartDialog dialog = new StartDialog(defaultURL);
            dialog.pack();
            dialog.setVisible(true);
        }
        catch (FileNotFoundException e) {
            guiDie("Ошибка: "+e.getLocalizedMessage());
        }
        catch (InvalidFileFormatException e) {
            guiDie("Неправильный формат конфигурационного файла");
        }
        catch (IOException e) {
            guiDie(e.getLocalizedMessage());
        }
        finally {
            if (cfg != null) {
                try {
                    cfg.close();
                }
                catch (IOException e) {
                    guiDie(e.getLocalizedMessage());
                }
            }
            System.exit(0);
        }
    }

    private static void setSystemLookAndFeel() {
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
    }

    private static void guiDie(String message) {
        JOptionPane.showMessageDialog(null, message, "Ошибка", ERROR_MESSAGE);
        System.exit(-1);
    }
}
