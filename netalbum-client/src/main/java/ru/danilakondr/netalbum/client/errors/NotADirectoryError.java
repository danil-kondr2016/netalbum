/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.errors;

import java.io.File;

/**
 *
 * @author danko
 */
public class NotADirectoryError extends IllegalArgumentException {
    private File file;
    
    public NotADirectoryError(File f) {
        super();
        this.file = f;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String getMessage() {
        return "Not a directory: " + file;
    }
}
