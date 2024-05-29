/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ru.danilakondr.netalbum.client.gui;

import java.io.ByteArrayOutputStream;
import ru.danilakondr.netalbum.client.utils.FileSize;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingUtilities;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import ru.danilakondr.netalbum.api.message.Response;
import ru.danilakondr.netalbum.client.connect.Session;

/**
 *
 * @author danko
 */
public class ViewFolderForm extends javax.swing.JFrame {
    private final Session session;
    private String folderName;
    
    /**
     * Creates new form FolderViewerForm
     */
    public ViewFolderForm(Session session) {
        initComponents();
        this.session = session;
        initViewer();
    }
    
    private void initViewer() {
        
        session.addOnResponseListener(Response.Type.DIRECTORY_INFO, (s, r) -> {
            Response.DirectoryInfo info = (Response.DirectoryInfo)r;
            SwingUtilities.invokeLater(() -> {
                lblFolderName.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderViewer.properties.name"), info.getDirectoryName()));
                lblFolderSize.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderViewer.properties.size"), FileSize.getDisplayFileSize(info.getDirectorySize())));
                lblImageCount.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderViewer.properties.count"), info.getImageCount()));
            });
            folderName = info.getDirectoryName();
        });
        session.requestDirectoryInfo();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblFolderName = new javax.swing.JLabel();
        lblFolderSize = new javax.swing.JLabel();
        lblImageCount = new javax.swing.JLabel();
        btnLoadContents = new javax.swing.JButton();
        dummyPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderViewer.title"), new Object[] {})); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderViewer.properties"), new Object[] {}))); // NOI18N

        lblFolderName.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderViewer.properties.name"), new Object[] {})); // NOI18N

        lblFolderSize.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderViewer.properties.size"), new Object[] {})); // NOI18N

        lblImageCount.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderViewer.properties.count"), new Object[] {})); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFolderName, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                    .addComponent(lblFolderSize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblImageCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblFolderName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFolderSize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblImageCount)
                .addContainerGap())
        );

        btnLoadContents.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderViewer.loadContents"), new Object[] {})); // NOI18N
        btnLoadContents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadContentsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dummyPanelLayout = new javax.swing.GroupLayout(dummyPanel);
        dummyPanel.setLayout(dummyPanelLayout);
        dummyPanelLayout.setHorizontalGroup(
            dummyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        dummyPanelLayout.setVerticalGroup(
            dummyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dummyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(btnLoadContents)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLoadContents)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dummyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoadContentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadContentsActionPerformed
        session.getThumbnails().thenAccept((resp) -> {
            System.out.println(resp.statusCode());
            System.out.println(resp.headers().firstValueAsLong("Content-Length"));
            
            long length = resp.headers().firstValueAsLong("Content-Length").getAsLong();
            try {
                InputStream is = resp.body();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ProgressMonitor monitor = new ProgressMonitor(this, "Loading", "", 0, (int)length);
                monitor.setMillisToDecideToPopup(0);
                monitor.setMillisToPopup(0);
                
                int n_read = 0;
                while (true) {
                    if (monitor.isCanceled()) {
                        return;
                    }
                    
                    int inc = is.read();
                    
                    if (inc == -1)
                        break;
                    
                    bos.write(inc);
                    
                    n_read++;
                    monitor.setProgress(n_read);
                }
                
                Path thumbnails = saveThumbnails(bos.toByteArray());
                if (thumbnails == null)
                    return;

                SwingUtilities.invokeLater( () -> {
                    btnLoadContents.setEnabled(false);

                    panelContentsViewer = new FolderContentsViewer(session, folderName, thumbnails);
                    ((GroupLayout)getContentPane().getLayout()).replace(dummyPanel, panelContentsViewer);
                    pack();
                });
            }
            catch (IOException e) {

            }
        });
    }//GEN-LAST:event_btnLoadContentsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoadContents;
    private javax.swing.JPanel dummyPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblFolderName;
    private javax.swing.JLabel lblFolderSize;
    private javax.swing.JLabel lblImageCount;
    // End of variables declaration//GEN-END:variables

    private FolderContentsViewer panelContentsViewer;
    
    private Path saveThumbnails(byte[] thumbnailsZip) {
        try {
            Path tmpFilePath = Files.createTempFile(session.getSessionId(), ".zip");
            File tmpFile = tmpFilePath.toFile();
            
            try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
                fos.write(thumbnailsZip);
            }
            
            return tmpFilePath;
        } 
        catch (IOException ex) {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(this, "Не удалось сохранить уменьшенные картинки на компьютере", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
