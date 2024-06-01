/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package ru.danilakondr.netalbum.api.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.danilakondr.netalbum.api.utils.FilenameUtils;

/**
 *
 * @author danko
 */
public class FilenameUtilsTest {
    
    public FilenameUtilsTest() {
    }
    
    @Test
    public void dirNameTest() {
        String fileName = "directory/test.png";
        String dirName = FilenameUtils.dirName(fileName);
        assertEquals("directory", dirName);
    }
}
