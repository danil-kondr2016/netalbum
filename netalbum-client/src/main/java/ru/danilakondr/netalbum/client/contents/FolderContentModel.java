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
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import ru.danilakondr.netalbum.api.data.Change;
import ru.danilakondr.netalbum.api.data.ImageInfo;

/**
 *
 * @author danko
 */
public class FolderContentModel extends DefaultTreeModel {
    private String folderName;
    private FileSystem fs;
    private HashMap<String, ImageInfo> imageInfoMap = new HashMap<>();

    
    private List<Change> changes;
    
    public FolderContentModel(String name) {
        super(null);
        this.folderName = name;
        this.changes = new ArrayList<>();
    }
    
    public void load(FileSystem fs) {
        try {
            var pThumbnails = fs.getPath("thumbnails");
            var pContents = fs.getPath("contents.json");
            
            byte[] contentsJson = Files.readAllBytes(pContents);
            ObjectMapper mapper = new ObjectMapper();
            List<ImageInfo> imgInfos = mapper.readValue(contentsJson, new TypeReference<List<ImageInfo>>() {});
            
            var tree = FolderContents.buildTree(pThumbnails, folderName);
            super.setRoot(tree);
          
            imgInfos.forEach(info -> imageInfoMap.put(info.getFileName(), info));
            
            var treeEnum = tree.depthFirstEnumeration();
            while (treeEnum.hasMoreElements()) {
                FolderContentNode node = (FolderContentNode)treeEnum.nextElement();
                if (node.isDirectory())
                    continue;
                
                String path = String.join("/", 
                        Arrays.stream(node.getUserObjectPath())
                        .map(obj -> Objects.toString(obj))
                        .filter(str -> !str.equals(folderName))
                        .toArray(n -> new String[n]));
                node.setImageInfo(imageInfoMap.get(path));
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
    
    public ImageInfo getImageInfo(String[] path) {
        return imageInfoMap.get(String.join("/", path));
    }
}
