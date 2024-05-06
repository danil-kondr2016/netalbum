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
	private static final Status STATUS_SUCCESS;
	private static final Status STATUS_GET_METHOD;
	static {
		STATUS_SUCCESS = new Status(StatusId.SUCCESS, "Success");
		STATUS_GET_METHOD = new Status(StatusId.GET_METHOD, "GET method invoked");
	}
	
	private StatusId id;
	private String message;
	
	public Status() {}
	
	public Status(StatusId id, String message) {
		this.id = id;
		this.message = message;
	}
	
	public StatusId getId() {
		return id;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setId(StatusId id) {
		this.id = id;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	private static String errorMessage(String mandatory, String optional) {
		if (optional != null && !optional.isBlank())
			return mandatory + ": " + optional;
		return mandatory;
	}
	
	static Status success() {
		return STATUS_SUCCESS;
	}
	
	static Status getMethod() {
		return STATUS_GET_METHOD;
	}
	
	static Status invalidMethod(String method) {
		return new Status(StatusId.INVALID_METHOD, errorMessage("Invalid method", method));
	}
	
	static Status invalidArgument(String reason) {
		return new Status(StatusId.INVALID_ARGUMENT, errorMessage("Invalid argument", reason));
	}
	
	static Status sqlError(String reason) {
		return new Status(StatusId.SQL_ERROR, errorMessage("SQL request error", reason));
	}
	
	static Status message(String message) {
		return new Status(StatusId.SUCCESS, message);
	}
	
	static Status exception(Throwable e) {
		return new Status(StatusId.EXCEPTION, e.toString());
	}
	
	static Status invalidRequest(String reason) {
		return new Status(StatusId.INVALID_REQUEST, errorMessage("Invalid request", reason));
	}
}
