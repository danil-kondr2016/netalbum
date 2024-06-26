package ru.danilakondr.netalbum.api.message;

import java.util.List;

import com.fasterxml.jackson.annotation.*;
import ru.danilakondr.netalbum.api.data.ChangeCommand;
import ru.danilakondr.netalbum.api.data.ChangeInfo;
import ru.danilakondr.netalbum.api.data.FileInfo;

/**
 * Класс-держатель ответа. Используется для формирования ответа на стороне
 * сервера и получения ответа на стороне клиента.
 *
 * <h2> Формат ответа </h2>
 * <p> Ответ содержит поле {@code type}, обозначающее тип ответа, произвольные
 * поля и поля, определённые типом ответа.
 *
 * <h2> Типы ответов </h2>
 * <h3> {@code SUCCESS} </h3>
 * <p> Возвращается при успешном завершении методов {@code CONNECT_TO_SESSION},
 * {@code DISCONNECT_FROM_SESSION}, {@code RESTORE_SESSION}.
 * <h3> {@code ERROR} </h3>
 * <p> Возвращается при произвольной ошибке. Содержит поле {@code status},
 * которое обозначает код ошибки.
 * <h4> Коды ошибок </h4>
 * <table>
 *     <thead><tr><th>Код ошибки</th><th>Значение ошибки</th></tr></thead>
 *     <tbody>
 *         <tr><td>{@code INVALID_REQUEST}</td><td>Неправильный запрос</td></tr>
 *         <tr><td>{@code FILE_NOT_FOUND}</td><td>Файл не найден</td></tr>
 *         <tr><td>{@code FILE_ALREADY_EXISTS}</td><td>Файл уже существует</td></tr>
 *         <tr><td>{@code NON_EXISTENT_SESSION}</td><td>Несуществующая сессия</td></tr>
 *         <tr><td>{@code NOT_AN_INITIATOR}</td><td>Клиент не является инициатором</td></tr>
 *         <tr><td>{@code NOT_A_VIEWER}</td><td>Клиент не является просмотрщиком</td></tr>
 *         <tr><td>{@code CLIENT_NOT_CONNECTED}</td><td>Клиент не подключён</td></tr>
 *         <tr><td>{@code CLIENT_ALREADY_CONNECTED}</td><td>Клиент уже подключён</td></tr>
 *         <tr><td>{@code EXCEPTION}</td><td>Исключение</td></tr>
 *     </tbody>
 * </table>
 * <h3> {@code SESSION_CREATED} </h3>
 * <p> Возвращается при успешном создании сессии. Содержит поле
 * {@code sessionId}, в котором записан 40-символьный идентификатор сессии в
 * формате base16.
 * <h3> {@code DIRECTORY_INFO} </h3>
 * <p> Возвращается в ответ на запрос {@code GET_DIRECTORY_INFO}. Содержит поля:
 * <ul>
 * <li>{@code directoryName} - название папки;
 * <li>{@code directorySize} - размер папки;
 * <lI>{@code imageCount} - количество изображений.
 * </ul>
 * <h3> {@code THUMBNAILS_ARCHIVE} </h3>
 * <p> Возвращается в ответ на запрос {@code DOWNLOAD_THUMBNAILS}. Содержит поле
 * {@code thumbnailsZip}, содержащее zip-архив с уменьшенными картинками,
 * который закодирован в формате base64.
 * <h3> {@code SYNCHRONIZING} </h3>
 * <p> Посылается сервером при отправке запроса {@code SYNCHRONIZE}. Содержит
 * список изменений.
 * <h3> {@code FILE_ADDED} </h3>
 * <p> Посылается сервером при отправке запроса {@code ADD_FILE}. Содержит
 * данные об отправленном изображении.
 * <h3> {@code SESSION_CLOSED} </h3>
 * <p> Посылается сервером при закрытии сессии каждому, кто к ней подключён.
 * <h3> {@code SESSION_RESTORED} </h3>
 * <p> Посылается сервером при успешном восстановлении сессии.
 * <h3> {@code VIEWER_CONNECTED} </h3>
 * <p> Посылается сервером при успешном подключении просмотрщика к сессии.
 * <h3> {@code CLIENT_DISCONNECTED} </h3>
 * <p> Посылается сервером при успешном отключении от сессии.
 *
 * @author Данила А. Кондратенко
 * @see Request
 * @see Response.Error
 * @see ChangeCommand
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="answer", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value=Response.DirectoryInfo.class, name="DIRECTORY_INFO"),
        @JsonSubTypes.Type(value=Response.Synchronizing.class, name="SYNCHRONIZING"),
        @JsonSubTypes.Type(value=Response.ThumbnailsArchive.class, name="THUMBNAILS_ARCHIVE"),
        @JsonSubTypes.Type(value=Response.SessionCreated.class, name="SESSION_CREATED"),
        @JsonSubTypes.Type(value=Response.FileAdded.class, name="FILE_ADDED"),
        @JsonSubTypes.Type(value=Response.class, name="SUCCESS"),
        @JsonSubTypes.Type(value=Response.Error.class, name="ERROR"),
        @JsonSubTypes.Type(value=Response.class, name="SESSION_CLOSED"),
        @JsonSubTypes.Type(value=Response.class, name="SESSION_RESTORED"),
        @JsonSubTypes.Type(value=Response.class, name="VIEWER_CONNECTED"),
        @JsonSubTypes.Type(value=Response.class, name="CLIENT_DISCONNECTED"),
})
@JsonPropertyOrder({"type","answer"})
public class Response extends Message {
    public enum Type {
        SUCCESS, ERROR,
        SESSION_CREATED,
        SESSION_CLOSED,
        SESSION_RESTORED,
        VIEWER_CONNECTED,
        CLIENT_DISCONNECTED,
        DIRECTORY_INFO,
        THUMBNAILS_ARCHIVE,
        SYNCHRONIZING, FILE_ADDED,
    }
    private static Response SUCCESS = null;
    private Type answerType;

    public Response() {
        super(Message.Type.RESPONSE);
    }

    public Response(Type type) {
        super(Message.Type.RESPONSE);
        this.answerType = type;
    }

    public static Response success() {
        if (SUCCESS == null)
            SUCCESS = new Response(Type.SUCCESS);

        return SUCCESS;
    }

    @JsonPropertyOrder({"type", "answer", "directoryName", "directorySize", "imageCount"})
    public static class DirectoryInfo extends Response {
        private String directoryName;
        private long directorySize;
        private long imageCount;

        public DirectoryInfo() {
            super(Response.Type.DIRECTORY_INFO);
        }

        public DirectoryInfo(String directoryName, long directorySize) {
            this();
            this.directoryName = directoryName;
            this.directorySize = directorySize;
        }
        
        public DirectoryInfo(String directoryName, long directorySize, long imageCount) {
            this(directoryName, directorySize);
            this.imageCount = imageCount;
        }

        public String getDirectoryName() {
            return directoryName;
        }

        public void setDirectoryName(String directoryName) {
            this.directoryName = directoryName;
        }

        public long getDirectorySize() {
            return directorySize;
        }

        public void setDirectorySize(long directorySize) {
            this.directorySize = directorySize;
        }

        public long getImageCount() {
            return imageCount;
        }

        public void setImageCount(long imageCount) {
            this.imageCount = imageCount;
        }
    }

    @JsonPropertyOrder({"type", "answer", "changes"})
    public static class Synchronizing extends Response {
        private List<ChangeInfo> changes;

        public Synchronizing() {
            super(Response.Type.SYNCHRONIZING);
        }

        public Synchronizing(List<ChangeInfo> changes) {
            this();
            this.changes = changes;
        }

        public List<ChangeInfo> getChanges() {
            return changes;
        }

        public void setChanges(List<ChangeInfo> changes) {
            this.changes = changes;
        }
    }

    @JsonPropertyOrder({"type", "answer", "thumbnailsZip"})
    public static class ThumbnailsArchive extends Response {
        private byte[] thumbnailsZip;

        public ThumbnailsArchive() {
            super(Response.Type.THUMBNAILS_ARCHIVE);
        }

        public ThumbnailsArchive(byte[] zip) {
            this();
            this.thumbnailsZip = zip;
        }

        public byte[] getThumbnailsZip() {
            return thumbnailsZip;
        }

        public void setThumbnailsZip(byte[] thumbnailsZip) {
            this.thumbnailsZip = thumbnailsZip;
        }
    }

    @JsonPropertyOrder({"type", "answer", "sessionId"})
    public static class SessionCreated extends Response {
        private String sessionId;

        public SessionCreated() {
            super(Response.Type.SESSION_CREATED);
        }

        public SessionCreated(String sessionId) {
            this();
            this.sessionId = sessionId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    /**
     * Класс-держатель ответа типа {@code ERROR}.
     * <h2>Коды ошибок</h2>
     * <h3>{@code INVALID_REQUEST}</h3>
     * <p>Неверный запрос. Содержит поле {@code message}.
     * <h3>{@code FILE_NOT_FOUND}</h3>
     * <p>Файл не найден. Содержит поле {@code fileName}.
     * <h3>{@code FILE_ALREADY_EXISTS}</h3>
     * <p>Файл уже существует. Содержит поле {@code fileName}.
     * <h3>{@code NON_EXISTENT_SESSION}</h3>
     * <p>Несуществующая сессия. Содержит поле {@code sessionId}.
     * <h3>{@code NOT_AN_INITIATOR}</h3>
     * <p>Клиент не является инициатором сессии.
     * <h3>{@code NOT_A_VIEWER}</h3>
     * <p>Клиент не является просмотрщиком.
     * <h3>{@code CLIENT_NOT_CONNECTED}</h3>
     * <p>Клиент не подключён.
     * <h3>{@code CLIENT_ALREADY_CONNECTED}</h3>
     * <p>Клиент уже подключён.
     * <h3>{@code INITIATOR_ALREADY_CONNECTED}</h3>
     * <p>Инициатор уже подключён.
     * <h3>{@code EXCEPTION}</h3>
     * <p>Исключение. Содержит поле {@code message}.
     */
    @JsonPropertyOrder({"type", "answer", "status"})
    public static class Error extends Response {
        private Status status;

        public enum Status {
            INVALID_REQUEST,
            FILE_NOT_FOUND,
            FILE_ALREADY_EXISTS,
            NON_EXISTENT_SESSION,
            NOT_AN_INITIATOR,
            NOT_A_VIEWER,
            CLIENT_NOT_CONNECTED,
            CLIENT_ALREADY_CONNECTED,
            INITIATOR_ALREADY_CONNECTED,
            EXCEPTION
        }

        public Error() {
            super(Response.Type.ERROR);
        }

        public Error(Status status) {
            this();
            this.status = status;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status type) {
            this.status = type;
        }
    }

    @JsonGetter("answer")
    public Type getAnswerType() {
        return answerType;
    }

    @JsonSetter("answer")
    public void setAnswerType(Type type) {
        this.answerType = type;
    }

    @JsonPropertyOrder({"type", "answer", "file"})
    public static class FileAdded extends Response {
        private FileInfo file;

        public FileAdded() {
            super(Response.Type.FILE_ADDED);
        }

        public FileAdded(FileInfo file) {
            super(Response.Type.FILE_ADDED);
            this.file = file;
        }

        public FileInfo getFile() {
            return file;
        }

        public void setFile(FileInfo image) {
            this.file = image;
        }
    }
}
