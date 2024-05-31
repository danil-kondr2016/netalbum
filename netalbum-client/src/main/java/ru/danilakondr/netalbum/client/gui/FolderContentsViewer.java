/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ru.danilakondr.netalbum.client.gui;

import ru.danilakondr.netalbum.client.utils.FileSize;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.nio.file.Path;

import java.nio.file.FileSystems;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import ru.danilakondr.netalbum.api.data.Change;

import ru.danilakondr.netalbum.api.data.FileInfo;
import ru.danilakondr.netalbum.api.message.Response;
import ru.danilakondr.netalbum.client.connect.Session;
import ru.danilakondr.netalbum.client.contents.FolderContentModel;
import ru.danilakondr.netalbum.client.contents.FolderContentNode;

/**
 *
 * @author danko
 */
public class FolderContentsViewer extends javax.swing.JPanel {
    private FolderContentModel contents;
    private ImagePanel imageViewer;
    private Session session;
    private String folderName;
    
    public FolderContentsViewer(Session session, String folderName, Path zipFile) {
        this.session = session;
        this.folderName = folderName;

        initContents(zipFile);
        initComponents();
        
        treeFolderContents.setTransferHandler(new FolderContentTreeTransferHandler());
        contents.addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                for (Object oNode: e.getChildren()) {
                    treeUpdated((FolderContentNode)oNode);
                }
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {
                for (Object oNode: e.getChildren()) {
                    var childNode = (FolderContentNode)oNode;
                    String newPath = String.join("/", Arrays.stream(childNode.getPath())
                        .filter(node -> !Objects.equals(node, childNode.getRoot()))
                        .map(node -> Objects.toString(node, ""))
                        .toArray(String[]::new));
                    contents.addInsert(childNode.getFileInfo(), newPath);
                }
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
                for (Object oNode: e.getChildren()) {
                    var childNode = (FolderContentNode)oNode;
                    contents.addRemove(childNode.getFileInfo());
                }
            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                for (Object oNode: e.getChildren()) {
                    treeUpdated((FolderContentNode)oNode);
                }
            }
            
            private void treeUpdated(FolderContentNode lastNode) {
                FileInfo info = lastNode.getFileInfo();
                String oldPath = info.getFileName();
                
                FolderContentNode rootNode = (FolderContentNode)lastNode.getRoot();
                String newPath = String.join("/", Arrays.stream(lastNode.getPath())
                        .filter(node -> !Objects.equals(node, rootNode))
                        .map(node -> Objects.toString(node, ""))
                        .toArray(String[]::new));
                
                contents.addUpdate(info, newPath);
            }
        });
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
        dummyPanel = new javax.swing.JPanel();
        lblOriginalSize = new javax.swing.JLabel();
        lblOriginalFileSize = new javax.swing.JLabel();
        lblFileName = new javax.swing.JLabel();
        btnSynchronize = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeFolderContents = new javax.swing.JTree();
        btnCreateDirectory = new javax.swing.JButton();

        dummyPanel.setPreferredSize(new java.awt.Dimension(400, 300));

        javax.swing.GroupLayout dummyPanelLayout = new javax.swing.GroupLayout(dummyPanel);
        dummyPanel.setLayout(dummyPanelLayout);
        dummyPanelLayout.setHorizontalGroup(
            dummyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        dummyPanelLayout.setVerticalGroup(
            dummyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        lblOriginalSize.setText(" ");

        lblOriginalFileSize.setText(" ");

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings"); // NOI18N
        btnSynchronize.setText(bundle.getString("folderViewer.synchronize")); // NOI18N
        btnSynchronize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSynchronizeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFileName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblOriginalSize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblOriginalFileSize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dummyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 264, Short.MAX_VALUE)
                        .addComponent(btnSynchronize)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dummyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblOriginalSize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblOriginalFileSize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSynchronize)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        treeFolderContents.setModel(contents);
        treeFolderContents.setAutoscrolls(true);
        treeFolderContents.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        treeFolderContents.setDragEnabled(true);
        treeFolderContents.setDropMode(javax.swing.DropMode.ON_OR_INSERT);
        treeFolderContents.setEditable(true);
        treeFolderContents.setName(""); // NOI18N
        treeFolderContents.setVerifyInputWhenFocusTarget(false);
        treeFolderContents.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeFolderContentsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(treeFolderContents);

        btnCreateDirectory.setText(bundle.getString("folderViewer.createDirectory")); // NOI18N
        btnCreateDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateDirectoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnCreateDirectory)
                .addGap(0, 116, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(btnCreateDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void treeFolderContentsValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeFolderContentsValueChanged
        if (imageViewer == null) {
            imageViewer = new ImagePanel();
            imageViewer.setPreferredSize(new Dimension(0, 300));

            ((GroupLayout)jPanel1.getLayout()).replace(dummyPanel, imageViewer);
        }
        
        FolderContentNode node = (FolderContentNode)evt.getPath().getLastPathComponent();
        if (node.isImage()) {
            FileInfo.Image info = (FileInfo.Image)node.getFileInfo();
            
            String fileName = Objects.toString(node.getUserObject(), "");
            lblOriginalSize.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderContents.imageSize"), info.getWidth(), info.getHeight()));
            lblOriginalFileSize.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderContents.fileSize"), FileSize.getDisplayFileSize(info.getFileSize())));
            lblFileName.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings").getString("folderContents.fileName"), fileName));
        
            try {
                String[] pathSegments = info.getFileName().split("/");
                BufferedImage img = contents.getThumbnail(pathSegments);
                imageViewer.loadImage(img);
            }
            catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        else {
            lblOriginalSize.setText("");
            lblOriginalFileSize.setText("");
            lblFileName.setText("");
            imageViewer.loadImage(null);
        }
    }//GEN-LAST:event_treeFolderContentsValueChanged

    private void btnSynchronizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSynchronizeActionPerformed
        List<Change> changes = contents.getChanges();
        
        var form = SwingUtilities.getWindowAncestor(this);
        session.addOnResponseListener(Response.Type.SYNCHRONIZING, s -> form.dispose(), true);
        session.addOnResponseListener(Response.Type.SUCCESS, s -> form.dispose(), true);
        session.synchronize(changes);
    }//GEN-LAST:event_btnSynchronizeActionPerformed

    private void btnCreateDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateDirectoryActionPerformed
        FolderContentNode node = FolderContentNode.createDirectory("New folder");
        FolderContentNode root = (FolderContentNode)contents.getRoot();
        
        FileInfo info = new FileInfo();
        info.setFileType(FileInfo.Type.DIRECTORY);
        info.setFileName("New folder");
        
        node.setFileInfo(info);
        
        contents.insertNodeInto(node, root, 0);
    }//GEN-LAST:event_btnCreateDirectoryActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreateDirectory;
    private javax.swing.JButton btnSynchronize;
    private javax.swing.JPanel dummyPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblOriginalFileSize;
    private javax.swing.JLabel lblOriginalSize;
    private javax.swing.JTree treeFolderContents;
    // End of variables declaration//GEN-END:variables

    private void initContents(Path zipFile) {
        try {
            FileSystem fs = FileSystems.newFileSystem(zipFile);
            this.contents = new FolderContentModel(folderName);
            this.contents.load(fs);
        }
        catch (IOException e) {
            this.contents = new FolderContentModel("Failure");
        }
    }
}

