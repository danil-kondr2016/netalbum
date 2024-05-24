/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.contents;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author danko
 */
public class FolderContents {
    public static FolderContentNode buildTree(Path path, String rootName) {
        var root = FolderContentNode.createDirectory(rootName);

        try {
            Files.walkFileTree(path, Set.of(), 1, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isDirectory())
                        root.add(buildTree(file, file.getFileName().toString()));
                    else
                        root.add(FolderContentNode.createImage(file.getFileName().toString()));
                    
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.TERMINATE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null)
                        return FileVisitResult.TERMINATE;

                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException ignored) {}
        
        return root;
    }

    public static FolderContentNode buildTree(Path path) {
        return buildTree(path, path.getFileName().toString());
    }
}
