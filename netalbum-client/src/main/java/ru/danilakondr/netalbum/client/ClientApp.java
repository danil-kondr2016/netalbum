package ru.danilakondr.netalbum.client;

import ru.danilakondr.netalbum.api.request.Request;
import ru.danilakondr.netalbum.api.response.Response;
import ru.danilakondr.netalbum.api.response.Status;
import ru.danilakondr.netalbum.client.connect.ConnectToServerTask;
import ru.danilakondr.netalbum.client.connect.ResponseListener;
import ru.danilakondr.netalbum.client.connect.SendRequestTask;
import ru.danilakondr.netalbum.client.gui.StartDialog;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.WebSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class ClientApp {
    private final Configuration cfg;
    private BlockingQueue<Response> responseQueue;
    private ResponseListener listener;
    private WebSocket socket;

    public ClientApp(Configuration cfg) {
        this.cfg = cfg;
        this.responseQueue = new ArrayBlockingQueue<>(1000);
        this.listener = new ResponseListener();
        this.listener.setResponseQueue(responseQueue);
    }

    public void run() {
        if (cfg.containsSessions()) {
            // TODO предложить пользователю восстановить сессии
        }

        String defaultURL = cfg.getDefaultURL();
        StartDialog startDlg = new StartDialog(defaultURL);
        startDlg.pack();
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
        JOptionPane optPane = new JOptionPane();
        optPane.setMessageType(ERROR_MESSAGE);
        optPane.setMessage("Waiting for " + uri);

        JDialog dlg = optPane.createDialog("Please wait");

        ConnectToServerTask connect = new ConnectToServerTask(uri, listener);
        connect.addPropertyChangeListener(new SwingWorkerCompletionWaiter(dlg));
        connect.execute();
        try {
            socket = connect.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        // 3. Послать запрос ключа сессии
        Request.InitSession req1 = new Request.InitSession();
        req1.setDirectoryName(name);

        SendRequestTask send1 = new SendRequestTask(listener, socket, req1);
        send1.execute();

        String sessionId;
        try {
            Response resp1 = responseQueue.take();
            if (resp1.getStatus() == Status.SESSION_CREATED) {
                sessionId = ((Response.SessionCreated) resp1).getSessionId();
                JOptionPane.showMessageDialog(null, sessionId);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 4. СТРОГО для теста: закрыть сессию

        Request req2 = new Request();
        req2.setMethod(Request.Type.CLOSE_SESSION);

        SendRequestTask send2 = new SendRequestTask(listener, socket, req2);
        send2.execute();
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
}
