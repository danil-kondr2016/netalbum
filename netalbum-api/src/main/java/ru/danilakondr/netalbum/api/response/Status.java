package ru.danilakondr.netalbum.api.response;

/**
 * Объект статуса ответа.
 * <p>
 * Формат объекта:
 * <pre>
 * {"id": код ответа,
 *  "message": строка-сообщение
 * }
 * </pre>
 */
public class Status {
	public static final int STATUS_ID_SUCCESS = 1;
	public static final int STATUS_ID_GET = 0;
	public static final int STATUS_ID_INVALID_METHOD = -1;
	public static final int STATUS_ID_INVALID_ARGUMENT = -2;
	public static final int STATUS_ID_SQL_ERROR = -3;
	
	public static final Status STATUS_SUCCESS = new Status(STATUS_ID_SUCCESS, "Success");
	public static final Status STATUS_GET = new Status(STATUS_ID_GET, "GET method invoked");
	
	private int id;
	private String message;
	
	public Status() {}
	
	public Status(int id, String message) {
		this.id = id;
		this.message = message;
	}
	
	public int getId() {
		return id;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
