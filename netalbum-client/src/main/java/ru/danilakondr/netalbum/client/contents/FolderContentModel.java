/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.contents;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.tree.DefaultTreeModel;
import ru.danilakondr.netalbum.api.data.Change;
import static ru.danilakondr.netalbum.api.data.Change.Type.ADD_FOLDER;
import static ru.danilakondr.netalbum.api.data.Change.Type.RENAME_DIR;
import static ru.danilakondr.netalbum.api.data.Change.Type.RENAME_FILE;
import ru.danilakondr.netalbum.api.data.FileInfo;

/**
 *
 * @author danko
 */
public class FolderContentModel extends DefaultTreeModel {
    private static class Command {
        Command(Type type, FileInfo info, String path) {
            this.type = type;
            this.info = info;
            this.path = path;
        }
        
        enum Type {
            INSERT,
            MOVE,
            REMOVE,
            UPDATE,
            ;
        }
        
        public final Type type;
        public final FileInfo info;
        public final String path;
    }
    
    private String folderName;
    private FileSystem fs;
    private HashMap<String, FileInfo> imageInfoMap = new HashMap<>();

    private List<Command> commands;
    
    public FolderContentModel(String name) {
        super(null, true);
        this.folderName = name;
        this.commands = new ArrayList<>();
    }
    
    public void addInsert(FileInfo info, String path) {
        commands.add(new Command(Command.Type.INSERT, info, path));
    }
    
    public void addRemove(FileInfo info) {
        Command cmd = commands.getLast();
        if (cmd.type == Command.Type.INSERT && cmd.info == info) {
            cmd = new Command(Command.Type.MOVE, cmd.info, cmd.path);
            commands.set(commands.size() - 1, cmd);
        }
        else {
            commands.add(new Command(Command.Type.REMOVE, info, null));
        }
    }
    
    public void addUpdate(FileInfo info, String path) {
        commands.add(new Command(Command.Type.UPDATE, info, path));
    }
    
    public void load(FileSystem fs) {
        try {
            var pThumbnails = fs.getPath("thumbnails");
            var pContents = fs.getPath("contents.json");
            
            byte[] contentsJson = Files.readAllBytes(pContents);
            ObjectMapper mapper = new ObjectMapper();
            List<FileInfo> fileInfoList = mapper.readValue(contentsJson, 
                    new TypeReference<List<FileInfo>>() {});
            
            var tree = FolderContents.buildTree(pThumbnails, folderName);
            super.setRoot(tree);
          
            fileInfoList.forEach(info -> imageInfoMap.put(info.getFileName(), info));
            
            var treeEnum = tree.depthFirstEnumeration();
            while (treeEnum.hasMoreElements()) {
                var node = (FolderContentNode)treeEnum.nextElement();
                String path = String.join("/", 
                        Arrays.stream(node.getUserObjectPath())
                        .map(obj -> Objects.toString(obj))
                        .filter(str -> !str.equals(folderName))
                        .toArray(n -> new String[n]));
                node.setFileInfo(imageInfoMap.get(path));
            }
            
            this.fs = fs;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage getThumbnail(String[] path) throws IOException {
        Path thumbnailPath = fs.getPath("thumbnails", path);
        byte[] thumbnailData = Files.readAllBytes(thumbnailPath);
        BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(thumbnailData));
        
        return thumbnail;
    }
    
    public FileInfo getImageInfo(String[] path) {
        return imageInfoMap.get(String.join("/", path));
    }
    
    private void replaceFileNames(List<Change> changes, int renIndex) {
        Change.Rename ren = (Change.Rename)changes.get(renIndex);
        String renOldName = "^" + Pattern.quote(ren.getOldName());
        String renNewName = Matcher.quoteReplacement(ren.getNewName());
        for (int j = renIndex + 1; j < changes.size(); j++) {
            Change ch = changes.get(j);
            switch (ch.getType()) {
                case RENAME_DIR:
                case RENAME_FILE:
                    Change.Rename nextRen = (Change.Rename)ch;
                    String oldName = nextRen.getOldName().replaceAll(renOldName, renNewName);
                    nextRen.setOldName(oldName);
                    changes.set(j, nextRen);
                    break;
            }
        }
    }

    private void replaceFolderPaths(List<Change> changes, int renIndex) {
        Change.Rename ren = (Change.Rename)changes.get(renIndex);

        String renOldName = "^" + Pattern.quote(ren.getOldName() + "/");
        String renNewName = Matcher.quoteReplacement(ren.getNewName() + "/");
        for (int j = renIndex + 1; j < changes.size(); j++) {
            Change ch = changes.get(j);
            switch (ch.getType()) {
                case ADD_FOLDER:
                    Change.AddFolder mkdir = (Change.AddFolder)ch;
                    String oldFolderName = mkdir.getFolderName();
                    String newFolderName = oldFolderName.replaceAll(renOldName, renNewName);
                    mkdir.setFolderName(newFolderName);
                    changes.set(j, mkdir);
                    break;
                case RENAME_DIR:
                case RENAME_FILE:
                    Change.Rename nextRen = (Change.Rename)ch;
                    String oldDirName = nextRen.getOldName().replaceAll(renOldName, renNewName);
                    String newDirName = nextRen.getNewName().replaceAll(renOldName, renNewName);
                    nextRen.setOldName(oldDirName);
                    nextRen.setNewName(newDirName);
                    changes.set(j, nextRen);
                    break;
            }
        }
    }
    
    public List<Change> getChanges() {
        List<Change> changes = new ArrayList<>();
        
        for (Command cmd: commands) {
            switch (cmd.type) {
                case MOVE:
                case UPDATE: {
                    Change.Rename ren = null;
                    switch (cmd.info.getFileType()) {
                        case FILE:
                            ren = new Change.RenameFile();
                            break;
                        case DIRECTORY:
                            ren = new Change.RenameDir();
                            break;
                    }
                    
                    ren.setOldName(cmd.info.getFileName());
                    ren.setNewName(cmd.path);
                    changes.add(ren);
                }
                default:
                    break;
            }
        }
        
        for (int i = 0; i < changes.size(); i++) {
            Change change = changes.get(i);
            if (change.getType() != RENAME_DIR)
                continue;
            
            replaceFolderPaths(changes, i);
        }
        
        for (int i = 0; i < changes.size(); i++) {
            Change change = changes.get(i);
            if (change.getType() != RENAME_DIR && change.getType() != RENAME_FILE)
                continue;
            
            replaceFileNames(changes, i);
        }
        
        return changes.stream().filter(ch -> {
            if (ch.getType() == RENAME_DIR || ch.getType() == RENAME_FILE) {
                Change.Rename ren = (Change.Rename)ch;
                return !Objects.equals(ren.getOldName(), ren.getNewName());
            }
            else {
                return true;
            }
        }).toList();
    }
}
