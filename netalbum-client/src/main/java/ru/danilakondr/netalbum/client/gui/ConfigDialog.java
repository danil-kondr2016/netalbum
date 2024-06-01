/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package ru.danilakondr.netalbum.client.gui;

import javax.swing.JOptionPane;
import ru.danilakondr.netalbum.client.utils.FileSize;
import ru.danilakondr.netalbum.client.data.NetAlbumPreferences;
import ru.danilakondr.netalbum.client.utils.ServerAddressValidator;

/**
 *
 * @author danko
 */
public class ConfigDialog extends javax.swing.JDialog {
    private final NetAlbumPreferences prefs;

    /**
     * Creates new form ConfigDialog
     */
    public ConfigDialog(java.awt.Frame parent, boolean modal, NetAlbumPreferences prefs) {
        super(parent, modal);
        this.prefs = prefs;
        initComponents();
        setValues();
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
        jLabel1 = new javax.swing.JLabel();
        tfServerAddress = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        spThumbnailWidth = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        spThumbnailHeight = new javax.swing.JSpinner();
        lblApproxSize = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnApply = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Параметры");
        setModal(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("config.serverProperties"), new Object[] {}))); // NOI18N

        jLabel1.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("config.serverAddress"), new Object[] {})); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfServerAddress)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfServerAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("config.thumbnailProperties"), new Object[] {}))); // NOI18N

        jLabel2.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("config.width"), new Object[] {})); // NOI18N

        spThumbnailWidth.setModel(new javax.swing.SpinnerNumberModel(640, null, null, 1));
        spThumbnailWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spThumbnailWidthStateChanged(evt);
            }
        });

        jLabel3.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("config.height"), new Object[] {})); // NOI18N

        spThumbnailHeight.setModel(new javax.swing.SpinnerNumberModel(480, null, null, 1));
        spThumbnailHeight.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spThumbnailHeightStateChanged(evt);
            }
        });

        lblApproxSize.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("config.approximateThumbnailSize"), new Object[] {FileSize.getDisplayFileSize(getApproximateImageSize())})); // NOI18N
        lblApproxSize.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblApproxSize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spThumbnailWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spThumbnailHeight, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(spThumbnailWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(spThumbnailHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblApproxSize)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnApply.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("button.Apply"), new Object[] {})); // NOI18N
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        btnCancel.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("button.Cancel"), new Object[] {})); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOK.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("button.OK"), new Object[] {})); // NOI18N
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnOK)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnApply)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApply)
                    .addComponent(btnCancel)
                    .addComponent(btnOK))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        if (!ServerAddressValidator.isValid(tfServerAddress.getText())) {
            JOptionPane.showMessageDialog(this, 
                    "Неправильный адрес сервера",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            
            return;
        }
        
        apply();
    }//GEN-LAST:event_btnApplyActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
         if (!ServerAddressValidator.isValid(tfServerAddress.getText())) {
            JOptionPane.showMessageDialog(this, 
                    "Неправильный адрес сервера",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            
            return;
        }
        
        apply();
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private long getApproximateImageSize() {
        int thumbnailWidth = (Integer)spThumbnailWidth.getValue();
        int thumbnailHeight = (Integer)spThumbnailHeight.getValue();
        
        long area = (long)thumbnailWidth * thumbnailHeight;
        long size = (area + 4) / 5;
        
        return size;
    }
    
    private void spThumbnailWidthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spThumbnailWidthStateChanged
        String dispSize = FileSize.getDisplayFileSize(getApproximateImageSize());
        lblApproxSize.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("config.approximateThumbnailSize"), new Object[] {dispSize}));
    }//GEN-LAST:event_spThumbnailWidthStateChanged

    private void spThumbnailHeightStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spThumbnailHeightStateChanged
        String dispSize = FileSize.getDisplayFileSize(getApproximateImageSize());
        lblApproxSize.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("config.approximateThumbnailSize"), new Object[] {dispSize}));
    }//GEN-LAST:event_spThumbnailHeightStateChanged
    
    private void apply() {
        int thumbnailWidth = (Integer)spThumbnailWidth.getValue();
        int thumbnailHeight = (Integer)spThumbnailHeight.getValue();
        String serverAddress = tfServerAddress.getText();
        
        prefs.setServerAddress(serverAddress);
        prefs.setThumbnailWidth(thumbnailWidth);
        prefs.setThumbnailHeight(thumbnailHeight);
    }
    
    private void setValues() {
        int thumbnailWidth = prefs.getThumbnailWidth();
        int thumbnailHeight = prefs.getThumbnailHeight();
        String serverAddress = prefs.getServerAddress();
        
        spThumbnailWidth.setValue(thumbnailWidth);
        spThumbnailHeight.setValue(thumbnailHeight);
        tfServerAddress.setText(serverAddress);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblApproxSize;
    private javax.swing.JSpinner spThumbnailHeight;
    private javax.swing.JSpinner spThumbnailWidth;
    private javax.swing.JTextField tfServerAddress;
    // End of variables declaration//GEN-END:variables
}
