package ru.danilakondr.netalbum.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.transaction.Transactional;
import java.util.List;

public class NetAlbumDAO {
    private SessionFactory factory;

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @Transactional
    public void initSession(String sessionId, String directoryName) {
        Session s = factory.getCurrentSession();
        NetAlbumSession session = new NetAlbumSession();
        session.setSessionId(sessionId);
        session.setDirectoryName(directoryName);
        s.saveOrUpdate(session);
    }

    public void removeSession(NetAlbumSession session) {
        Session s = factory.openSession();
        Transaction t = s.beginTransaction();
        s.remove(session);
        t.commit();
    }

    public NetAlbumSession getSession(String sessionId) {
        Session s = factory.openSession();
        Query<NetAlbumSession> q = s.createQuery(
                "FROM NetAlbumSession WHERE sessionId=:id",
                NetAlbumSession.class);
        q.setParameter("id", sessionId);

        List<NetAlbumSession> lResults = q.getResultList();
        if (lResults.isEmpty())
            return null;

        return lResults.get(0);
    }

    public ImageFile getFile(String sessionId, String fileName) {
        Session s = factory.openSession();
        Query q = s.createQuery("FROM contents WHERE sessionId=:id AND fileName=:name");
        q.setParameter("id", sessionId);
        q.setParameter("name", fileName);

        List lResults = q.getResultList();
        if (lResults.isEmpty())
            return null;

        return (ImageFile) lResults.get(0);
    }

    public ImageFile getFile(long id) {
        Session s = factory.openSession();
        Query q = s.createQuery("FROM contents WHERE fileId=:id");
        q.setParameter("id", id);

        List lResults = q.getResultList();
        if (lResults.isEmpty())
            return null;

        return (ImageFile) lResults.get(0);
    }
}
