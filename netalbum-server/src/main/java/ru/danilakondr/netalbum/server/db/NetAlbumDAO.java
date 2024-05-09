package ru.danilakondr.netalbum.server.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.danilakondr.netalbum.server.model.NetAlbumSession;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
}
