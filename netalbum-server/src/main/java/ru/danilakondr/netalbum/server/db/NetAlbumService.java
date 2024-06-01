package ru.danilakondr.netalbum.server.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.danilakondr.netalbum.api.data.ImageData;
import ru.danilakondr.netalbum.api.utils.FileIdGenerator;
import ru.danilakondr.netalbum.server.error.FileAlreadyExistsError;
import ru.danilakondr.netalbum.server.error.NonExistentSession;
import ru.danilakondr.netalbum.server.model.ImageFile;
import ru.danilakondr.netalbum.server.model.NetAlbumSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import ru.danilakondr.netalbum.api.data.ChangeCommand;
import ru.danilakondr.netalbum.api.data.ChangeInfo;
import ru.danilakondr.netalbum.api.data.FileInfo;
import ru.danilakondr.netalbum.api.utils.FilenameUtils;
import ru.danilakondr.netalbum.server.error.NotADirectoryError;
import ru.danilakondr.netalbum.server.model.ChangeQueueRecord;

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
            dir.setFileId(FileIdGenerator.generate(dirName));
            dir.setFileName(dirName);
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
        file.setFileId(data.getFileId());
        file.setFileName(data.getFileName());
        file.setFileSize(data.getFileSize());
        file.setSessionId(sessionId);
        file.setImgWidth(data.getWidth());
        file.setImgHeight(data.getHeight());
        file.setThumbnail(data.getThumbnail());

        dao.putImageFile(file);
    }

    @Transactional
    public void rename(String sessionId, long fileId, String newName) {
        ImageFile file = dao.getImageFile(sessionId, fileId);
        if (file.getFileType() != ImageFile.Type.FILE) {
            renameDir(sessionId, fileId, newName);
            return;
        }

        if (dao.getImageFile(sessionId, newName) != null)
            throw new FileAlreadyExistsError(newName);

        file.setFileName(newName);
        dao.putImageFile(file);
    }
    
    @Transactional
    private void renameDir(String sessionId, long fileId, String newName) {
        ImageFile dir = dao.getImageFile(sessionId, fileId);
        if (dir.getFileType() != ImageFile.Type.DIRECTORY)
            throw new NotADirectoryError(dir.getFileName());
        
        String oldName = dir.getFileName();
        
        ImageFile newDir = dao.getImageFile(sessionId, newName);
        if (newDir != null)
            throw new FileAlreadyExistsError(newName);
        
        dao.getSession(sessionId)
                .getFiles().stream()
                .filter(file -> file.getFileName().startsWith(oldName))
                .forEach(file -> {
                    String oldFileName = file.getFileName();
                    String newFileName = oldFileName.replace(oldName, newName);
                    
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
                    info.setFileId(f.getFileId());
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
                    info.setFileId(f.getFileId());
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
    
    @Transactional
    public void putChange(String sessionId, ChangeCommand change) {
        ChangeQueueRecord record = new ChangeQueueRecord();
        record.setSessionId(sessionId);
        record.setChangeType(change.getType());

        switch (change.getType()) {
            case ADD_FOLDER:
                ChangeCommand.AddFolder addFolder = (ChangeCommand.AddFolder)change;
                record.setFileId(addFolder.getFileId());
                record.setNewName(addFolder.getFolderName());
                break;
            case RENAME:
                ChangeCommand.Rename ren = (ChangeCommand.Rename)change;
                record.setOldName(dao.getImageFile(sessionId, ren.getFileId()).getFileName());
                record.setFileId(ren.getFileId());
                record.setNewName(ren.getNewName());
                break;
        }
        
        dao.putChangeQueueRecord(record);
    }
    
    @Transactional
    public List<ChangeInfo> getChangesList(String sessionId) {
        List<ChangeInfo> result = new ArrayList<>();
        NetAlbumSession session = dao.getSession(sessionId);
        if (session == null)
            throw new NonExistentSession(sessionId);
        
        List<ChangeQueueRecord> records = session.getChangesFromQueue();
        for (ChangeQueueRecord record: records) {
            switch (record.getChangeType()) {
                case ADD_FOLDER:
                    ChangeInfo.AddFolder addFolder = 
                            new ChangeInfo.AddFolder(record.getNewName());
                    result.add(addFolder);
                    break;
                case RENAME:
                    ChangeInfo.Rename rename = new ChangeInfo.Rename(
                            record.getOldName(), 
                            record.getNewName());
                    result.add(rename);
                    break;
            }
        }
        
        return result;
    }
    
    @Transactional
    public List<ChangeInfo> moveChanges(String sessionId) {
        List<ChangeInfo> result = getChangesList(sessionId);
        dao.clearChangeQueue(sessionId);
        
        return result;
    }
    
    @Transactional
    public boolean isChangeQueueEmpty(String sessionId) {
        return dao.isChangeQueueEmpty(sessionId);
    }
}
