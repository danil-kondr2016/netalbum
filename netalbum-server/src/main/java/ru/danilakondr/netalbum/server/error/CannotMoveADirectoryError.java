/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.server.error;

/**
 *
 * @author danko
 */
public class CannotMoveADirectoryError extends RuntimeException {

    public CannotMoveADirectoryError(String fileName) {
        super(fileName);
    }
    
}
