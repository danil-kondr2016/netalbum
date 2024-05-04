package ru.danilakondr.netalbum.api.response;

/**
 * Объект ответа, посылаемого в формате JSON.
 * <p>
 * Формат ответа:
 * <pre>
 * {"status": объект Status,
 *  "contents": объект произвольного вида
 * };
 * </pre>
 * @param <T> тип объекта
 * @see Status
 */
public class Response<T> {
	private Status status;
	private T contents;
	
	public Response() {};
	
	public Status getStatus() {
		return status;
	}
	
	public T getContents() {
		return contents;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setContents(T contents) {
		this.contents = contents;
	}
}
