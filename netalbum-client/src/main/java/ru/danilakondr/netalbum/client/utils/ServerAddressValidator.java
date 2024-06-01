/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.utils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author danko
 */
public class ServerAddressValidator {
    public static boolean isValid(String url) {
        try {
            URI uri = URI.create("http://" + url);
            
            if (uri.getQuery() != null && !uri.getQuery().isEmpty())
                return false;
            if (uri.getFragment() != null && !uri.getFragment().isEmpty())
                return false;
        }
        catch (IllegalArgumentException e) {
            System.out.println(e);
            return false;
        }
        
        return true;
    }
}
