/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/SpringFramework/AbstractController.java to edit this template
 */
package ru.danilakondr.netalbum.server.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.danilakondr.netalbum.server.db.NetAlbumService;

/**
 *
 * @author danko
 */
@Controller
public class DataController {
    private NetAlbumService service;

    @Autowired
    public void setService(NetAlbumService service) {
        this.service = service;
    }

    @RequestMapping(method=RequestMethod.GET, value="/archive/{id}")
    public void sendThumbnailsArchive(@PathVariable("id") String id, 
            HttpServletResponse response) throws IOException 
    {
        byte[] thumbnailsZip = service.generateArchiveWithThumbnails(id);
        response.setContentType("application/zip");
        response.setContentLength(thumbnailsZip.length);
        response.setHeader("Content-Disposition", "attachment; filename=\"%s.zip\"".formatted(id));
        
        try (var out = response.getOutputStream()){
            out.write(thumbnailsZip);
            response.flushBuffer();
        }
    }
}
