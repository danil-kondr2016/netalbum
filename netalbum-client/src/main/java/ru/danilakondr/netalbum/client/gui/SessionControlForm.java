/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ru.danilakondr.netalbum.client.gui;

import java.io.File;
import java.net.URI;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import ru.danilakondr.netalbum.client.connect.SessionTable;
import ru.danilakondr.netalbum.client.connect.Session;
import ru.danilakondr.netalbum.api.message.Response;
import ru.danilakondr.netalbum.client.NetAlbumPreferences;
import ru.danilakondr.netalbum.client.SessionInfo;

/**
 *
 * @author danko
 */
public class SessionControlForm extends javax.swing.JFrame {
    private final SessionTable sessionTable = new SessionTable();
    private NetAlbumPreferences cfg;
    
    /**
     * Creates new form SessionControlForm
     */
    public SessionControlForm() {
        initComponents();
    }
    
    public void setConfiguration(NetAlbumPreferences cfg) {
        this.cfg = cfg;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contextMenu = new javax.swing.JPopupMenu();
        miViewSession = new javax.swing.JMenuItem();
        miCloseSessions = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSessionList = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        miInitSession = new javax.swing.JMenuItem();
        miOpenSession = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator1 = new javax.swing.JPopupMenu.Separator();
        miPreferences = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator2 = new javax.swing.JPopupMenu.Separator();
        miCloseAllSessions = new javax.swing.JMenuItem();
        miExit = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        miAbout = new javax.swing.JMenuItem();

        miViewSession.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("contextMenu.ViewSessionProperties"), new Object[] {})); // NOI18N
        miViewSession.setToolTipText("");
        miViewSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miViewSessionActionPerformed(evt);
            }
        });
        contextMenu.add(miViewSession);

        miCloseSessions.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("contextMenu.CloseSelectedSessions"), new Object[] {})); // NOI18N
        miCloseSessions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miCloseSessionsActionPerformed(evt);
            }
        });
        contextMenu.add(miCloseSessions);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblSessionList.setModel(sessionTable);
        tblSessionList.setComponentPopupMenu(contextMenu);
        jScrollPane1.setViewportView(tblSessionList);

        fileMenu.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("menu.File"), new Object[] {})); // NOI18N

        miInitSession.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("menu.File.NewFolder"), new Object[] {})); // NOI18N
        miInitSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miInitSessionActionPerformed(evt);
            }
        });
        fileMenu.add(miInitSession);

        miOpenSession.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("menu.File.OpenFolder"), new Object[] {})); // NOI18N
        fileMenu.add(miOpenSession);
        fileMenu.add(jSeparator1);

        miPreferences.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("menu.File.Configuration"), new Object[] {})); // NOI18N
        miPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miPreferencesActionPerformed(evt);
            }
        });
        fileMenu.add(miPreferences);
        fileMenu.add(jSeparator2);

        miCloseAllSessions.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("menu.File.CloseAllSessions"), new Object[] {})); // NOI18N
        miCloseAllSessions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miCloseAllSessionsActionPerformed(evt);
            }
        });
        fileMenu.add(miCloseAllSessions);

        miExit.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("menu.File.Quit"), new Object[] {})); // NOI18N
        miExit.setToolTipText("");
        miExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExitActionPerformed(evt);
            }
        });
        fileMenu.add(miExit);

        jMenuBar1.add(fileMenu);

        helpMenu.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("menu.Help"), new Object[] {})); // NOI18N

        miAbout.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("menu.Help.About"), new Object[] {})); // NOI18N
        helpMenu.add(miAbout);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private File selectFolder() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int result = dirChooser.showOpenDialog(this);
        File directory;
        if (result == JFileChooser.APPROVE_OPTION) {
            directory = dirChooser.getSelectedFile();
            return directory;
        } else {
            return null;
        }
    }
    
    private void initSession(File directory) {
        String serverAddress = cfg.getServerAddress();
        URI serverUri = URI.create(serverAddress);
        
        Session session = new Session();
        session.addOnConnectedListener(s -> sessionTable.addSession(s));
        session.addOnConnectedListener(s -> cfg.addInitiatedSession(
                serverAddress, 
                s.getSessionId(), 
                directory.getAbsolutePath()));
        session.addOnCloseListener(s -> sessionTable.removeSession(s));
        session.addOnResponseListener(Response.Type.SESSION_CLOSED, s -> cfg.removeInitiatedSession(s.getUrl(), s.getSessionId()), false);
        session.addOnResponseListener(Response.Type.SESSION_CREATED, (s) -> {
            s.loadImages(directory);
        }, true);
        session.init(serverUri, directory.getName());
    }
    
    private void miPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miPreferencesActionPerformed
        ConfigDialog dlg = new ConfigDialog(this, true, cfg);
        dlg.setVisible(true);
    }//GEN-LAST:event_miPreferencesActionPerformed

    private void miInitSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miInitSessionActionPerformed
        File directory = selectFolder();
        if (directory == null)
            return;
        
        initSession(directory);
    }//GEN-LAST:event_miInitSessionActionPerformed

    private void miCloseAllSessionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miCloseAllSessionsActionPerformed
        int x = JOptionPane.showConfirmDialog(this, 
                "Вы действительно хотите закрыть все сессии?", 
                "Внимание",
                YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (x == JOptionPane.NO_OPTION)
            return;
        
        for (int i = 0; i < sessionTable.getRowCount(); i++) {
            Session s = sessionTable.getSessionAt(i);
            s.close();
        }
    }//GEN-LAST:event_miCloseAllSessionsActionPerformed

    private void miCloseSessionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miCloseSessionsActionPerformed
        if (tblSessionList.getSelectedRowCount() < 1)
            return;

        int[] rows = tblSessionList.getSelectedRows();
        for (int row: rows) {
            Session s = sessionTable.getSessionAt(row);
            s.close();
        }
    }//GEN-LAST:event_miCloseSessionsActionPerformed

    private void miExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miExitActionPerformed
        dispose();
    }//GEN-LAST:event_miExitActionPerformed

    private void miViewSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miViewSessionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_miViewSessionActionPerformed
    
    public void restoreSessions() {
        List<SessionInfo> sessions = cfg.getInitiatedSessions();
        for (SessionInfo sessionInfo : sessions) {
            Session session = new Session();
            session.addOnResponseListener(Response.Type.SESSION_RESTORED, s -> sessionTable.addSession(s));
            session.addOnCloseListener(s -> sessionTable.removeSession(s));
            session.addOnResponseListener(Response.Type.SESSION_CLOSED, s -> cfg.removeInitiatedSession(s.getUrl(), s.getSessionId()), false);
            session.setPath(sessionInfo.getPath());
            session.restore(URI.create(sessionInfo.getUrl()), sessionInfo.getSessionId());
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu contextMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem miAbout;
    private javax.swing.JMenuItem miCloseAllSessions;
    private javax.swing.JMenuItem miCloseSessions;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miInitSession;
    private javax.swing.JMenuItem miOpenSession;
    private javax.swing.JMenuItem miPreferences;
    private javax.swing.JMenuItem miViewSession;
    private javax.swing.JTable tblSessionList;
    // End of variables declaration//GEN-END:variables
}
