/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.danilakondr.netalbum.client.connect;

import java.util.List;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Flow;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import ru.danilakondr.netalbum.api.data.ImageData;
import ru.danilakondr.netalbum.api.message.Request;
import ru.danilakondr.netalbum.api.message.Response;
import ru.danilakondr.netalbum.client.Images;

/**
 *
 * @author danko
 */
public class LoadImagesTask extends SwingWorker<Void, File> {

    private int nTotal;

    public LoadImagesTask(NetAlbumService service, ProgressMonitor monitor, File directory) {
        this.service = service;
        this.monitor = monitor;
        this.directory = directory;
        
        this.service.subscribe(imgTaskSubscriber);
    }
    
    private final ProgressMonitor monitor;
    private final NetAlbumService service;
    private final File directory;
    
    private int nProcessed = 0;
    private final Flow.Subscriber<Response> imgTaskSubscriber = new Flow.Subscriber<Response>() {
        private Flow.Subscription subscription;
        
        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.printf("Subscribed(%s)%n", subscription);
            
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(Response item) {
            if (item.getType() == Response.Type.IMAGE_ADDED) {
                Response.ImageAdded imgAdded = (Response.ImageAdded)item;
                Logger.getLogger(LoadImagesTask.class.getName()).log(Level.INFO, "Image added: {0}", imgAdded.getImage().getFileName());
                
                nProcessed++;
                monitor.setNote(imgAdded.getImage().getFileName());
                monitor.setProgress(nProcessed);
                if (!monitor.isCanceled())
                    subscription.request(1);
            }
            else if (item.getType() == Response.Type.ERROR) {
                LoadImagesTask.this.cancel(true);
            }
        }

        @Override
        public void onError(Throwable throwable) {
        }

        @Override
        public void onComplete() {
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
                    .filter(p -> LoadImagesTask.this.isValidImageFileName(p.toAbsolutePath().toString()))
                    .map(p -> p.toFile())
                    .count();
            
            return result;
        } catch (IOException ex) {
            Logger.getLogger(LoadImagesTask.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    private void scanAllImages() {
        try (var stream = Files.walk(directory.toPath())) {
            stream
                    .filter(p -> LoadImagesTask.this.isValidImageFileName(p.toAbsolutePath().toString()))
                    .map(p -> p.toFile())
                    .forEach(f -> publish(f));
        } catch (IOException ex) {
            Logger.getLogger(LoadImagesTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        nTotal = getAllImageCount();
        
        monitor.setMinimum(0);
        monitor.setMaximum(nTotal);
        
        service.subscribe(imgTaskSubscriber);
        
        scanAllImages();
        return null;
    }
    
    @Override
    protected void done() {
        if (monitor.isCanceled() && nProcessed == nTotal)
            SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(null, "Completed"));
    }
    
    @Override
    protected void process(List<File> files) {
        for (File file : files) {
            try {
                Request.AddImage addImage = new Request.AddImage();
                String name = directory.toPath().relativize(file.toPath()).toString();
                
                ImageData imgData = Images.generateImage(file, name, 640, 480);
                addImage.setImage(imgData);
                
                service.putRequest(addImage);
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE));
                ex.printStackTrace(System.err);
                Logger.getLogger(LoadImagesTask.class.getName()).log(Level.SEVERE, null, ex);
                cancel(true);
            }
        }
    }
}
