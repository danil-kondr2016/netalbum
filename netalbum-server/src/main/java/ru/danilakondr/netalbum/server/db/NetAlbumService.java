package ru.danilakondr.netalbum.server.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.danilakondr.netalbum.api.data.ImageData;
import ru.danilakondr.netalbum.server.error.FileAlreadyExistsError;
import ru.danilakondr.netalbum.server.error.FileNotFoundError;
import ru.danilakondr.netalbum.server.error.NonExistentSession;
import ru.danilakondr.netalbum.server.model.ImageFile;
import ru.danilakondr.netalbum.server.model.NetAlbumSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class NetAlbumService {
    private NetAlbumDAO dao;

    @Autowired
    public void setDAO(NetAlbumDAO dao) {
        this.dao = dao;
    }

    @Transactional
    public void initSession(String sessionId, String directoryName) {
        NetAlbumSession session = new NetAlbumSession();
        session.setSessionId(sessionId);
        session.setDirectoryName(directoryName);

        dao.initSession(session);
    }

    @Transactional
    public void removeSession(String sessionId) {
        NetAlbumSession session = dao.getSession(sessionId);
        if (session == null)
            throw new NonExistentSession(sessionId);

        dao.removeSession(session);
    }

    @Transactional
    public NetAlbumSession getSession(String sessionId) {
        return dao.getSession(sessionId);
    }

    @Transactional
    public void putImage(String sessionId, ImageData data) {
        if (dao.getImageFile(sessionId, data.getFileName()) != null)
            throw new FileAlreadyExistsError(data.getFileName());

        ImageFile file = new ImageFile();
        file.setFileName(data.getFileName());
        file.setFirstName(data.getFileName());
        file.setFileSize(data.getFileSize());
        file.setSessionId(sessionId);
        file.setImgWidth(data.getWidth());
        file.setImgHeight(data.getHeight());
        file.setThumbnail(data.getThumbnail());

        dao.putImageFile(file);
    }

    @Transactional
    public void renameFile(String sessionId, String oldName, String newName) {
        ImageFile file = dao.getImageFile(sessionId, oldName);
        if (file == null)
            throw new FileNotFoundError(oldName);

        if (dao.getImageFile(sessionId, newName) != null)
            throw new FileAlreadyExistsError(newName);

        file.setFileName(newName);
        dao.putImageFile(file);
    }

    @Transactional
    public long getDirectorySize(String sessionId) {
        NetAlbumSession s = dao.getSession(sessionId);

        return s.getFiles().stream()
                .mapToLong(ImageFile::getFileSize)
                .sum();
    }
    
    @Transactional
    public long getImageCount(String sessionId) {
        NetAlbumSession s = dao.getSession(sessionId);
        
        return s.getFiles().stream().count();
    }

    @Transactional
    public byte[] generateArchiveWithThumbnails(String sessionId) {
        NetAlbumSession s = dao.getSession(sessionId);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ZipOutputStream out = new ZipOutputStream(os)) {
            for (ImageFile f : s.getFiles()) {
                if (f.getFileName() == null)
                    continue;

                ZipEntry entry = new ZipEntry(f.getFileName());
                out.putNextEntry(entry);
                ByteArrayInputStream is = new ByteArrayInputStream(f.getThumbnail());
                is.transferTo(out);
                out.closeEntry();
            }

            out.finish();
            return os.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
            throw new IllegalStateException(e);
        }
    }
}
