package ru.danilakondr.netalbum.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;

public class Database {
    private SessionFactory sf = null;
    private static Database db = null;

    private Database() throws IOException {
        sf = new Configuration()
                .addAnnotatedClass(ImageFile.class)
                .addAnnotatedClass(NetAlbumSession.class)
                .configure()
                .buildSessionFactory();
    }

    public SessionFactory getSessionFactory() {
        return sf;
    }

    public static synchronized Database getInstance() {
        try {
            if (db == null)
                db = new Database();

            return db;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void closeInstance() {
        db.getSessionFactory().close();
        db = null;
    }
}