class FolderContentTreeTransferHandler extends TransferHandler {
    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    FolderContentNode[] nodesToRemove;

    public FolderContentTreeTransferHandler() {
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType +
                    ";class=\"" +
                    FolderContentNode[].class.getName() +
                    "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } catch(ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
        }
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if(!support.isDrop()) {
            return false;
        }
        support.setShowDropLocation(true);
        if(!support.isDataFlavorSupported(nodesFlavor)) {
            return false;
        }
        // Do not allow a drop on the drag source selections.
        JTree.DropLocation dl =
                (JTree.DropLocation)support.getDropLocation();
        JTree tree = (JTree)support.getComponent();
        int dropRow = tree.getRowForPath(dl.getPath());
        int[] selRows = tree.getSelectionRows();
        for(int i = 0; i < selRows.length; i++) {
            if(selRows[i] == dropRow) {
                return false;
            }
            FolderContentNode treeNode =
                    (FolderContentNode)tree.getPathForRow(selRows[i]).getLastPathComponent();
            for (TreeNode offspring: Collections.list(treeNode.depthFirstEnumeration())) {
                if (tree.getRowForPath(new TreePath(((FolderContentNode)offspring).getPath())) == dropRow) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths == null) {
            return null;
        }
        // Make up a node array of copies for transfer and
        // another for/of the nodes that will be removed in
        // exportDone after a successful drop.
        List<FolderContentNode> copies = new ArrayList<>();
        List<FolderContentNode> toRemove = new ArrayList<>();
        FolderContentNode firstNode =
                (FolderContentNode) paths[0].getLastPathComponent();
        HashSet<TreeNode> doneItems = new LinkedHashSet<>(paths.length);
        FolderContentNode copy = copy(firstNode, doneItems, tree);
        copies.add(copy);
        toRemove.add(firstNode);
        for (int i = 1; i < paths.length; i++) {
            FolderContentNode next =
                    (FolderContentNode) paths[i].getLastPathComponent();
            if (doneItems.contains(next)) {
                continue;
            }
            // Do not allow higher level nodes to be added to list.
            if (next.getLevel() < firstNode.getLevel()) {
                break;
            } else if (next.getLevel() > firstNode.getLevel()) {  // child node
                copy.add(copy(next, doneItems, tree));
                // node already contains child
            } else {                                        // sibling
                copies.add(copy(next, doneItems, tree));
                toRemove.add(next);
            }
            doneItems.add(next);
        }
        FolderContentNode[] nodes =
                copies.toArray(FolderContentNode[]::new);
        nodesToRemove =
                toRemove.toArray(FolderContentNode[]::new);
        return new NodesTransferable(nodes);
    }

