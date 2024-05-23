/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.connect;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import ru.danilakondr.netalbum.api.data.ImageData;
import ru.danilakondr.netalbum.api.message.Message;
import ru.danilakondr.netalbum.api.message.Request;
import ru.danilakondr.netalbum.api.message.Response;
import ru.danilakondr.netalbum.client.Images;

/**
 *
 * @author danko
 */
public class ImageLoader {
    public ImageLoader(NetAlbumService service, File directory) {
        this.service = service;
        this.directory = directory;
        
        service.subscribe(imgTaskSubscriber);
    }
    
    private final NetAlbumService service;
    private final ProgressMonitor monitor = new ProgressMonitor(null,
            "Loading images...",
            "",
            0, 100);
    
    private final File directory;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    
    private int nTotal = 0;
    private int nProcessed = 0;
    private final Flow.Subscriber<Message> imgTaskSubscriber = new Flow.Subscriber<Message>() {
        private Flow.Subscription subscription;
        
        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(Message item) {
            subscription.request(1);
            if (item.getType() != Message.Type.RESPONSE)
                return;
            
            Response resp = (Response)item;

            if (resp.getAnswerType() == Response.Type.IMAGE_ADDED) {
                Response.ImageAdded imgAdded = (Response.ImageAdded)resp;
                
                nProcessed++;
                EventQueue.invokeLater(() -> {
                    monitor.setNote(imgAdded.getImage().getFileName());
                    monitor.setProgress(nProcessed);
                    if (nProcessed == nTotal) {
                        JOptionPane.showMessageDialog(null, "All images has been loaded successfully.");
                    }
                });
            }
            else if (resp.getAnswerType() == Response.Type.ERROR) {
                subscription.cancel();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            System.out.printf("Error %s%n", throwable);
        }

        @Override
        public void onComplete() {
            System.out.println("Completed");
        }
    };
    
    private final Pattern VALID_IMAGE_PATTERN = Pattern.compile("^.*\\.([Pp][Nn][Gg]|[Jj][Pp][Ee]?[Gg]|[Jj][Ff][Ii][Ff])$");
    
    private boolean isValidImageFileName(String name) {
        Matcher m = VALID_IMAGE_PATTERN.matcher(name);
        return m.matches();
    }
    
    private int getAllImageCount() {
        try (var stream = Files.walk(directory.toPath())) {
            int result = (int) stream
                    .filter(p -> isValidImageFileName(p.toAbsolutePath().toString()))
                    .map(p -> p.toFile())
                    .count();
            
            return result;
        } catch (IOException ex) {
            return 0;
        }
    }
    
    private void scanAndSendAllImages() {
        try (var stream = Files.walk(directory.toPath())) {
            var files = stream
                    .filter(p -> isValidImageFileName(p.toAbsolutePath().toString()))
                    .map(p -> p.toFile());
            var fileIterator = files.iterator();
            
            while (fileIterator.hasNext()) {
                if (monitor.isCanceled()) {
                    break;
                }
                
                File f = fileIterator.next();
                loadImage(f);
            }
            
            exec.shutdown();
        } catch (IOException ex) {
            Logger.getLogger(ImageLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadImage(File file) {
        try {
            Request.AddImage addImage = new Request.AddImage();
            String name = directory.toPath()
                    .relativize(file.toPath())
                    .toString()
                    .replace(File.separatorChar, '/');

            ImageData imgData = Images.generateImage(file, name, 640, 480);
            addImage.setImage(imgData);

            service.sendRequest(addImage);
        } catch (IOException ex) {
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE));
            ex.printStackTrace(System.err);
            Logger.getLogger(ImageLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void execute() {
        nTotal = getAllImageCount();
        EventQueue.invokeLater(() -> {
            monitor.setMillisToPopup(0);
            monitor.setMillisToDecideToPopup(0);
            monitor.setMaximum(nTotal);
            monitor.setProgress(0);
        });
        exec.execute(() -> scanAndSendAllImages());
    }
}
