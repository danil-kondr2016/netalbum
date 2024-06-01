package ru.danilakondr.netalbum.server.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.danilakondr.netalbum.server.model.ImageFile;
import ru.danilakondr.netalbum.server.model.NetAlbumSession;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import ru.danilakondr.netalbum.server.model.ChangeQueueRecord;

@Repository
public class NetAlbumDAO {
    private SessionFactory factory;

    @Autowired
    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @Transactional
    public void initSession(NetAlbumSession session) {
        Session s = factory.getCurrentSession();
        s.saveOrUpdate(session);
    }

    @Transactional
    public void removeSession(NetAlbumSession session) {
        Session s = factory.getCurrentSession();
        s.remove(session);
    }

    @Transactional
    public NetAlbumSession getSession(String sessionId) {
        Session s = factory.getCurrentSession();
        Query<NetAlbumSession> q = s.createQuery(
                "FROM NetAlbumSession WHERE sessionId=:id",
                NetAlbumSession.class);
        q.setParameter("id", sessionId);

        List<NetAlbumSession> lResults = q.getResultList();
        if (lResults.isEmpty())
            return null;

        return lResults.get(0);
    }

    @Transactional
    public void putImageFile(ImageFile file) {
        Session s = factory.getCurrentSession();
        s.saveOrUpdate(file);
    }
    
    @Transactional
    public void removeImageFile(ImageFile file) {
        Session s = factory.getCurrentSession();
        s.remove(file);
    }

    @Transactional
    public ImageFile getImageFile(String sessionId, String name) {
        Session s = factory.getCurrentSession();
        Query<ImageFile> q = s.createQuery(
                "FROM ImageFile WHERE sessionId=:id AND fileName=:name",
                        ImageFile.class)
                .setParameter("id", sessionId)
                .setParameter("name", name);

        List<ImageFile> results = q.getResultList();
        if (results.isEmpty())
            return null;
        if (results.size() > 1)
            throw new IllegalArgumentException("Repeating files");

        return results.get(0);
    }
    
    @Transactional
    public ImageFile getImageFile(String sessionId, long id) {
        Session s = factory.getCurrentSession();
        Query<ImageFile> q = s.createQuery(
                "FROM ImageFile WHERE sessionId=:sessionId AND fileId=:fileId",
                        ImageFile.class)
                .setParameter("sessionId", sessionId)
                .setParameter("fileId", id);

        List<ImageFile> results = q.getResultList();
        if (results.isEmpty())
            return null;
        if (results.size() > 1)
            throw new IllegalArgumentException("Repeating files");

        return results.get(0);
    }
    
    @Transactional
    public void putChangeQueueRecord(ChangeQueueRecord change) {
        Session s = factory.getCurrentSession();
        s.saveOrUpdate(change);
    }
    
    @Transactional
    public boolean isChangeQueueEmpty(String sessionId) {
        Session s = factory.getCurrentSession();
        
        Query<ChangeQueueRecord> q = 
                s.createQuery("FROM ChangeQueueRecord WHERE sessionId=:id", 
                        ChangeQueueRecord.class)
                        .setParameter("id", sessionId)
                ;
        
        return q.getResultList().isEmpty();
    }

    @Transactional
    public void clearChangeQueue(String sessionId) {
        Session s = factory.getCurrentSession();
        
        Query<ChangeQueueRecord> q = 
                s.createQuery("FROM ChangeQueueRecord WHERE sessionId=:id",
                        ChangeQueueRecord.class)
                .setParameter("id", sessionId)
                ;
        
        List<ChangeQueueRecord> l = q.getResultList();
        if (l == null)
            return;
        
        if (l.isEmpty())
            return;
        
        for (ChangeQueueRecord c: l) {
            s.remove(c);
        }
    }
}
