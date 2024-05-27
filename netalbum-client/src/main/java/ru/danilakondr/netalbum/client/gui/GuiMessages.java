/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.gui;

import java.util.ResourceBundle;

/**
 *
 * @author danko
 */
public class GuiMessages {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings");
    
    public static final String ERROR_TITLE = BUNDLE.getString("messageTitle.Error");
    
    public static final String INVALID_REQUEST = BUNDLE.getString("message.invalidRequest");
    public static final String NON_EXISTENT_SESSION = BUNDLE.getString("message.nonExistentSession");
    public static final String CLIENT_NOT_CONNECTED = BUNDLE.getString("message.clientNotConnected");
    public static final String CLIENT_ALREADY_CONNECTED = BUNDLE.getString("message.clientAlreadyConnected");
    public static final String NOT_AN_INITIATOR = BUNDLE.getString("message.notAnInitiator");
    public static final String NOT_A_VIEWER = BUNDLE.getString("message.notAViewer");
    public static final String FILE_NOT_FOUND = BUNDLE.getString("message.fileNotFound");
    public static final String FILE_ALREADY_EXISTS = BUNDLE.getString("message.fileAlreadyExists");
    public static final String EXCEPTION = BUNDLE.getString("message.exception");
}
