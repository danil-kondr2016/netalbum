package ru.danilakondr.fstreemodel;

import java.util.Comparator;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Compares to {@link java.nio.file.Path} objects together, for collation 
 * (sorting) purposes.
 * <p>Instances of this class sort null, then folders then files, keeping
 * everything in {@link String#CASE_INSENSITIVE_ORDER CASE_INSENSITIVE_ORDER}.
 * </p>
 */
public class PathComparator implements Comparator<Path> {
    public static final Comparator INSTANCE = new PathComparator();

    @Override
    public int compare(Path p1, Path p2) {
        if (null == p1 && null == p2) {
            return 0;
        } else if (null == p1) {
            return -1;
        } else if (null == p2) {
            return 1;
        }
        
        if (Files.isDirectory(p1) && !Files.isDirectory(p2)) {
            return -1;
        } else if (!Files.isDirectory(p1) && Files.isDirectory(p2)) {
            return 1;
        } else {
            return p1.getFileName().toString().compareToIgnoreCase(p2.getFileName().toString());
        }
    }
}
