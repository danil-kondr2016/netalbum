package ru.danilakondr.netalbum.client;

import ru.danilakondr.netalbum.api.message.Request;
import ru.danilakondr.netalbum.api.message.Response;
import ru.danilakondr.netalbum.client.connect.NetAlbumService;
import ru.danilakondr.netalbum.client.gui.StartDialog;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Flow;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import ru.danilakondr.netalbum.api.message.Message;
import ru.danilakondr.netalbum.client.connect.ImageLoader;

public class ClientApp {
    private final Configuration cfg;
    private final NetAlbumService service = new NetAlbumService();
    
    private String sessionId, address;

    public ClientApp(Configuration cfg) {
        this.cfg = cfg;
    }

    public void run() {
        if (cfg.containsSessions()) {
            // TODO предложить пользователю восстановить сессии
        }

        String defaultURL = cfg.getServerAddress();
        StartDialog startDlg = new StartDialog(null, true);
        startDlg.setServerAddress(defaultURL);
        startDlg.setVisible(true);

        switch (startDlg.getSessionType()) {
            case INIT_SESSION:
                initSession(startDlg.getServerAddress(), startDlg.getDirectoryPath());
                // TODO загрузить папку на сервер
                // Здесь поддержка сессии вполне может быть реализована
                // через фоновое присутствие (значок в трее)
                // Возможно, потребуется какое-то окно, отображающее состояние
                // подключения.
                break;
            case CONNECT_TO_SESSION:
                // TODO подключиться к имеющейся сессии
                // Здесь нужна форма NetAlbumEditorForm или что-то в этом
                // духе
                break;
        }
    }

    private void initSession(String address, String directoryPath)  {
        // 1. Определить внутреннее название папки.
        File directory = new File(directoryPath);
        String name = directory.getName();
        // 2. Подключиться.
        URI uri = URI.create(address);
        service.connectTo(uri);
        try {
            service.waitUntilConnected();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Request.InitSession req1 = new Request.InitSession();
        req1.setDirectoryName(name);
        
        service.subscribe(new Flow.Subscriber<Message>() {
            private Flow.Subscription subscription;
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1);
            }

            @Override
            public void onNext(Message item) {
                subscription.request(1);

                if (item.getType() != Message.Type.RESPONSE)
                    return;
                
                Response resp = (Response)item;
                
                if (resp.getAnswerType() == Response.Type.SESSION_CREATED) {
                    sessionId = ((Response.SessionCreated) resp).getSessionId();
                    JOptionPane.showMessageDialog(null,
                            "Session ID: " + sessionId + "\r\n" +
                                    "Directory name: " + name);

                    cfg.addSession(sessionId, address, directoryPath);
                    ClientApp.this.address = address;

                    loadImages(directory);
                }
                else if (resp.getAnswerType() == Response.Type.ERROR) {
                    StringBuilder msg = new StringBuilder();
                    msg.append(resp.getAnswerType()).append("\r\n");
                    msg.append(((Response.Error)resp).getStatus());
                    JOptionPane.showMessageDialog(null, msg, "Error", ERROR_MESSAGE);
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
            }
            
        });
        
        service.putRequest(req1);
    }
    
    private void loadImages(File directory) {
        ImageLoader loader = new ImageLoader(service, directory);
        loader.execute();
    }

    private void closeSession() {
        Request req2 = new Request();
        req2.setMethod(Request.Method.CLOSE_SESSION);
        service.putRequest(req2);
    }

    private static void guiDie(String message) {
        JOptionPane.showMessageDialog(null, message, "Ошибка", ERROR_MESSAGE);
        System.exit(-1);
    }

    private static final String NETALBUM_CONF_FALLBACK =
            System.getProperty("user.home")
                    + File.separator
                    + "/netalbum.ini";

    private static File getNetalbumIni() {
        // 1. Проверка системного свойства "netalbum.ini"
        String ini = System.getProperty("netalbum.ini", NETALBUM_CONF_FALLBACK);
        return new File(ini);
    }

    public static void main(String[] args) throws IOException {
        Configuration cfg = new Configuration(getNetalbumIni());
        ClientApp app = new ClientApp(cfg);
        app.run();
    }

    public enum SessionType {
        INIT_SESSION,
        CONNECT_TO_SESSION
    }
}
