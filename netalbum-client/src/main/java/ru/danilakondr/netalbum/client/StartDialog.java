package ru.danilakondr.netalbum.client;

import javax.swing.*;
import java.awt.event.*;

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

    public StartDialog() {
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

    private void onOK() {
        boolean initiateSessionState = rbInitiateSession.isSelected();
        boolean connectToSessionState = rbConnectToSession.isSelected();

        if (initiateSessionState == connectToSessionState) {
            JOptionPane.showMessageDialog(null,
                    "Неправильный выбор типа сессии",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        else if (initiateSessionState) {
            JOptionPane.showMessageDialog(null,
                    "Вы выбрали инициирование сессии.");
        }
        else if (connectToSessionState) {
            JOptionPane.showMessageDialog(null,
                    "Вы выбрали подключение к существующей сессии."
            );
        }

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
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

        StartDialog dialog = new StartDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
