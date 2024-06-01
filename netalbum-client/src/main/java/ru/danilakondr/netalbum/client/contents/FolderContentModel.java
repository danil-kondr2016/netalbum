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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.tree.DefaultTreeModel;
import ru.danilakondr.netalbum.api.data.ChangeCommand;
import ru.danilakondr.netalbum.api.data.FileInfo;
import ru.danilakondr.netalbum.api.data.ChangeInfo;

/**
 *
 * @author danko
 */
public class FolderContentModel extends DefaultTreeModel {
    private static class Command {
        Command(Type type, long fileId, String path) {
            this.type = type;
            this.fileId = fileId;
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
        public final long fileId;
        public final String path;
    }
    
    private String folderName;
    private FileSystem fs;
    private final HashMap<String, FileInfo> fileInfoMap = new HashMap<>();

    private List<Command> commands;
    
    public FolderContentModel(String name) {
        super(null, true);
        this.folderName = name;
        this.commands = new ArrayList<>();
    }
    
    public void addInsert(long fileId, String path) {
        commands.add(new Command(Command.Type.INSERT, fileId, path));
    }
    
    public void addRemove(long fileId) {
        Command cmd = commands.get(commands.size() - 1);
        if (cmd.type == Command.Type.INSERT && cmd.fileId == fileId) {
            cmd = new Command(Command.Type.MOVE, cmd.fileId, cmd.path);
            commands.set(commands.size() - 1, cmd);
        }
        else {
            commands.add(new Command(Command.Type.REMOVE, fileId, null));
        }
    }
    
    public void addUpdate(long fileId, String path) {
        commands.add(new Command(Command.Type.UPDATE, fileId, path));
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
          
            fileInfoList.forEach(info -> fileInfoMap.put(info.getFileName(), info));
            
            var treeEnum = tree.depthFirstEnumeration();
            while (treeEnum.hasMoreElements()) {
                var node = (FolderContentNode)treeEnum.nextElement();
                String path = String.join("/", 
                        Arrays.stream(node.getUserObjectPath())
                        .map(obj -> Objects.toString(obj))
                        .filter(str -> !str.equals(folderName))
                        .toArray(n -> new String[n]));
                node.setFileInfo(fileInfoMap.get(path));
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
        return fileInfoMap.get(String.join("/", path));
    }

    public List<ChangeCommand> getChanges() {
        List<ChangeCommand> changes = new ArrayList<>();
        
        for (Command cmd: commands) {
            switch (cmd.type) {
                case MOVE:
                case UPDATE: {
                    ChangeCommand.Rename ren = new ChangeCommand.Rename();
                    ren.setFileId(cmd.fileId);
                    ren.setNewName(cmd.path);
                    changes.add(ren);
                    break;
                }
                case INSERT: {
                    ChangeCommand.AddFolder mkdir = new ChangeCommand.AddFolder();
                    mkdir.setFileId(cmd.fileId);
                    mkdir.setFolderName(cmd.path);
                    changes.add(mkdir);
                    break;
                }
                default:
                    break;
            }
        }
        
        return changes;
    }
}
