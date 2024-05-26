/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.api.data;

/**
 *
 * @author danko
 */
public class FilenameUtils {
    public static String basename(String path) {
        String[] segments = path.split("/");
        return segments[segments.length - 1];
    }
    
    public static String dirName(String path) {
        String[] segments = path.split("/");
        String baseName = segments[segments.length - 1];
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.length - 1; i++) {
            sb.append(segments[i]);
            if (i < segments.length - 2)
                sb.append("/");
        }
        
        return sb.toString();
    }
}