    private FolderContentNode copy(FolderContentNode node, HashSet<TreeNode> doneItems, JTree tree) {
        FolderContentNode copy = (FolderContentNode)node.clone();
        doneItems.add(node);
        for (int i=0; i<node.getChildCount(); i++) {
            copy.add(copy((FolderContentNode)((TreeNode)node).getChildAt(i), doneItems, tree));
        }
        int row = tree.getRowForPath(new TreePath(copy.getPath()));
        tree.expandRow(row);
        return copy;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if((action & MOVE) == MOVE) {
            JTree tree = (JTree)source;
            FolderContentModel model = (FolderContentModel)tree.getModel();
            // Remove nodes saved in nodesToRemove in createTransferable.
            for (FolderContentNode nodeToRemove : nodesToRemove) {
                model.removeNodeFromParent(nodeToRemove);
            }
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if(!canImport(support)) {
            return false;
        }
        // Extract transfer data.
        FolderContentNode[] nodes = null;
        try {
            Transferable t = support.getTransferable();
            nodes = (FolderContentNode[])t.getTransferData(nodesFlavor);
        } catch(UnsupportedFlavorException ufe) {
            System.out.println("UnsupportedFlavor: " + ufe.getMessage());
        } catch(java.io.IOException ioe) {
            System.out.println("I/O error: " + ioe.getMessage());
        }
        // Get drop location info.
        JTree.DropLocation dl =
                (JTree.DropLocation)support.getDropLocation();
        int childIndex = dl.getChildIndex();
        TreePath dest = dl.getPath();
        FolderContentNode parent =
                (FolderContentNode)dest.getLastPathComponent();
        JTree tree = (JTree)support.getComponent();
        FolderContentModel model = (FolderContentModel)tree.getModel();
        // Configure for drop mode.
        int index = childIndex;    // DropMode.INSERT
        if (childIndex == -1) {    // DropMode.ON
            index = parent.getChildCount();
        }
        // Add data to model.
        for (FolderContentNode node : nodes) {
            model.insertNodeInto(node, parent, index++);
        }
        return true;
    }

    public String toString() {
        return getClass().getName();
    }

    public class NodesTransferable implements Transferable {
        FolderContentNode[] nodes;

        public NodesTransferable(FolderContentNode[] nodes) {
            this.nodes = nodes;
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if(!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);
            return nodes;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }
}
