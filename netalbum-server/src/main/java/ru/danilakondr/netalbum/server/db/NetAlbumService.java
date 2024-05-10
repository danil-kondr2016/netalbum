package ru.danilakondr.netalbum.server.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.danilakondr.netalbum.api.ImageData;
import ru.danilakondr.netalbum.server.error.FileAlreadyExistsError;
import ru.danilakondr.netalbum.server.error.FileNotFoundError;
import ru.danilakondr.netalbum.server.error.NonExistentSession;
import ru.danilakondr.netalbum.server.model.ImageFile;
import ru.danilakondr.netalbum.server.model.NetAlbumSession;

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

    public void renameFile(String sessionId, String oldName, String newName) {
        ImageFile file = dao.getImageFile(sessionId, oldName);
        if (file == null)
            throw new FileNotFoundError(oldName);

        if (dao.getImageFile(sessionId, newName) != null)
            throw new FileAlreadyExistsError(newName);

        file.setFileName(newName);
        dao.putImageFile(file);
    }
}
