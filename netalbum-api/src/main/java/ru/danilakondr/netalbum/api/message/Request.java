package ru.danilakondr.netalbum.api.message;

import com.fasterxml.jackson.annotation.*;
import ru.danilakondr.netalbum.api.data.Change;
import ru.danilakondr.netalbum.api.data.ImageData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * <p> В ответ сервер может послать ответ типа {@code SUCCESS}, если подключение
 * произошло успешно, или ответ типа {@code NON_EXISTENT_SESSION}, если сессия
 * не существует.
 * <h3> {@code RESTORE_SESSION} </h3>
 * <p> Подключение к сессии как инициатор. Содержит поле {@code sessionId}.
 * <p> В ответ сервер может послать ответ типа {@code SUCCESS}, если подключение
 * произошло успешно, или ответ типа {@code NON_EXISTENT_SESSION}, если сессия
 * не существует.
 * <h3> {@code DISCONNECT_FROM_SESSION} </h3>
 * <p> Отключение от сессии.
 * <h3> {@code CLOSE_SESSION} </h3>
 * <p> Закрытие сессии.
 * <p> Сервер посылает всем просмотрщикам ответ {@code SESSION_EXITS},
 * инициатору отправляется {@code SUCCESS}. Вся информация о сессии удаляется
 * полностью.
 * <h3> {@code GET_DIRECTORY_INFO} </h3>
 * <p> Получение информации о папке.
 * <p> В ответ сервер посылает ответ типа {@code DIRECTORY_INFO}.
 * <h3> {@code DOWNLOAD_THUMBNAILS} </h3>
 * <p> Скачивание уменьшенных картинок.
 * <p> В ответ сервер посылает ответ типа {@code THUMBNAILS_ARCHIVE}.
 * <h3> {@code ADD_IMAGES} </h3>
 * <p> Добавление изображений. Содержит поле {@code images}, представляющее
 * собой массив данных об изображениях в виде объектов.
 * <p> В ответ сервер возвращает {@code SUCCESS}.
 * <h3> {@code SYNCHRONIZE} </h3>
 * <p> Синхронизация. Содержит поле {@code changes}, представляющее собой массив
 * изменений в виде объектов.
 * <p> В ответ сервер рассылает сообщение {@code SYNCHRONIZING}, содержащее
 * копию массива изменений из запроса, отправивишему отправляет {@code SUCCESS}.
 *
 * @author Данила А. Кондратенко
 * @see Response
 * @see Change
 * @see ImageData
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="method", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value=Request.InitSession.class, name="INIT_SESSION"),
        @JsonSubTypes.Type(value=Request.Synchronize.class, name="SYNCHRONIZE"),
        @JsonSubTypes.Type(value=Request.AddImages.class, name="ADD_IMAGES"),
        @JsonSubTypes.Type(value=Request.ConnectToSession.class, name="CONNECT_TO_SESSION"),
        @JsonSubTypes.Type(value=Request.RestoreSession.class, name="RESTORE_SESSION"),
        @JsonSubTypes.Type(value=Request.class, name="CLOSE_SESSION"),
        @JsonSubTypes.Type(value=Request.class, name="DISCONNECT_FROM_SESSION"),
        @JsonSubTypes.Type(value=Request.class, name="GET_DIRECTORY_INFO"),
        @JsonSubTypes.Type(value=Request.class, name="DOWNLOAD_THUMBNAILS")
})
public class Request {
    private Type method;
    private final Map<String, Object> contents;

    public static class InitSession extends Request {
        private String directoryName;

        public InitSession() {
            super(Type.INIT_SESSION);
        }

        public String getDirectoryName() {
            return directoryName;
        }

        public void setDirectoryName(String dirName) {
            this.directoryName = dirName;
        }
    }

    public static class RestoreSession extends Request {
        private String sessionId;

        public RestoreSession() {
            super(Type.RESTORE_SESSION);
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    public static class ConnectToSession extends Request {
        private String sessionId;

        public ConnectToSession() {
            super(Type.CONNECT_TO_SESSION);
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    public static class Synchronize extends Request {
        private List<Change> changes;

        public Synchronize() {
            super(Type.SYNCHRONIZE);
        }

        public List<Change> getChanges() {
            return changes;
        }

        public void setChanges(List<Change> changes) {
            this.changes = changes;
        }
    }

    public static class AddImages extends Request {
        private List<ImageData> images;

        public AddImages() {
            super(Type.ADD_IMAGES);
        }

        @JsonSetter("images")
        public void setImages(List<ImageData> data) {
            this.images = data;
        }

        @JsonGetter("images")
        public List<ImageData> getImages() {
            return images;
        }
    }

    public Request() {
        this.contents = new HashMap<>();
    }

    public Request(Type method) {
        this.method = method;
        this.contents = new HashMap<>();
    }

    public Type getMethod() {
        return method;
    }

    public void setMethod(Type method) {
        this.method = method;
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return contents;
    }

    @JsonAnySetter
    public void setProperty(String prop, Object value) {
        this.contents.put(prop, value);
    }

    public enum Type {
          INIT_SESSION
        , RESTORE_SESSION
        , CONNECT_TO_SESSION
        , DISCONNECT_FROM_SESSION
        , CLOSE_SESSION
        , GET_DIRECTORY_INFO
        , ADD_IMAGES
        , DOWNLOAD_THUMBNAILS
        , SYNCHRONIZE
        ;
    }
}
