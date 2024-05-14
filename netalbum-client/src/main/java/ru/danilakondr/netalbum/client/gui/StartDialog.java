package ru.danilakondr.netalbum.client.gui;

import ru.danilakondr.netalbum.client.LocalizedMessages;
import ru.danilakondr.netalbum.client.NetAlbumClientApp;
import ru.danilakondr.netalbum.client.SessionIdValidator;

import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class StartDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton rbInitiateSession;
    private JTextField tfFolderPath;
    private JButton btnSelectFolder;
    private JRadioButton rbConnectToSession;
    private JTextField tfSessionKey;
    private JTextField tfServerAddress;
    private final JFileChooser dirChooser;

    private NetAlbumClientApp.SessionType sessionType;
    private String sessionId;
    private String urlString;
    private String directoryPath;

    public StartDialog() {
        dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        btnSelectFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = dirChooser.showOpenDialog(StartDialog.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File f = dirChooser.getSelectedFile();
                    StartDialog.this.tfFolderPath.setText(f.getAbsolutePath());
                }
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public StartDialog(String defaultURL) {
        this();
        if (defaultURL != null)
            tfServerAddress.setText(defaultURL);
    }

    private void onOK() {
        boolean initiateSessionState = rbInitiateSession.isSelected();
        boolean connectToSessionState = rbConnectToSession.isSelected();

        if (initiateSessionState == connectToSessionState) {
            JOptionPane.showMessageDialog(null,
                    LocalizedMessages.invalidSessionTypeSelection(),
                    LocalizedMessages.error(), JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        else if (initiateSessionState) {
            JOptionPane.showMessageDialog(null,
                    "Вы выбрали инициирование сессии.");
            this.sessionType = NetAlbumClientApp.SessionType.INIT_SESSION;
            this.urlString = this.tfServerAddress.getText();
            this.directoryPath = this.tfFolderPath.getText();
        }
        else if (connectToSessionState) {
            JOptionPane.showMessageDialog(null,
                    "Вы выбрали подключение к существующей сессии."
            );
            String sessionId = tfSessionKey.getText();
            if (!SessionIdValidator.isSessionIdValid(sessionId)) {
                JOptionPane.showMessageDialog(null,
                        LocalizedMessages.invalidSessionKey(sessionId),
                        LocalizedMessages.error(), JOptionPane.ERROR_MESSAGE);
            }

            this.sessionType = NetAlbumClientApp.SessionType.CONNECT_TO_SESSION;
            this.sessionId = sessionId;
        }

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public String getSessionId() {
        return sessionId;
    }

    public NetAlbumClientApp.SessionType getSessionType() {
        return sessionType;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public String getServerAddress() {
        return urlString;
    }
}
