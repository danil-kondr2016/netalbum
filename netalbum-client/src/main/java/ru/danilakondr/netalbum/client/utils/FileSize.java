/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.utils;

import java.util.ResourceBundle;
import java.math.BigDecimal;

/**
 *
 * @author danko
 */
public class FileSize {
    private 
    static final ResourceBundle bundle = 
        ResourceBundle.getBundle("ru/danilakondr/netalbum/client/gui/Strings");
    
    private static final String UNIT_LETTERS = bundle.getString("FileSize.unitLetters");
    private static final String BYTE_SIGN = bundle.getString("FileSize.byteSign");
    private static final String I = bundle.getString("FileSize.i");
    
    private static final long KiB = (1L << 10);
    private static final long MiB = (1L << 20);
    private static final long GiB = (1L << 30);
    private static final long TiB = (1L << 40);
    private static final long PiB = (1L << 50);
    private static final long EiB = (1L << 60);
    
    public static String getDisplayFileSize(long size) {
        if (size < 2*KiB)
            return String.format("%,d %s", size, BYTE_SIGN);
        else if (size < MiB)
            return String.format("%,d %c%s%s", size/KiB, 
                    UNIT_LETTERS.charAt(0), I, BYTE_SIGN);
        else if (size < GiB)
            return String.format("%,d %c%s%s", size/MiB, 
                    UNIT_LETTERS.charAt(1), I, BYTE_SIGN);
        else if (size < TiB)
            return String.format("%,d %c%s%s", size/GiB, 
                    UNIT_LETTERS.charAt(2), I, BYTE_SIGN);
        else if (size < PiB)
            return String.format("%,d %c%s%s", size/TiB, 
                    UNIT_LETTERS.charAt(3), I, BYTE_SIGN);
        else if (size < EiB)
            return String.format("%,d %c%s%s", size/PiB, 
                    UNIT_LETTERS.charAt(4), I, BYTE_SIGN);
        else 
            return String.format("%,d %c%s%s", size/EiB, 
                    UNIT_LETTERS.charAt(5), I, BYTE_SIGN);
    }

}
