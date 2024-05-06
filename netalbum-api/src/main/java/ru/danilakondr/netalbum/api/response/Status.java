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
		return new Status(StatusId.SUCCESS, "Success");
	}
	
	static Status getMethod() {
		return new Status(StatusId.GET_METHOD, "GET method invoked");
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
		return new Status(StatusId.MESSAGE, message);
	}
}
