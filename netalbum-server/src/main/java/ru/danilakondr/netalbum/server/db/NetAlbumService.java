package ru.danilakondr.netalbum.server.db;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import ru.danilakondr.netalbum.api.data.FileInfo;
import ru.danilakondr.netalbum.api.data.FilenameUtils;

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
    public void putDirectory(String sessionId, String dirName) {
        ImageFile file = dao.getImageFile(sessionId, dirName);
        if (file != null && file.getFileType() == ImageFile.Type.FILE)
            throw new FileAlreadyExistsError(dirName);
        
        if (file == null) {
            ImageFile dir = new ImageFile();
            dir.setFileType(ImageFile.Type.DIRECTORY);
            dir.setSessionId(sessionId);
            dir.setFileName(dirName);
            dir.setFirstName(dirName);
            dir.setFileSize(0);
            dir.setImgWidth(0);
            dir.setImgHeight(0);
            dir.setThumbnail(null);
            
            dao.putImageFile(dir);
        }
    }
    
    @Transactional
    public void putDirectories(String sessionId, String dirName) {
        String dir = dirName;
        while (!dir.isEmpty()) {
            putDirectory(sessionId, dir);
            dir = FilenameUtils.dirName(dir);
        }
    }

    @Transactional
    public void putImage(String sessionId, ImageData data) {
        if (dao.getImageFile(sessionId, data.getFileName()) != null)
            throw new FileAlreadyExistsError(data.getFileName());
        
        String dirName = FilenameUtils.dirName(data.getFileName());
        putDirectories(sessionId, dirName);

        ImageFile file = new ImageFile();
        file.setFileType(ImageFile.Type.FILE);
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
        if (file.getFileType() != ImageFile.Type.FILE)
            throw new IllegalArgumentException("CANNOT_MOVE_A_DIRECTORY");

        if (dao.getImageFile(sessionId, newName) != null)
            throw new FileAlreadyExistsError(newName);
        
        String newDirName = FilenameUtils.dirName(newName);
        ImageFile newDir = dao.getImageFile(sessionId, newDirName);
        if (newDir == null)
            throw new IllegalArgumentException("DIRECTORY_NOT_FOUND " + newDirName);

        file.setFirstName(oldName);
        file.setFileName(newName);
        dao.putImageFile(file);
    }
    
    @Transactional
    public void renameDir(String sessionId, String oldName, String newName) {
        ImageFile dir = dao.getImageFile(sessionId, oldName);
        if (dir == null)
            throw new FileNotFoundError(oldName);
        if (dir.getFileType() != ImageFile.Type.DIRECTORY)
            throw new IllegalArgumentException("NOT_A_DIRECTORY");
        
        ImageFile newDir = dao.getImageFile(sessionId, newName);
        if (newDir != null)
            throw new FileAlreadyExistsError(newName);
        
        dao.getSession(sessionId)
                .getFiles().stream()
                .filter(file -> file.getFileName().startsWith(oldName))
                .forEach(file -> {
                    String oldFileName = file.getFileName();
                    String newFileName = oldFileName.replace(oldName, newName);
                    
                    file.setFirstName(oldFileName);
                    file.setFileName(newFileName);
                    
                    dao.putImageFile(file);
                });
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
        
        return s.getFiles().stream()
                .filter(f -> f.getFileType() == ImageFile.Type.FILE)
                .count();
    }

    @Transactional
    public byte[] generateArchiveWithThumbnails(String sessionId) {
        NetAlbumSession s = dao.getSession(sessionId);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ZipOutputStream out = new ZipOutputStream(os)) {
            List<FileInfo> infoList = new ArrayList<>();
            
            ZipEntry thumbnailsEntry = new ZipEntry("thumbnails/");
            out.putNextEntry(thumbnailsEntry);
            out.closeEntry();
            
            for (ImageFile f : s.getFiles()) {
                if (f.getFileName() == null)
                    continue;
                
                if (f.getFileType() == ImageFile.Type.DIRECTORY) {
                    FileInfo info = new FileInfo(FileInfo.Type.DIRECTORY);
                    info.setFileName(f.getFileName());
                    infoList.add(info);
                    
                    String dirName = f.getFileName();
                    if (!dirName.endsWith("/"))
                        dirName += "/";
                    
                    ZipEntry dirEntry = new ZipEntry("thumbnails/" + dirName);
                    out.putNextEntry(dirEntry);
                }
                else {
                    FileInfo.Image info = new FileInfo.Image();
                    info.setFileName(f.getFileName());
                    info.setFileSize(f.getFileSize());
                    info.setWidth(f.getImgWidth());
                    info.setHeight(f.getImgHeight());
                    infoList.add(info);

                    ZipEntry fileEntry = new ZipEntry("thumbnails/" + f.getFileName());
                    out.putNextEntry(fileEntry);
                    ByteArrayInputStream is = new ByteArrayInputStream(f.getThumbnail());
                    is.transferTo(out);
                }
                out.closeEntry();
            }
            
            ZipEntry contentsEntry = new ZipEntry("contents.json");
            out.putNextEntry(contentsEntry);
            ObjectMapper mapper = new ObjectMapper();
            byte[] contentsJson = mapper.writeValueAsBytes(infoList);
            out.write(contentsJson);

            out.finish();
            return os.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
            throw new IllegalStateException(e);
        }
    }
}
