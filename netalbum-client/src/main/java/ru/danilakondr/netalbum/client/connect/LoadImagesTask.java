package ru.danilakondr.netalbum.client.connect;

import ru.danilakondr.netalbum.api.data.ImageData;
import ru.danilakondr.netalbum.api.request.Request;
import ru.danilakondr.netalbum.api.response.Response;
import ru.danilakondr.netalbum.api.response.Status;
import ru.danilakondr.netalbum.client.Images;
import ru.danilakondr.netalbum.client.LocalizedMessages;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.http.WebSocket;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LoadImagesTask extends SwingWorker<Response, File> {
    @Override
    protected Response doInBackground() throws Exception {
        return null;
    }
    /*
    private final File directory;
    private final ResponseListener listener;
    private final WebSocket socket;
    private int totalFiles = 0;
    private int totalProcessed = 0;
    private final int thumbnailWidth;
    private final int thumbnailHeight;
    private Response lastResponse = null;

    private static final Pattern VALID_IMAGE_NAME = Pattern.compile(
            "^.*?(\\.[Pp][Nn][Gg]|\\.[Jj][Pp][Ee]?[Gg]|\\.[Jj][Ff][Ii][Ff])$");

    static final FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            Matcher m = VALID_IMAGE_NAME.matcher(name);
            return m.matches();
        }
    };

    private static class FileEnumerator implements FileVisitor<Path> {
        private final AtomicInteger count = new AtomicInteger(0);

        private final File directory;

        public FileEnumerator(File directory) {
            if (!directory.isDirectory())
                throw new IllegalArgumentException(LocalizedMessages.notADirectoryError(directory));

            this.directory = directory;
        }

        public int getCount() {
            return count.get();
        }

        public void enumerate() throws IOException {
            Path p = directory.toPath();
            Files.walkFileTree(p, this);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (attrs.isDirectory())
                return FileVisitResult.CONTINUE;

            if (attrs.isOther())
                return FileVisitResult.CONTINUE;

            String filePath = directory.toPath().relativize(file.toAbsolutePath()).toString();
            if (filter.accept(directory, filePath))
                count.incrementAndGet();

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.TERMINATE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.SKIP_SIBLINGS;
        }
    }

    public LoadImagesTask(File directory, ResponseListener listener, WebSocket socket, int thumbnailWidth, int thumbnailHeight) {
        this.directory = directory;
        this.listener = listener;
        this.socket = socket;
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;

        if (!directory.isDirectory())
            throw new IllegalArgumentException(LocalizedMessages.notADirectoryError(directory));
    }

    @Override
    protected Response doInBackground() throws Exception {
        totalProcessed = 0;

        FileEnumerator enumerator = new FileEnumerator(directory);
        enumerator.enumerate();
        totalFiles = enumerator.getCount();

        try (Stream<Path> s = Files.walk(directory.toPath())) {
            s.map(Path::toFile)
                    .filter(f -> filter.accept(directory, f.getName()))
                    .forEach(this::publish);
        }
        catch (RuntimeException e) {
            return new Response(Status.ERROR);
        }

        return lastResponse;
    }

    @Override
    protected void process(List<File> chunks) {
        for (File f : chunks) {
            String path = directory.toPath().relativize(f.toPath()).toString();
            try {
                ImageData data = Images.generateImage(f, path, thumbnailWidth, thumbnailHeight);
                Request.AddImages req = new Request.AddImages();
                req.setImages(List.of(data));

                SendRequestTask task = new SendRequestTask(listener, socket, req);
                task.execute();
                Response resp = task.get();
                lastResponse = resp;
                if (resp.getStatus() != Status.SUCCESS)
                    return;

                totalProcessed++;
                setProgress(totalProcessed * 100 / totalFiles);
            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
     */
}
