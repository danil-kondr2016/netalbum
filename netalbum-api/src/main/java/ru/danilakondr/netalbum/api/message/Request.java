package ru.danilakondr.netalbum.api.message;

import com.fasterxml.jackson.annotation.*;
import ru.danilakondr.netalbum.api.data.ChangeCommand;
import ru.danilakondr.netalbum.api.data.ImageData;

import java.util.List;

/**
 * Класс-держатель запроса. Используется для формирования запроса на стороне
 * клиента и получения ответа на стороне сервера.
 *
 * <h2> Типы запросов </h2>
 * <h3> {@code INIT_SESSION} </h3>
 * <p> Инициализация сессии. Содержит поле {@code directoryName} (имя папки).
 * <p> В ответ сервер посылает ответ типа {@code SESSION_CREATED}.
 * <h3> {@code CONNECT_TO_SESSION} </h3>
 * <p> Подключение к сессии как просмотрщик. Содержит поле {@code sessionId}.
 * <p> В ответ сервер может послать ответ типа {@code VIEWER_CONNECTED}, если 
 * подключение произошло успешно, или ответ типа {@code NON_EXISTENT_SESSION}, 
 * если сессия не существует.
 * <h3> {@code RESTORE_SESSION} </h3>
 * <p> Подключение к сессии как инициатор. Содержит поле {@code sessionId}.
 * <p> В ответ сервер может послать ответ типа {@code SESSION_RESTORED}, если 
 * подключение произошло успешно, или ответ типа {@code NON_EXISTENT_SESSION}, 
 * если сессия не существует.
 * <h3> {@code DISCONNECT_FROM_SESSION} </h3>
 * <p> Отключение от сессии. В ответ сервер посылает 
 * {@code CLIENT_DISCONNECTED}.
 * <h3> {@code CLOSE_SESSION} </h3>
 * <p> Закрытие сессии.
 * <p> Сервер посылает всем ответ {@code SESSION_CLOSED}. Вся информация о 
 * сессии удаляется полностью.
 * <h3> {@code GET_DIRECTORY_INFO} </h3>
 * <p> Получение информации о папке.
 * <p> В ответ сервер посылает ответ типа {@code DIRECTORY_INFO}.
 * <h3> {@code DOWNLOAD_THUMBNAILS} </h3>
 * <p> Скачивание уменьшенных картинок.
 * <p> В ответ сервер посылает ответ типа {@code THUMBNAILS_ARCHIVE}.
 * <h3> {@code ADD_FILE} </h3>
 * <p> Добавление файлов. Содержит поле {@code file}, представляющее
 * собой данные о файле в виде объекта.
 * <p> В ответ сервер возвращает {@code FILE_ADDED}.
 * <h3> {@code ADD_DIRECTORY} </h3>
 * <p> Добавление директории. Содержит поле {@code directoryName}, являющееся
 * строкой, которое содержит имя папки.
 * <p> В ответ сервер возвращает {@code FILE_ADDED}.
 * <h3> {@code SYNCHRONIZE} </h3>
 * <p> Синхронизация. Содержит поле {@code changes}, представляющее собой массив
 * изменений в виде объектов.
 * <p> В ответ сервер рассылает сообщение {@code SYNCHRONIZING}, содержащее
 * копию массива изменений из запроса, отправивишему отправляет {@code SUCCESS}.
 *
 * @author Данила А. Кондратенко
 * @see Response
 * @see ChangeCommand
 * @see ImageData
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="method", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value=Request.InitSession.class, name="INIT_SESSION"),
        @JsonSubTypes.Type(value=Request.Synchronize.class, name="SYNCHRONIZE"),
        @JsonSubTypes.Type(value=Request.AddFile.class, name="ADD_FILE"),
        @JsonSubTypes.Type(value=Request.AddDirectory.class, name="ADD_DIRECTORY"),
        @JsonSubTypes.Type(value=Request.ConnectToSession.class, name="CONNECT_TO_SESSION"),
        @JsonSubTypes.Type(value=Request.RestoreSession.class, name="RESTORE_SESSION"),
        @JsonSubTypes.Type(value=Request.class, name="CLOSE_SESSION"),
        @JsonSubTypes.Type(value=Request.class, name="DISCONNECT_FROM_SESSION"),
        @JsonSubTypes.Type(value=Request.class, name="GET_DIRECTORY_INFO"),
        @JsonSubTypes.Type(value=Request.class, name="DOWNLOAD_THUMBNAILS")
})
@JsonPropertyOrder({"type", "method"})
public class Request extends Message {
    private Method method;

    @JsonPropertyOrder({"type","method","directoryName"})
    public static class InitSession extends Request {
        private String directoryName;

        public InitSession() {
            super(Method.INIT_SESSION);
        }

        public String getDirectoryName() {
            return directoryName;
        }

        public void setDirectoryName(String dirName) {
            this.directoryName = dirName;
        }
    }

    @JsonPropertyOrder({"type","method","sessionId"})
    public static class RestoreSession extends Request {
        private String sessionId;

        public RestoreSession() {
            super(Method.RESTORE_SESSION);
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    @JsonPropertyOrder({"type","method","sessionId"})
    public static class ConnectToSession extends Request {
        private String sessionId;

        public ConnectToSession() {
            super(Method.CONNECT_TO_SESSION);
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    @JsonPropertyOrder({"type","method","changes"})
    public static class Synchronize extends Request {
        private List<ChangeCommand> changes;

        public Synchronize() {
            super(Method.SYNCHRONIZE);
        }

        public List<ChangeCommand> getChanges() {
            return changes;
        }

        public void setChanges(List<ChangeCommand> changes) {
            this.changes = changes;
        }
    }

    @JsonPropertyOrder({"type","method","file"})
    public static class AddFile extends Request {
        private ImageData file;

        public AddFile() {
            super(Method.ADD_FILE);
        }

        @JsonSetter("file")
        public void setFile(ImageData data) {
            this.file = data;
        }

        @JsonGetter("file")
        public ImageData getFile() {
            return file;
        }
    }
    
    @JsonPropertyOrder({"type", "method", "fileId", "directoryName"})
    public static class AddDirectory extends Request {
        private long fileId;
        private String directoryName;
        
        public AddDirectory() {
            super(Method.ADD_DIRECTORY);
        }
        
        public AddDirectory(String dirName) {
            this();
            this.directoryName = dirName;
        }

        public long getFileId() {
            return fileId;
        }

        public void setFileId(long fileId) {
            this.fileId = fileId;
        }

        @JsonGetter("directoryName")
        public String getDirectoryName() {
            return directoryName;
        }

        @JsonSetter("directoryName")
        public void setDirectoryName(String directoryName) {
            this.directoryName = directoryName;
        }
    }

    public Request() {
        super(Message.Type.REQUEST);
    }

    public Request(Method method) {
        super(Message.Type.REQUEST);
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public enum Method {
          INIT_SESSION
        , RESTORE_SESSION
        , CONNECT_TO_SESSION
        , DISCONNECT_FROM_SESSION
        , CLOSE_SESSION
        , GET_DIRECTORY_INFO
        , ADD_FILE
        , ADD_DIRECTORY
        , DOWNLOAD_THUMBNAILS
        , SYNCHRONIZE
        ;
    }
}
